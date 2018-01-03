package ua.softserve.academy.kv030.authservice.converter;

import ua.softserve.academy.kv030.authservice.api.model.FilesFilterDTO;
import ua.softserve.academy.kv030.authservice.dao.ResourceFilterCriteria;
import ua.softserve.academy.kv030.authservice.dao.SortingOrderEnum;
import ua.softserve.academy.kv030.authservice.exceptions.AuthServiceException;

/**
 * Created by user on 21.12.17.
 */
public final class ResourceFilterConverter {
    private ResourceFilterConverter() {
    }

    /**
     * Converts files metadata query parameters DTO object to ResourceFilterCriteria
     *
     * @param filterDTO  files metadata filter parameters DTO object that needed to be converted to ResourceFilterCriteria
     * @return ResourceFilterCriteria object
     * @throws AuthServiceException if DTO object is null
     */
    public static ResourceFilterCriteria convert(FilesFilterDTO filterDTO) {
        if(filterDTO == null) {
            throw new AuthServiceException("FilesFilterDTO to be converted to ResourceFilterCriteria is null.");
        }
        ResourceFilterCriteria criteria = new ResourceFilterCriteria();
        criteria.setOffset(filterDTO.getOffset());
        criteria.setLimit(filterDTO.getLimit());
        criteria.setOrder(filterDTO.getOrder() == null ? null : SortingOrderEnum.valueOf(filterDTO.getOrder().name()));
        criteria.setFieldToSortBy(filterDTO.getSortField() == null ? null : filterDTO.getSortField().toString());
        criteria.setPermissionType(filterDTO.getPermissionType() == null ? null :
                ResourceFilterCriteria.PermissionTypeEnum.valueOf(filterDTO.getPermissionType().name()));
        criteria.setUserId(filterDTO.getUserId());
        criteria.setFileType(filterDTO.getFileType() == null ? null : ResourceFilterCriteria.FileTypeEnum.valueOf(filterDTO.getFileType().name()));
        return criteria;
    }
}
