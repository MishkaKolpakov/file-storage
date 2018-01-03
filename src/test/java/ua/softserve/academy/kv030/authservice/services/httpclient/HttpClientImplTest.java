package ua.softserve.academy.kv030.authservice.services.httpclient;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import ua.softserve.academy.kv030.authservice.AuthServiceApplication;

import java.io.IOException;
import java.util.Random;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = AuthServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HttpClientImplTest {

    private MockMultipartFile multipartFile;
    private MockRestServiceServer mockServer;
    private HttpClientImpl uploadFile;

    @Value("${fileservice.url}")
    private String URL_PREFIX;

    @Value("${statservice.url}")
    private String STAT_SERVICE_URL;

    @Before
    public void setUp() {
        multipartFile = new MockMultipartFile("file", "hello" + new Random().nextInt(100) + ".txt", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());
        RestTemplate restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        uploadFile = new HttpClientImpl(URL_PREFIX, restTemplate);
    }
    
    @Ignore
    @Test
    public void uploadFile() throws Exception {

        mockServer.expect(requestTo(URL_PREFIX + "files"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess());

        ResponseEntity<String> response = uploadFile.uploadFile(multipartFile, "");

        mockServer.verify();
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    }

    @Ignore
    @Test
    public void uploadFileFail() throws Exception {

        mockServer.expect(requestTo(URL_PREFIX + "files"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        ResponseEntity<String> response = uploadFile.uploadFile(multipartFile, "");

        mockServer.verify();
        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }

    @Ignore
    @Test
    public void downloadFile() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("filename", multipartFile.getOriginalFilename());

        mockServer.expect(requestTo(URL_PREFIX + "files/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(new ByteArrayResource(multipartFile.getBytes()) {
                    @Override
                    public String getFilename() {
                        return super.getFilename();
                    }
                }, MediaType.MULTIPART_FORM_DATA).headers(headers));

        String response = uploadFile.downloadFile("1");

        mockServer.verify();
        assertThat("200", equalTo(response));
    }

    @Ignore
    @Test
    public void downloadFileBytes() throws Exception {
        mockServer.expect(requestTo(URL_PREFIX + "files/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(new ByteArrayResource(multipartFile.getBytes()) {
                    @Override
                    public String getFilename() {
                        return super.getFilename();
                    }
                }, MediaType.MULTIPART_FORM_DATA));

        byte[] response = uploadFile.downloadFileBytes("1");

        mockServer.verify();
        assertNotNull(response);
        assertEquals("Hello, World!", new String(response));
    }

    @Ignore
    @Test
    public void downloadFileFail() throws Exception {

        mockServer.expect(requestTo(URL_PREFIX + "files/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withBadRequest());

        String response = uploadFile.downloadFile("1");

        mockServer.verify();
        assertThat("400", equalTo(response));
    }

    @Ignore
    @Test
    public void deleteFile() throws Exception {
        mockServer.expect(requestTo(URL_PREFIX + "files/1"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess());

        ResponseEntity<Void> response = uploadFile.deleteFile("1");
        mockServer.verify();
        assertEquals("200", response.getStatusCode().toString());
    }

    @Ignore
    @Test
    public void deleteFileFail() throws Exception {
        mockServer.expect(requestTo(URL_PREFIX + "files/0"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withBadRequest());

        ResponseEntity<Void> response = uploadFile.deleteFile("0");
        mockServer.verify();
        assertEquals("400", response.getStatusCode().toString());
    }

    @Ignore
    @Test
    public void uploadFileReal() throws IOException {

        ResponseEntity<String> response = uploadFile.uploadFile(multipartFile, "");

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    }

    @Ignore
    @Test
    public void downloadRealFile() {

        String response = uploadFile.downloadFile("1");

        assertThat("200", equalTo(response));
    }

}