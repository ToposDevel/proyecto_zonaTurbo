package com.topostechnology.service;

import org.springframework.transaction.annotation.Transactional;

import com.topostechnology.domain.CoreCatalogEntity;
import com.topostechnology.exception.TrException;
import com.topostechnology.repository.BaseRepository;
import com.topostechnology.utils.DateUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public abstract class CoreCatalogService<T extends CoreCatalogEntity> {
    protected String entityName;

    public CoreCatalogService(String entityName) {
        this.entityName = entityName;
    }

    public T save(T entity) throws TrException {

        if (entity.getId() == null) {
            return create(entity);
        } else {
            return update(entity);
        }

    }

    protected T update(T entity) throws TrException {
        Optional<T> optionalEntity = findById(entity.getId());
        if (!optionalEntity.isPresent())
            throw new TrException(entityName + " entity not found.");

        entity.setUpdatedAt(DateUtils.getDateFrom(LocalDateTime.now()));
        return getRepository().save(entity);
    }

    protected T create(T entity) throws TrException {
        entity.setCreatedAt(DateUtils.getDateFrom(LocalDateTime.now()));
        entity.setActive(true);
        return getRepository().save(entity);
    }

    public T delete(Long id) throws TrException {
        Optional<T> optionalEntity = findById(id);

        if (!optionalEntity.isPresent())
            throw new TrException(entityName + " entity not found.");
        getRepository().delete(optionalEntity.get());

        return optionalEntity.get();
    }

    public List<T> findAll() {
        return getRepository().findAllByOrderByIdAsc();
    }

    public Optional<T> findById(Long id) {
        return getRepository().findById(id);
    }

    @Transactional
    public T changeStatus(Long id) throws TrException {
        Optional<T> optionalEntity = findById(id);
        if (!optionalEntity.isPresent())
            throw new TrException(entityName + " entity not found.");
        T entity = optionalEntity.get();
        entity.setActive(!entity.isActive());
        entity.setUpdatedAt(DateUtils.getDateFrom(LocalDateTime.now()));
        return getRepository().save(entity);
    }

    protected abstract BaseRepository<T, Long> getRepository();
}
