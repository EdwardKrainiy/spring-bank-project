package com.itech.dao;

import com.itech.model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Repository
@EnableTransactionManagement
@Transactional

public abstract class EntityRepository<TEntity> implements IRepository<TEntity> {

    @Autowired
    protected HibernateTransactionManager transactionManager;

    @Override
    public Long create(TEntity entity) {
        Session session = Objects.requireNonNull(transactionManager.getSessionFactory()).openSession();
        Transaction tx1 = session.beginTransaction();
        Long savedEntityId = (Long) session.save(entity);
        tx1.commit();
        session.close();
        return savedEntityId;
    }

    @Override
    public TEntity update(TEntity entity) {
        Session session = Objects.requireNonNull(transactionManager.getSessionFactory()).openSession();
        Transaction tx1 = session.beginTransaction();
        session.update(entity);
        tx1.commit();
        session.close();
        return entity;
    }

    @Override
    public void delete(TEntity entity) {
        Session session = Objects.requireNonNull(transactionManager.getSessionFactory()).openSession();
        Transaction tx1 = session.beginTransaction();
        session.delete(entity);
        tx1.commit();
        session.close();
    }

    @Override
    public TEntity findById(Long id) {
        return (TEntity) transactionManager
                .getSessionFactory()
                .openSession()
                .createQuery("from " + getTableName() + " WHERE id = " + id).uniqueResult();
    }

    @Override
    public List<TEntity> findAll() {
        return (List<TEntity>) transactionManager
                .getSessionFactory()
                .openSession()
                .createQuery("from " + getTableName()).list();
    }

    public abstract String getTableName();

}
