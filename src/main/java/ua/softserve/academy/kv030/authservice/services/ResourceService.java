package ua.softserve.academy.kv030.authservice.services;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import ua.softserve.academy.kv030.authservice.api.model.FileMetadataDTO;
import ua.softserve.academy.kv030.authservice.api.model.FilesFilterDTO;
import ua.softserve.academy.kv030.authservice.entity.Resource;
import ua.softserve.academy.kv030.authservice.entity.User;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service interface, which represents business-logic methods of
 * resource creation/sharing/deleting etc.
 * @author Tanya Ohotnik
 * @since 2017-11-15
 */
public interface ResourceService {
    /**
     * @param uuid unique resource id
     * @return file url to download it exactly from service
     */

    String getFileURL(String uuid);

    byte[] downloadFile(String uuid, Optional<String> desktop);

    String getFileNameByUUID(String uuid);

    Resource getFileByUUID(String uuid);
    /**
     * add new resource id database (only metadata) when it already encrypted and key existed
     *
     * @param fileMetadataDTO
     * @return modified FileMetadataDTO, if metadata was added
     */
    FileMetadataDTO addResourceMetadata(FileMetadataDTO fileMetadataDTO);

    /**
     * generate key, encrypt file with key and upload it in file service, if upload successfull
     * then add new resource id database (only metadata)
     *
     * @param fileMetadataDTO - resource metadata
     * @param multipartFile   - multipart contains file to upload it to file service
     * @return modified FileMetadataDTO, if file uploaded and metadata saved
     * @throws IOException if can`t upload file to fileservice
     */
    FileMetadataDTO addResourceAndLoadToFileService(FileMetadataDTO fileMetadataDTO, MultipartFile multipartFile) throws IOException, NoSuchAlgorithmException;

    /**
     * delete resource from db
     *
     * @param ownerId
     * @param fileId
     * @return true, if resource was deleted
     */
    boolean deleteResource(Long ownerId, Long fileId);

    /**
     * set to entity appropriate permission from db
     *
     * @param fileMetadataDTO - resource metadata
     * @param userIds         - IDs of users who have access to file in case of "LIST_OF_USERS" permission,
     *                        in case of "ALL_USERS" set is null
     * @return true if permission successfully sets
     */
    boolean shareExistingResource(FileMetadataDTO fileMetadataDTO, @Nullable Set<Long> userIds);

    /**
     * check is resource expired, if yes, delete if from file service and
     * delete file metadata from db
     *
     * @param uuid
     * @return true, if resource expired
     */
    boolean isResourceExpired(String uuid);

    /**
     * check is passed owner is owner of resource
     *
     * @param fileMetadataDTO
     * @return true, if userId is owner`s
     */
    boolean isUserOwner(Long userId, FileMetadataDTO fileMetadataDTO);

    /**
     * check is passed owner is owner of resource
     *
     * @param userId
     * @param resourceId
     * @return true, if userId is owner`s
     */
    boolean isUserOwner(Long userId, Long resourceId);

    /**
     * check is user can read file according to permissions
     *
     * @param fileMetadataDTO
     * @return true, if user can read file
     */
    boolean isUserCanReadFile(Long userId, FileMetadataDTO fileMetadataDTO);

    boolean isUserCanReadFile(String email, String uuid);

    ResponseEntity<FileMetadataDTO> checkFileSizeAndUpload(FileMetadataDTO fileMetadata, MultipartFile file, Optional<String> web) throws IOException, NoSuchAlgorithmException;

    List<FileMetadataDTO> getUserFilesList(Long userId);

    FileMetadataDTO getFileMetadata(Long resourceId);

    List<FileMetadataDTO> listFilesByFilter(FilesFilterDTO filterDTO);
}
