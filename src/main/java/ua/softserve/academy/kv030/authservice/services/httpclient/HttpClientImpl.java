package ua.softserve.academy.kv030.authservice.services.httpclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.*;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.IOUtils;
import ua.softserve.academy.kv030.authservice.utils.JwtUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * The UploadFileImpl class
 * Methods for upload/download from FileService
 * using {@link RestTemplate} to create request to endpoints
 *
 *
 * @author  Michael Yablon, Artem Zakharov
 * @version 1.0
 * @since   04-11-2017
 */

@Service
public class HttpClientImpl implements HttpClient {

    @Autowired
    private JwtUtil jwtUtil;

    private static final String AUTH_HEADER = "X-AUTH";
    private String fileServiceUrl;

    @Value("${statservice.url}")
    private String statisticServiceUrl;

    private byte[] bytes = "Test".getBytes();

    @Autowired
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientImpl.class);

    private RestTemplate restTemplate;

    @Autowired
    public HttpClientImpl(@Value("${fileservice.url}")String fileServiceUrl, RestTemplate restTemplate) {
        this.fileServiceUrl = fileServiceUrl;
        this.restTemplate = restTemplate;
    }

    private void logElapsedTime(String messagePrefix, long start) {
        LOGGER.info("{} Http call took {} ms.", messagePrefix, System.currentTimeMillis() - start);
    }

    @Override
    public ResponseEntity<String> uploadFile(MultipartFile multipartFile, String uuid) throws IOException {

        final long start = System.currentTimeMillis();

        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();

        map.add("file", new MultipartFileResource(multipartFile));
        map.add("fileId", uuid);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add(AUTH_HEADER, jwtUtil.generateToken("user.100@i.ua", "ADMIN"));
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);

        try {
            logElapsedTime("Success", start);
            return restTemplate.exchange(fileServiceUrl + "/files", HttpMethod.POST, requestEntity, String.class);
        } catch (HttpClientErrorException e) {
            logElapsedTime("HttpClientErrorException", start);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Cacheable( value = "encryptedFiles", key="#id")
    @Override
    public byte[] downloadFileBytes(String id) {
        final long start = System.currentTimeMillis();

        RequestCallback requestCallback = request -> {
            request.getHeaders()
                    .setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));
            request.getHeaders().add(AUTH_HEADER, jwtUtil.generateToken("user.100@i.ua", "ADMIN"));
        };

        ResponseExtractor<byte[]> responseExtractor =
                response -> IOUtils.readFully(response.getBody(), -1, false);
        try {
            logElapsedTime("Success", start);
            return restTemplate.execute(fileServiceUrl + "files/" + id,
                    HttpMethod.GET, requestCallback, responseExtractor);
        } catch (HttpClientErrorException e) {
            logElapsedTime("HttpClientErrorException", start);
            return new byte[0];
        }
    }

    @CacheEvict( value = "encryptedFiles", key="#id")
    @Override
    public ResponseEntity<Void> deleteFile(String id) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTH_HEADER, jwtUtil.generateToken("user.100@i.ua", "ADMIN"));
        try {
            LOGGER.info("deleting file");
            return restTemplate.exchange(fileServiceUrl + "files/" + id, HttpMethod.DELETE, new HttpEntity<>(headers), Void.class);
        }
        catch (RestClientException e){
            LOGGER.info("File delete failed");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> getUploadSizeStatistics(long userId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            return restTemplate.exchange(statisticServiceUrl + "stat/size/" + userId, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        } catch (RestClientException e) {
            LOGGER.info("Statistic failed");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<String> getUploadDownloadRate(long userId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            return restTemplate.exchange(statisticServiceUrl + "updownrate/" + userId, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        } catch (RestClientException e) {
            LOGGER.info("Statistic failed");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * MultipartFileResource inner class that attaches OriginalFileName to MultipartFile
     */
    private class MultipartFileResource extends ByteArrayResource {

        private String filename;

        MultipartFileResource(MultipartFile multipartFile) throws IOException {
            super(multipartFile.getBytes());
            this.filename = multipartFile.getOriginalFilename();
        }

        @Override
        public String getFilename() {
            return this.filename;
        }
    }

    @Override
    public String downloadFile(String id) {

        final long start = System.currentTimeMillis();

        RequestCallback requestCallback = request -> {
            request.getHeaders()
                    .setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));
            request.getHeaders().add(AUTH_HEADER, jwtUtil.generateToken("user.100@i.ua", "ADMIN"));
        };

        ResponseExtractor<String> responseExtractor = response -> {

            String filename = response.getHeaders().get("filename").get(0);

            Path path = Paths.get("src/test/resources/" + filename);

            Files.copy(response.getBody(), path);

            return String.valueOf(response.getRawStatusCode());
        };

        try {
            logElapsedTime("Success", start);
            return restTemplate.execute(fileServiceUrl + "files/" + id,
                    HttpMethod.GET, requestCallback, responseExtractor);
        } catch (HttpClientErrorException e) {
            logElapsedTime("HttpClientErrorException", start);
            return HttpStatus.BAD_REQUEST.toString();
        }
    }
}


