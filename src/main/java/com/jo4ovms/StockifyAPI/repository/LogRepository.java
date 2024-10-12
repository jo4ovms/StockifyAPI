package com.jo4ovms.StockifyAPI.repository;

import com.jo4ovms.StockifyAPI.model.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {
}
