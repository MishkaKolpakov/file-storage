package ua.softserve.academy.kv030.authservice.dao;

import ua.softserve.academy.kv030.authservice.entity.Resource;

import java.util.List;
import java.util.Optional;

/**
 * Handles CRUD operations on Resource entities.
 *
 * @author  user
 * @version  1.0
 * @sine  2017-11-19
 */
public interface ResourceDAO extends CrudDAO<Resource> {

    /**
     * Finds a single resource with specified UUID.
     *
     * @param uuid UUID under with a file stored. UUID must be unique for each file.
     * @return optional of a resource from database.
     * */
    Optional<Resource> findElementByUUID(String uuid);

    /**
     * Checks whether a resource with specified uuid exists in database.
     *
     * @param uuid uuid of a resource
     * @return <tt>true</tt> if email exists, <tt>false</tt> otherwise
     * */
    Boolean containsUUID(String uuid);

    /**
     * Finds a single resource that was encrypted with specified secret key.
     *
     * @param keyId ID of a secret key that was used to encrypt file.
     * @return optional of a resource from database.
     * */
    Optional<Resource> findElementBySecretKeyId(long keyId);

    /**
     * Finds all resources per page defined by offset to retrieve results from and by page size, sorted by resource ID.
     *
     * @param offset a first result entity to be retrieved from the database, starts from 0.
     * @param pageSize max number of results to retrieve.
     * @return a list of resources from database.
     * */
    List<Resource> findAllElementsByOffsetByPageSizeByIdSort(int offset, int pageSize, SortingOrderEnum order);

    /**
     * Finds all resources by defined search criteria.
     *
     * @param criteria criteria to search resources by
     * @return a list of resources from database.
     * */
    List<Resource> findAllElementsByCriteria(ResourceFilterCriteria criteria);
}
