package ua.softserve.academy.kv030.authservice.services.httpclient;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


/**
 * HttpClient interface for HttpClient service
 *
 * @author Michael Yablon, Artem Zakharov
 * @version 1.0
 * @since 04-11-2017
 */

public interface HttpClient {

    /**
     * Method that forms MultipartFile and create Request to FileService endpoint
     *
     * @param file MultipartFile to store in cloud storage
     * @return ResponseEntity<String> with HttpStatus representation
     * @throws IOException
     */
    ResponseEntity<String> uploadFile(MultipartFile file, String uuid) throws IOException;

    /**
     * Method that connects to FileService
     * and downloads file from cloud storage by Long id
     *
     * @param id File id
     * @return HttpStatus String representation(temporarily)
     */
    byte[] downloadFileBytes(String id);

    /**
     * Delete file from storage
     *
     * @param id id of file that will be deleted
     * @return <code>true</code> if file was deleted succesfully
     * <code>false</code> otherwise
     */
    ResponseEntity<Void> deleteFile(String id);


    ResponseEntity<String> getUploadSizeStatistics(long userId);

    ResponseEntity<String> getUploadDownloadRate(long userId);


    /**
     * Method that connects to FileService
     * and downloads file from cloud storage by Long id
     *
     * @param id File id
     * @return HttpStatus String representation(temporarily)
     */
    String downloadFile(String id);
}
