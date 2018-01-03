package ua.softserve.academy.kv030.authservice.dao;

import java.util.List;
import java.util.Optional;

public interface CrudDAO<T> {
    List<T> findAll();

    T update(T item);

    boolean delete(T item);

    Optional<T> findElementById(Long id);

    T insert(T item);

    Long count();

    <F> Boolean containsFieldEqual(String fieldName, F fieldValue);

    <F> Optional<T> findOneByFieldEqual(String fieldName, F fieldValue);

    <F> List<T> findAllByFieldEqual(String fieldName, F fieldValue);

    <F extends Comparable> List<T> findAllByFieldBetween(String fieldName, F fieldValueStart, F fieldValueEnd);

    List<T> findAllByOffsetByPageSizeSortedByField(int offset, int pageSize, String fieldToSortBy, SortingOrderEnum order);
}