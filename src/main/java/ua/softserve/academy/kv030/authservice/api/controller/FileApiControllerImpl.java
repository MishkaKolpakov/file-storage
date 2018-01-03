package ua.softserve.academy.kv030.authservice.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ua.softserve.academy.kv030.authservice.api.model.FileMetadataDTO;
import ua.softserve.academy.kv030.authservice.api.model.FilesFilterDTO;
import ua.softserve.academy.kv030.authservice.api.model.ShareDataDTO;
import ua.softserve.academy.kv030.authservice.api.model.UserDTO;
import ua.softserve.academy.kv030.authservice.entity.Statistic;
import ua.softserve.academy.kv030.authservice.exceptions.DataValidationException;
import ua.softserve.academy.kv030.authservice.exceptions.PermissionException;
import ua.softserve.academy.kv030.authservice.services.AuthService;
import ua.softserve.academy.kv030.authservice.services.ResourceService;
import ua.softserve.academy.kv030.authservice.services.rabbit.RabbitMQProducer;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.Set;

@RestController
public class FileApiControllerImpl implements FileApi {

    private Logger logger;
    private ResourceService resourceService;
    private AuthService authService;
    private RabbitMQProducer rabbitMQProducer;

    @Value("${fileservice.int.cipher-file-size}")
    private int maxFileSize;

    @Autowired
    public FileApiControllerImpl(ResourceService resourceService, Logger logger,
                                 AuthService authService, RabbitMQProducer rabbitMQProducer) {
        this.rabbitMQProducer = rabbitMQProducer;
        this.resourceService = resourceService;
        this.logger = logger;
        this.authService = authService;
    }

    @PreAuthorize("hasRole('USER') or hasRole('TECH_SUPP') or hasRole('ADMIN')")
    @Override
    public ResponseEntity<FileMetadataDTO> uploadFile(FileMetadataDTO fileMetadata, @RequestPart("file") MultipartFile file, @RequestHeader(value="web", required=false) Optional<String> web) {
        try {

            ResponseEntity<FileMetadataDTO> response = resourceService.checkFileSizeAndUpload(fileMetadata, file, web);

            if (rabbitMQProducer != null)
                rabbitMQProducer.send(new Statistic(fileMetadata.getFileUUID(), new Date(), fileMetadata.getOwnerId(), "upload", fileMetadata.getFileSize()));

            if (response != null)
                return response;

        } catch (IOException | NoSuchAlgorithmException e) {
            logger.error(e.getMessage());
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }

    @PreAuthorize("hasRole('USER') or hasRole('TECH_SUPP') or hasRole('ADMIN')")
    @Override
    public ResponseEntity<Void> shareFile(@RequestBody ShareDataDTO shareData) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!isUserOwnerOfResource(authentication,
                shareData.getFileMetadata().getResourceId())) {
            throw new PermissionException("not authorized to share current resource");
        }
        if (shareData != null) {
            FileMetadataDTO fileMetadataDTO = shareData.getFileMetadata();
            List<Long> userIdList = shareData.getPermittedUserIds();
            Set<Long> userIdSet = null;
            if (userIdList != null && !userIdList.isEmpty()) {
                userIdSet = new HashSet<>(shareData.getPermittedUserIds());
            }
            boolean isSharedSuccessfully = resourceService.shareExistingResource(fileMetadataDTO, userIdSet);
            if (isSharedSuccessfully) {

                if (rabbitMQProducer != null)
                    rabbitMQProducer.send(new Statistic(fileMetadataDTO.getFileUUID(), new Date(), fileMetadataDTO.getOwnerId(), "share", fileMetadataDTO.getFileSize()));

                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PreAuthorize("hasRole('USER') or hasRole('TECH_SUPP') or hasRole('ADMIN')")
    @Override
    public ResponseEntity<FileMetadataDTO> addFileMetadata(@RequestBody FileMetadataDTO fileMetadata) {
        FileMetadataDTO fileMetadataDTO = resourceService.addResourceMetadata(fileMetadata);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

        if (fileMetadataDTO != null) {
            return new ResponseEntity<>(httpHeaders, HttpStatus.OK);
        }

        return new ResponseEntity<>(httpHeaders, HttpStatus.BAD_REQUEST);
    }

    @PreAuthorize("hasRole('USER') or hasRole('TECH_SUPP') or hasRole('ADMIN')")
    @Override
    public ResponseEntity<org.springframework.core.io.Resource> downloadFile(@PathVariable String uuid, @RequestHeader Optional<String> desktop) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!resourceService.isUserCanReadFile(authentication.getPrincipal().toString(), uuid)) {
            throw new PermissionException("not permitted to download current resource");
        }

        byte[] fileBytes = resourceService.downloadFile(uuid, desktop);

        if (fileBytes == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        logger.info("Successfully loaded bytes from file service", fileBytes);

        ua.softserve.academy.kv030.authservice.entity.Resource res = resourceService.getFileByUUID(uuid);

        ByteArrayResource resource = new ByteArrayResource(fileBytes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", res.getMimeType());
        headers.add("Content-Disposition", String.format("attachment; filename=%s", resourceService.getFileNameByUUID(uuid)));

            headers.add("Content-Key", res.getSecretKey().getKey());

            rabbitMQProducer.send(new Statistic(uuid, new Date(), res.getOwner().getUserId(), "download", res.getSize()));

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(res.getSize())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }

    @PreAuthorize("hasRole('USER') or hasRole('TECH_SUPP') or hasRole('ADMIN')")
    @Override
    public ResponseEntity<String> fileURL(@PathVariable String uuid) {
        String fileURL = resourceService.getFileURL(uuid);
        if (fileURL != null) {
            return new ResponseEntity<>(fileURL, HttpStatus.OK);
        } else
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PreAuthorize("hasRole('USER') or hasRole('TECH_SUPP') or hasRole('ADMIN')")
    @Override
    public ResponseEntity<List<FileMetadataDTO>> listFilesByOffsetAndPageLimit(@PathVariable("userId") Long userId, Optional<Integer> offset, Optional<Integer> limit) {
        List<FileMetadataDTO> response = resourceService.getUserFilesList(userId);
        if (response.isEmpty()) {
            logger.info("Returning No Content");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        logger.info("Response ready: {}", response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER') or hasRole('TECH_SUPP') or hasRole('ADMIN')")
    @Override
    public ResponseEntity<Void> deleteFile(@PathVariable("userId") Long userId, @PathVariable("fileId") Long fileId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!isUserOwnerOfResource(authentication, fileId)) {
            throw new PermissionException("not authorized to delete current resource");
        }
        if (resourceService.deleteResource(userId, fileId)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('USER') or hasRole('TECH_SUPP') or hasRole('ADMIN')")
    @Override
    public ResponseEntity<FileMetadataDTO> getFileMetadata(@PathVariable("resourceId") Long resourceId) {
        FileMetadataDTO fileMetadataDTO = resourceService.getFileMetadata(resourceId);
        return new ResponseEntity<>(fileMetadataDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER') or hasRole('TECH_SUPP') or hasRole('ADMIN')")
    @Override
    public ResponseEntity<List<FileMetadataDTO>> listFilesByFilter(@NotNull @RequestParam(value = "filter", required = true) String filter) {

        List<FileMetadataDTO> fileMetadataDTOList;
        FilesFilterDTO filterDTO;
        ObjectMapper mapper = new ObjectMapper();
        try {
            filterDTO = mapper.readValue(filter, FilesFilterDTO.class);
        } catch (IOException e) {
            logger.error(String.format("Can not read value from json string: %s.", filter));
            throw new DataValidationException(String.format("Can not read value from json string: %s.", filter));
        }
        fileMetadataDTOList = resourceService.listFilesByFilter(filterDTO);
        if (fileMetadataDTOList.size() > 0) {
            return new ResponseEntity<>(fileMetadataDTOList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    private boolean isUserOwnerOfResource(Authentication authentication, Long fileId) {
        UserDTO userDTO = authService.findUserByEmail(authentication.getPrincipal().toString());
        if (resourceService.isUserOwner(userDTO.getId(), fileId)) {
            logger.debug("user is owner of resource");
            return true;
        } else return false;
    }
}

