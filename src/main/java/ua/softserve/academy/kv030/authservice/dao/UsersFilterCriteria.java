package ua.softserve.academy.kv030.authservice.dao;

/**
 * Created by user on 08.12.17.
 */
public class UsersFilterCriteria {
    private Integer limit;
    private Integer offset;
    private SortingOrderEnum order;
    private String fieldToSortBy;
    private String role;
    private Boolean userStatus;
    private Boolean passwordStatus;

    public UsersFilterCriteria() {
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(Boolean userStatus) {
        this.userStatus = userStatus;
    }

    public Boolean getPasswordStatus() {
        return passwordStatus;
    }

    public void setPasswordStatus(Boolean passwordStatus) {
        this.passwordStatus = passwordStatus;
    }

}
