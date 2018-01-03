package ua.softserve.academy.kv030.authservice.converter;

import ua.softserve.academy.kv030.authservice.api.model.FileMetadataDTO;
import ua.softserve.academy.kv030.authservice.api.model.PermissionTypeDTO;
import ua.softserve.academy.kv030.authservice.entity.Permission;
import ua.softserve.academy.kv030.authservice.entity.Resource;
import ua.softserve.academy.kv030.authservice.entity.SecretKey;
import ua.softserve.academy.kv030.authservice.entity.User;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for converting objects between Entity and DTO
 *
 * @author Tanya Ohotnik
 * @since 2017-11-15
 */
public final class ResourceConverter {
    private ResourceConverter() {
    }

    /**
     * @param date
     * @return timestamp representation of OffsetDateTime
     */
    private static Timestamp offsetDateTimeToTimestamp(OffsetDateTime date) {
        if (date == null) return null;
        return Timestamp.valueOf(date.toLocalDateTime());
    }

    /**
     * @param timestamp
     * @return OffsetDateTime representation of Timestamp
     */
    private static OffsetDateTime timestampToOffsetDateTimeTo(Timestamp timestamp) {
        if (timestamp == null) return null;
        ZoneOffset offset = OffsetDateTime.now().getOffset();
        return OffsetDateTime.of(timestamp.toLocalDateTime(), offset);
    }

    /**
     * Converts resource entity to corresponding Dto
     *
     * @param resource - entity that needed to be converted to DTO
     * @return DTO object
     */
    public static FileMetadataDTO convertToDto(Resource resource) {
        if (resource == null)
            return null;
        FileMetadataDTO dto = new FileMetadataDTO();
        dto.setResourceId(resource.getResourceId() == 0 ? null : resource.getResourceId());
        dto.setOwnerId(resource.getOwner() == null ? null : resource.getOwner().getUserId());
        dto.setFileName(resource.getFileName());
        dto.setMime(resource.getMimeType());
        dto.setFileSize(resource.getSize() == 0 ? null : resource.getSize());
        dto.setFileUUID(resource.getLinkToFile());
        if (resource.getSecretKey() != null) {
            dto.setKey(resource.getSecretKey().getKey());
            Timestamp timestamp = resource.getSecretKey().getExpirationDate();
            OffsetDateTime date = timestampToOffsetDateTimeTo(timestamp);
            dto.setExpirationTime(date);
        }
        if (resource.getPermission() != null)
            switch (resource.getPermission().getPermission()) {
                case "ALL_USERS":
                    dto.setPermission(PermissionTypeDTO.ALL_USERS);
                    break;
                case "LIST_OF_USERS":
                    dto.setPermission(PermissionTypeDTO.LIST_OF_USERS);
                    break;
                default:
                    dto.setPermission(null);
            }
        return dto;
    }

    /**
     * Converts resource DTO to corresponding Entity
     *
     * @param fileDataDTO - dto that needed to be converted to entity
     * @return entity
     */
    public static Resource convertToEntity(FileMetadataDTO fileDataDTO) {
        if (fileDataDTO == null)
            return null;
        Resource resource = new Resource();
        User user = new User();
        //set owner
        long userId = fileDataDTO.getOwnerId() == null ? 0 : fileDataDTO.getOwnerId();
        user.setUserId(userId);
        resource.setOwner(user);
        //set attributes to permission
        if (fileDataDTO.getPermission() != null) {
            Permission permission = new Permission();
            permission.setPermission(fileDataDTO.getPermission().toString());
            resource.setPermission(permission);
        }
        SecretKey key = new SecretKey();
        //set attributes to secret key
        if (fileDataDTO.getKey() != null) {
            key.setKey(fileDataDTO.getKey());
        }
        Timestamp timestamp = offsetDateTimeToTimestamp(fileDataDTO.getExpirationTime());
        key.setExpirationDate(timestamp);

        resource.setSecretKey(key);

        //set file metadata
        long resourceId = fileDataDTO.getResourceId() == null ? 0 : fileDataDTO.getResourceId();
        resource.setResourceId(resourceId);
        resource.setFileName(fileDataDTO.getFileName());
        resource.setMimeType(fileDataDTO.getMime());
        resource.setLinkToFile(fileDataDTO.getFileUUID());
        long size = fileDataDTO.getFileSize() == null ? 0 : fileDataDTO.getFileSize();
        resource.setSize(size);

        return resource;
    }

    /**
     * Convert collection of entities into list of dto
     *
     * @param resources - list of entities
     * @return list of dto
     */
    public static List<FileMetadataDTO> convertToDtoList(Collection<Resource> resources) {
        return resources.
                stream().
                map(ResourceConverter::convertToDto).
                collect(Collectors.toList());

    }

    /**
     * Convert collection of DTOs into list of entities
     *
     * @param dtos - list of DTOs
     * @return list of entities
     */
    public static List<Resource> convertToEntityList(Collection<FileMetadataDTO> dtos) {
        return dtos.
                stream().
                map(ResourceConverter::convertToEntity).
                collect(Collectors.toList());

    }
}
