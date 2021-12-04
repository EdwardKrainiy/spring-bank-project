package com.itech.dao;

import java.util.List;

public interface IRepository<T> {
    Long create(T obj);
    T update(T obj);
    void delete(T obj);
    T findById(Long id);
    List<T> findAll();
}
