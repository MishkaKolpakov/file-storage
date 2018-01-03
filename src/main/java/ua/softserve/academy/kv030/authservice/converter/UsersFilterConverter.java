package ua.softserve.academy.kv030.authservice.converter;

import ua.softserve.academy.kv030.authservice.api.model.UsersFilterDTO;
import ua.softserve.academy.kv030.authservice.dao.SortingOrderEnum;
import ua.softserve.academy.kv030.authservice.dao.UsersFilterCriteria;
import ua.softserve.academy.kv030.authservice.exceptions.AuthServiceException;

import java.util.Arrays;

public final class UsersFilterConverter {
    private UsersFilterConverter() {
    }

    /**
     * Converts users query parameters DTO object to UsersFilterCriteria
     *
     * @param filterDTO  users filter parameters DTO object that needed to be converted to UsersFilterCriteria
     * @return UsersFilterCriteria object
     * @throws AuthServiceException if DTO object is null
     */
    public static UsersFilterCriteria convert(UsersFilterDTO filterDTO) {
        if(filterDTO == null) {
            throw new AuthServiceException("UsersQueryCriteriaDTO to be converted to UsersFilterCriteria is null.");
        }
        UsersFilterCriteria criteria = new UsersFilterCriteria();
        criteria.setOffset(filterDTO.getOffset());
        criteria.setLimit(filterDTO.getLimit());
        criteria.setOrder(filterDTO.getOrder() == null ? null : SortingOrderEnum.valueOf(filterDTO.getOrder().name()));
        criteria.setFieldToSortBy(filterDTO.getSortField() == null ? null : filterDTO.getSortField().toString());
        criteria.setRole(filterDTO.getRole() == null ? null : filterDTO.getRole().name());
        criteria.setRole(filterDTO.getRole() == null ? null : filterDTO.getRole().name());
        UsersFilterDTO.UserStatusEnum  userStatus = filterDTO.getUserStatus();
        if(userStatus != null) {
            switch (userStatus) {
                case ACTIVE:
                    criteria.setUserStatus(true);
                    break;
                case DELETED:
                    criteria.setUserStatus(false);
                    break;
                default:
                    throw new AuthServiceException(String.format("Not known user status: %s. User status may be one of the following: %s.",
                            userStatus, Arrays.asList(UsersFilterDTO.UserStatusEnum.values())));
            }
        }
        UsersFilterDTO.PasswordStatusEnum  passwordStatus = filterDTO.getPasswordStatus();
        if(passwordStatus != null) {
            switch (passwordStatus) {
                case ACTIVE:
                    criteria.setPasswordStatus(true);
                    break;
                case EXPIRED:
                    criteria.setPasswordStatus(false);
                    break;
                default:
                    throw new AuthServiceException(String.format("Not known password status: %s. Password status may be one of the following: %s.",
                            userStatus, Arrays.asList(UsersFilterDTO.PasswordStatusEnum.values())));
            }
        }
        return criteria;
    }
}
