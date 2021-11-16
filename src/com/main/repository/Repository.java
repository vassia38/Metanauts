package com.main.repository;

import com.main.model.validators.ValidationException;
import com.main.model.Entity;

/**
 * CRUD operations repository interface
 * @param <ID> - type E must have an attribute of type ID
 * @param <E> -  type of entities saved in repository
 */

public interface Repository<ID, E extends Entity<ID>> {

    /**
     * @param id -the id of the entity to be returned
     *           id must not be null
     * @return the entity with the specified id
     * or null - if there is no entity with the given id
     * @throws IllegalArgumentException if id is null.
     */
    E findOneById(ID id);

    /**
     * @return all entities
     */
    Iterable<E> findAll();

    /**
     * @param entity entity must be not null
     * @return null- if the given entity is saved
     * otherwise returns the entity (id already exists)
     * @throws ValidationException      if the entity is not valid
     * @throws IllegalArgumentException if the given entity is null.     *
     */
    E save(E entity);

    /**
     * @param id of an entity to be deleted
     * @return null if no entity was found and deleted,
     * otherwise return the deleted entity
     */
    E delete(ID id);

    /**
     * @return amount of entities in this repository
     */
    Integer size();

    /**
     * @param entity whose id must match an existent entity
     * @return the entity with old state
     */
    E update(E entity);
}


