package ua.softserve.academy.kv030.authservice.dao;

/**
 * Created by user on 21.12.17.
 */
public class ResourceFilterCriteria {
    private Long userId;
    private Integer limit;
    private Integer offset;
    private SortingOrderEnum order;
    private String fieldToSortBy;
    private FileTypeEnum fileType;
    private PermissionTypeEnum permissionType;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public SortingOrderEnum getOrder() {
        return order;
    }

    public void setOrder(SortingOrderEnum order) {
        this.order = order;
    }

    public String getFieldToSortBy() {
        return fieldToSortBy;
    }

    public void setFieldToSortBy(String fieldToSortBy) {
        this.fieldToSortBy = fieldToSortBy;
    }

    public FileTypeEnum getFileType() {
        return fileType;
    }

    public void setFileType(FileTypeEnum fileType) {
        this.fileType = fileType;
    }

    public PermissionTypeEnum getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(PermissionTypeEnum permissionType) {
        this.permissionType = permissionType;
    }

    public enum FileTypeEnum {
        OWN, SHARED
    }

    public enum PermissionTypeEnum {
        NOT_SET, ALL_USERS, LIST_OF_USERS
    }

    @Override
    public String toString() {
        return "ResourceFilterCriteria{" + "userId=" + userId + ", limit=" + limit + ", offset=" + offset + ", order=" + order + ", fieldToSortBy='" + fieldToSortBy + '\'' + ", fileType=" + fileType + ", permissionType=" + permissionType + '}';
    }
}
