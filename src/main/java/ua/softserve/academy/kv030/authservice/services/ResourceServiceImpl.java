package ua.softserve.academy.kv030.authservice.services;


import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import twitter4j.*;
import ua.softserve.academy.kv030.authservice.api.model.FileMetadataDTO;
import ua.softserve.academy.kv030.authservice.api.model.FilesFilterDTO;
import ua.softserve.academy.kv030.authservice.api.model.RoleDTO;
import ua.softserve.academy.kv030.authservice.converter.ResourceFilterConverter;
import ua.softserve.academy.kv030.authservice.converter.ResourceConverter;
import ua.softserve.academy.kv030.authservice.dao.*;
import ua.softserve.academy.kv030.authservice.entity.Permission;
import ua.softserve.academy.kv030.authservice.entity.Resource;
import ua.softserve.academy.kv030.authservice.entity.SecretKey;
import ua.softserve.academy.kv030.authservice.entity.User;
import ua.softserve.academy.kv030.authservice.exceptions.EntityNotFoundException;
import ua.softserve.academy.kv030.authservice.exceptions.FileNotFoundException;
import ua.softserve.academy.kv030.authservice.exceptions.ResourceNotFoundException;
import ua.softserve.academy.kv030.authservice.exceptions.UserNotFoundException;
import ua.softserve.academy.kv030.authservice.services.encryption.CipherServiceImpl;
import ua.softserve.academy.kv030.authservice.services.httpclient.CustomMultipartFile;
import ua.softserve.academy.kv030.authservice.services.httpclient.HttpClient;
import ua.softserve.academy.kv030.authservice.services.httpclient.HttpClientImpl;
import ua.softserve.academy.kv030.authservice.services.mail.EmailServiceImpl;
import ua.softserve.academy.kv030.authservice.services.mail.EmailType;
import ua.softserve.academy.kv030.authservice.values.Constants;

import javax.annotation.Nullable;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ResourceServiceImpl implements ResourceService {
    public static final String CANT_FIND_RESOURCE = "can`t find resource with passed id";
    private String fileServiceUrl;
    private String authServiceUrl;
    private int cipherFileSize;
    private Logger logger;
    private HttpClient httpClient;
    private ResourceDAO resourceDAO;
    private UserDAO userDAO;
    private PermissionDAO permissionDAO;
    private CipherServiceImpl cipherService;
    private EmailServiceImpl emailService;
    private TwitterFactory twitterFactory;

    @Autowired
    public ResourceServiceImpl(@Value("${fileservice.url}") String fileServiceUrl,
                               @Value("${application.url}") String authServiceUrl,
                               @Value("${fileservice.int.cipher-file-size}") int cipherFileSize,
                               Logger logger,
                               HttpClient httpClient, CipherServiceImpl cipherService,
                               EmailServiceImpl emailService, ResourceDAO resourceDAO,
                               UserDAO userDAO, PermissionDAO permissionDAO,
                               TwitterFactory twitterFactory) {

        this.logger = logger;
        this.httpClient = httpClient;
        this.resourceDAO = resourceDAO;
        this.userDAO = userDAO;
        this.fileServiceUrl = fileServiceUrl;
        this.authServiceUrl = authServiceUrl;
        this.cipherService = cipherService;
        this.emailService = emailService;
        this.permissionDAO = permissionDAO;
        this.twitterFactory = twitterFactory;
        //conversion from megabytes to bytes
        this.cipherFileSize = cipherFileSize * 1024 * 1024;
    }


    @Override
    public String getFileURL(String uuid) {
        Optional<Resource> optionalResource = resourceDAO.findElementByUUID(uuid);
        if (!optionalResource.isPresent()) {
            logger.info(CANT_FIND_RESOURCE);
            throw new ResourceNotFoundException(uuid);
        }
        Resource dbResource = optionalResource.get();
        //file size in db stores in bytes
        if (dbResource.getSize() > cipherFileSize) {
            return getBigFileURL(dbResource);
        } else
            return getSmallFileURL(dbResource);

    }

    @Override
    public byte[] downloadFile(String uuid, Optional<String> desktop) {
        Optional<Resource> optionalResource = resourceDAO.findElementByUUID(uuid);
        if(isResourceExpired(uuid)){
            logger.info(CANT_FIND_RESOURCE);
            throw new ResourceNotFoundException(uuid);
        }
        if (!optionalResource.isPresent()) {
            logger.info(CANT_FIND_RESOURCE);
            throw new ResourceNotFoundException(uuid);
        }
        Resource dbResource = optionalResource.get();

        byte[] fileBytes = httpClient.downloadFileBytes(uuid);
        if(fileBytes.length==0) {
            logger.error("File downloaded from file service is empty");
            throw new FileNotFoundException("File downloaded from file service is empty",uuid);
        }

        if (fileBytes.length <= cipherFileSize && desktop.isPresent()){
            logger.info(Arrays.toString(fileBytes));
            return fileBytes;
        }
        else {
            fileBytes = cipherService.decrypt(fileBytes,dbResource.getSecretKey().getKey());
            logger.info(Arrays.toString(fileBytes));
            return fileBytes;
        }
    }

    @Override
    public String getFileNameByUUID(String uuid) {
        Optional<Resource> optionalResource = resourceDAO.findElementByUUID(uuid);
        if (!optionalResource.isPresent()) {
            logger.info(CANT_FIND_RESOURCE);
            throw new ResourceNotFoundException(uuid);
        }
        Resource dbResource = optionalResource.get();
        return  dbResource.getFileName();
    }

    @Override
    public Resource getFileByUUID(String uuid) {
        Optional<Resource> optionalResource = resourceDAO.findElementByUUID(uuid);
        if (!optionalResource.isPresent()) {
            logger.info(CANT_FIND_RESOURCE);
            throw new ResourceNotFoundException(uuid);
        }
        return optionalResource.get();
    }
    private String getSmallFileURL(Resource resource) {
        return fileServiceUrl + Constants.fileServiceFileEndpoint + resource.getLinkToFile();
    }

    private String getBigFileURL(Resource resource) {
        return authServiceUrl + Constants.authServiceFileEndpoint + resource.getLinkToFile();
    }

    @Override
    public FileMetadataDTO addResourceMetadata(FileMetadataDTO fileMetadataDTO) {
        if (fileMetadataDTO == null) {
            logger.info("inserted resource is null");
            return null;
        }
        Resource resource = ResourceConverter.convertToEntity(fileMetadataDTO);
        //key is required and object already created in converter
        if (resource.getSecretKey().getKey() == null) {
            logger.info("Required key is null");
            return null;
        }
        resource.getSecretKey().setStatus(true);
        resource.setLinkToFile(fileMetadataDTO.getFileUUID());

        // Setting owner
        Optional<User> userOptional = userDAO.findElementById(fileMetadataDTO.getOwnerId());
        if (userOptional.isPresent()) {
            User owner = userOptional.get();
            resource.setOwner(owner);
        } else throw new UserNotFoundException("No such user");

        if(fileMetadataDTO.getPermission() != null) {
            Optional<Permission> permissionOptional = permissionDAO.findOneByFieldEqual("permission", fileMetadataDTO.getPermission().toString());
            if (permissionOptional.isPresent()) {
                Permission permissionFromDB = permissionOptional.get();
                resource.setPermission(permissionFromDB);
            } else throw new EntityNotFoundException("No such permission in database: " + fileMetadataDTO.getPermission().name());
        }

        Resource newResource = resourceDAO.insert(resource);
        logger.info("new resource insertion");
        return ResourceConverter.convertToDto(newResource);
    }

    @Override
    public FileMetadataDTO addResourceAndLoadToFileService(FileMetadataDTO fileMetadataDTO, MultipartFile multipartFile) throws IOException, NoSuchAlgorithmException {
        if (fileMetadataDTO == null) {
            logger.info("resource to insert is null");
            return null;
        }
        if (multipartFile == null || multipartFile.getBytes() == null) {
            logger.info("file to insert is null");
            return null;
        }
        //multipart file->bytes->encrypt->file->multipart
        //encrypt file
        String key = cipherService.generateKey();
        fileMetadataDTO.setKey(key);
        byte[] fileBytes = cipherService.encrypt(multipartFile.getBytes(), key);
        logger.info("resource encrypting");

        //wrap encrypted file in multipart
        String fileName = multipartFile.getOriginalFilename();

        CustomMultipartFile customMultipartFile = new CustomMultipartFile(fileBytes, fileName);
        try {
            customMultipartFile.transferTo(customMultipartFile.getFile());
        } catch (IllegalStateException e) {
            logger.info("IllegalStateException : " + e);
        } catch (IOException e) {
            logger.info("IOException : " + e);
        }

        try {
            ResponseEntity<String> response = httpClient.uploadFile(customMultipartFile, fileMetadataDTO.getFileUUID());
            if (response.getStatusCode() == HttpStatus.BAD_REQUEST) {
                logger.info("can't upload file to fileservice");
                return null;
            } else {
                logger.info("file uploading to fileservice successful");
            }
        } catch (IOException e) {

            logger.info("Can't upload file to fileService");
            e.printStackTrace();
            return null;
        }
        return addResourceMetadata(fileMetadataDTO);

    }

    @Override
    public boolean deleteResource(Long ownerId, Long fileId) {

        //check is here resource with passed id
        Optional<Resource> optionalResource = resourceDAO.findElementById(fileId);
        if (!optionalResource.isPresent()) {
            logger.info(CANT_FIND_RESOURCE);
            return false;
        }
        Resource resource = optionalResource.get();
        User user = resource.getOwner();
        if (ownerId != user.getUserId() && !user.getRole().getRoleName().equals(RoleDTO.ADMIN.toString())) {
            return false;
        }
        //delete file from file service
        logger.info("Sending request to delete file by link: {}", resource.getLinkToFile());
        ResponseEntity<Void> response = httpClient.deleteFile(resource.getLinkToFile());
        logger.info("Response code is: {}", response.getStatusCode());
        if (response.getStatusCode() == HttpStatus.OK) {
            logger.info("file deleting from fileservice");
            resourceDAO.delete(resource);
            logger.info("file metadata deleting from authservice");
            return true;
        } else {
            logger.info("can`t delete file from fileservice");
            return false;
        }

    }

    @Override
    //@Transactional
    public boolean shareExistingResource(FileMetadataDTO fileMetadataDTO, @Nullable Set<Long> userIds) {
        if (fileMetadataDTO == null || fileMetadataDTO.getResourceId() == null || fileMetadataDTO.getPermission() == null) {
            logger.info("No id/permission passed in fileMetadataDTO or fileMetadataDTO is null");
            return false;
        }
        //check is here resource with passed id
        Resource newResource = ResourceConverter.convertToEntity(fileMetadataDTO);
        Optional<Resource> optionalResource = resourceDAO.findElementById(newResource.getResourceId());
        if (!optionalResource.isPresent()) {
            logger.info("can`t find resource with passed id");
            return false;
        }
        Resource dbResource = optionalResource.get();
        Optional<Permission> optionalPermission = permissionDAO.findOneByFieldEqual("permission", fileMetadataDTO.getPermission().name());
        if (!optionalPermission.isPresent()) {
            logger.info("can`t find permission with passed name");
            return false;
        }
        dbResource.setPermission(optionalPermission.get());

        logger.info(fileMetadataDTO.getPermission().name());
        logger.info("set new permission into resource");
        switch (dbResource.getPermission().getPermission()) {
            //everything is ok, need no additional actions
            case "ALL_USERS":
                resourceDAO.update(dbResource);
                List<User> users = userDAO.findAllUsersByStatus(true);
                sendEmails(new HashSet<>(users), dbResource);
                try {
                    Twitter twitter = twitterFactory.getInstance();
                    IDs followerIds = twitter.getFollowersIDs("SuperCloudKV", -1);
                    long[] ids = followerIds.getIDs();
                    for(long id : ids) {
                        twitter4j.User user = twitter.showUser(id);
                        String username = user.getScreenName();
                        DirectMessage message = twitter.sendDirectMessage(username,
                                String.format("Dear %s, %s shared with you %s. You can download file from our web-service using %s link",
                                        username, dbResource.getOwner().getFirstName() + dbResource.getOwner().getLastName(),
                                        dbResource.getFileName(), dbResource.getLinkToFile()));
                        logger.info("Messages are successfully sent");
                    }
                }catch (TwitterException exc){
                    logger.info("Twitter interaction failed");
                }
                return true;
            //need to set list of users, who can read this file
            case "LIST_OF_USERS":
                Set<User> permittedUsers = userIds.stream()
                        .map(s -> userDAO.findElementById((s)))
                        .filter(optional -> optional.isPresent() && optional.get().isStatus())
                        .map(optional -> optional.get()).collect(Collectors.toSet());
                dbResource.setUsers(permittedUsers);
                permittedUsers.forEach(user -> user.addResource(dbResource));
                if (resourceDAO.update(dbResource) != null && permittedUsers != null) {
                    sendEmails(permittedUsers, dbResource);
                    return true;
                } else return false;
            default:
                return false;
        }
    }

    private void sendEmails(Set<User> users, Resource resource) {
        users.forEach(user -> emailService.sendMail(user, resource, EmailType.SHARED_FILE)
        );
    }

    @Override
    public boolean isResourceExpired(String uuid) {
        Optional<Resource> optionalResource = resourceDAO.findElementByUUID(uuid);
        if (!optionalResource.isPresent()) {
            logger.info(CANT_FIND_RESOURCE);
            return true;
        }
        Resource dbResource = optionalResource.get();
        SecretKey key = dbResource.getSecretKey();
        if (key == null) {
            logger.info("can`t find resource key");
            return true;
        }
        Timestamp currentTime = Timestamp.valueOf(LocalDateTime.now());
        Timestamp resourceExpirationTime = key.getExpirationDate();
        boolean isExpired = false;
        if (resourceExpirationTime.before(currentTime)) {
            isExpired = true;
            httpClient.deleteFile(dbResource.getLinkToFile());
            resourceDAO.delete(dbResource);
        }
        return isExpired;
    }

    @Override
    public boolean isUserOwner(Long userId, FileMetadataDTO fileMetadataDTO) {
        if (fileMetadataDTO == null || fileMetadataDTO.getResourceId() == null || fileMetadataDTO.getOwnerId() == null) {
            logger.info("No resourceId/ownerId passed in fileMetadataDTO or fileMetadataDTO is null");
            return false;
        }
        Optional<Resource> optionalResource = resourceDAO.findElementById(fileMetadataDTO.getResourceId());
        if (!optionalResource.isPresent()) {
            logger.info(CANT_FIND_RESOURCE);
            return false;
        }
        Resource resource = optionalResource.get();
        return resource.getOwner().getUserId() == userId;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserOwner(Long userId, Long resourceId) {
        if (userId == null || resourceId == null) {
            logger.info("resourceId/ownerId is null");
            return false;
        }
        Optional<Resource> optionalResource = resourceDAO.findElementById(resourceId);
        if (!optionalResource.isPresent()) {
            logger.info(CANT_FIND_RESOURCE);
            return false;
        }
        Resource resource = optionalResource.get();
        return userId.equals(resource.getOwner().getUserId());
    }

    @Override
    public boolean isUserCanReadFile(Long userId, FileMetadataDTO fileMetadataDTO) {

        if (fileMetadataDTO == null || fileMetadataDTO.getResourceId() == null || fileMetadataDTO.getPermission() == null) {
            logger.info("No id/permission passed in fileMetadataDTO or fileMetadataDTO is null");
            return false;
        }
        Long resourceId = fileMetadataDTO.getResourceId();
        Optional<Resource> optionalResource = resourceDAO.findElementById(resourceId);
        if (!optionalResource.isPresent()) {
            logger.info(CANT_FIND_RESOURCE);
            return false;
        }
        Resource resource = optionalResource.get();
        Optional<User> optionalUser = userDAO.findElementById(resourceId);
        if (!optionalUser.isPresent()) {
            logger.info(CANT_FIND_RESOURCE);
            return false;
        }
        User user = optionalUser.get();
        switch (fileMetadataDTO.getPermission().toString()) {
            case "ALL_USERS":
                return true;
            case "LIST_OF_USERS":
                //if user is owner, he can read file
                if (user.getUserId() == resource.getOwner().getUserId())
                    return true;
                Set<User> readableUsers = resource.getUsers();
                return readableUsers.contains(user);
            default:
                return false;
        }

    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserCanReadFile(String email, String uuid) {
        Optional<Resource> optionalResource = resourceDAO.findElementByUUID(uuid);
        if (!optionalResource.isPresent()) {
            logger.info(CANT_FIND_RESOURCE);
            return false;
        }
        Resource resource = optionalResource.get();

        Optional<User> optionalUser = userDAO.findUserByEmail(email);
        if (!optionalUser.isPresent()) {
            logger.info("can not find user with email {} ", email);
            return false;
        }
        User user = optionalUser.get();

        if(resource.getOwner().getUserId() == user.getUserId()) {
            return true;
        }
        switch (resource.getPermission().getPermission()) {
            case "ALL_USERS":
                return true;
            case "LIST_OF_USERS":
                return resource.getUsers().stream().filter(u -> u.getUserId() == user.getUserId()).findAny().isPresent();
            default:
                return false;
        }
    }

    @Override
    public ResponseEntity<FileMetadataDTO> checkFileSizeAndUpload(FileMetadataDTO fileMetadata, MultipartFile file,  Optional<String> web) throws IOException, NoSuchAlgorithmException {
        logger.warn(web.toString());
        //generate unique uuid
        String uuid = UUID.randomUUID().toString();
//        while(resourceDAO.containsUUID(uuid)){
//            uuid = UUID.randomUUID().toString();
//        }
        fileMetadata.setFileUUID(uuid);
        if (fileMetadata.getFileSize() <= cipherFileSize && !web.isPresent()) {

            ResponseEntity<String> fileServiceResponse = httpClient.uploadFile(file, fileMetadata.getFileUUID());

            if(fileServiceResponse.getStatusCode().is2xxSuccessful()) {
                addResourceMetadata(fileMetadata);
                return new ResponseEntity<>(fileMetadata, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            FileMetadataDTO fileMetadataDTO = addResourceAndLoadToFileService(fileMetadata, file);
            if(fileMetadataDTO != null){
                return new ResponseEntity<>(fileMetadata, HttpStatus.CREATED);
            }
        }
        return null;
    }

    @Override
    public List<FileMetadataDTO> getUserFilesList(Long userId) {
        logger.info("Querying files list for user: {}", userId);
        List<Resource> filesList = resourceDAO.findAllByFieldEqual("owner", userId);
        if (filesList.isEmpty()) {
            logger.info("No entries found. Returning Empty List");
            return Collections.emptyList();
        }
        logger.info("Retrieved list: {}", filesList);
        return ResourceConverter.convertToDtoList(filesList);
    }

    @Override
    public FileMetadataDTO getFileMetadata(Long resourceId) {
        Optional<Resource> resourceOptional = resourceDAO.findElementById(resourceId);
        if(resourceOptional.isPresent()) {
            return ResourceConverter.convertToDto(resourceOptional.get());
        } else {
            logger.info("No file metadata found for id " + resourceId);
            throw new EntityNotFoundException("No file metadata found for id " + resourceId);
        }
    }

    @Override
    public List<FileMetadataDTO> listFilesByFilter(FilesFilterDTO filterDTO) {
        ResourceFilterCriteria criteria = ResourceFilterConverter.convert(filterDTO);

        List<Resource> resources = resourceDAO.findAllElementsByCriteria(criteria);
        return ResourceConverter.convertToDtoList(resources);
    }
}
