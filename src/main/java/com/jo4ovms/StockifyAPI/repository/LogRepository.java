package com.jo4ovms.StockifyAPI.repository;

import com.jo4ovms.StockifyAPI.model.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {

    Page<Log> findByEntityAndOperationType(String entity, Log.OperationType operationType, Pageable pageable);
    Page<Log> findByEntity(String entity, Pageable pageable);
    Page<Log> findByOperationType(Log.OperationType operationType, Pageable pageable);
}
