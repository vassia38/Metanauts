package com.main.repository.paging;


import com.main.model.Entity;
import com.main.repository.Repository;

public interface PagingRepository<ID ,
        E extends Entity<ID>>
        extends Repository<ID, E> {

    Page<E> findAll(Pageable pageable);   // Pageable e un fel de paginator
}
