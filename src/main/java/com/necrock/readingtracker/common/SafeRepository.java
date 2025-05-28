package com.necrock.readingtracker.common;

import com.google.common.collect.ImmutableMap;
import com.necrock.readingtracker.exception.DatabaseException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public abstract class SafeRepository<E, K> {

    private final JpaRepository<E, K> repository;

    @PersistenceContext
    private EntityManager entityManager;

    public SafeRepository(JpaRepository<E, K> repository) {
        this.repository = repository;
    }

    protected abstract ImmutableMap<String, Function<E, RuntimeException>> getUniqueConstraints();

    public final E saveAndFlush(E entity) {
        if (!entityManager.isJoinedToTransaction()) {
            throw new IllegalStateException("EntityManager is not joined to a transaction" +
                    " - make sure to call this method withing a @Transactional environment");
        }
        return doSaveAndHandleExceptions(repository::saveAndFlush, entity);
    }

    public final E save(E entity) {
        return doSaveAndHandleExceptions(repository::save, entity);
    }

    private E doSaveAndHandleExceptions(Function<E, E> saveFunction, E entity) {
        try {
            onSave(entity);
            return saveFunction.apply(entity);
        } catch (DataIntegrityViolationException | ConstraintViolationException ex) {
            for (var constraint : getUniqueConstraints().entrySet()) {
                if (messageIndicatesConstraint(ex, constraint.getKey())) {
                    throw constraint.getValue().apply(entity);
                }
            }
            throw new DatabaseException("Failed to save: " + ExceptionUtils.getRootCauseMessage(ex),
                    ex);
        } catch (RuntimeException ex) {
            throw new DatabaseException("Failed to save: " + ExceptionUtils.getRootCauseMessage(ex),
                    ex);
        }
    }

    protected void onSave(E entity) {}

    public final void delete(E entity) {
        repository.delete(entity);
        onDelete(entity);
    }

    protected void onDelete(E entity) {}

    public final Optional<E> findById(K key) {
        return repository.findById(key);
    }

    public final List<E> findAll() {
        return repository.findAll();
    }

    private static boolean messageIndicatesConstraint(Throwable ex, String constraint) {
        return ExceptionUtils.getRootCauseMessage(ex).toLowerCase().contains(constraint.toLowerCase());
    }
}
