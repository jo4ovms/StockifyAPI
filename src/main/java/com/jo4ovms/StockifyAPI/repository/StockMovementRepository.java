package com.jo4ovms.StockifyAPI.repository;
import com.jo4ovms.StockifyAPI.model.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    List<StockMovement> findByStockProductIdAndMovementDateBetween(Long productId, LocalDateTime startDate, LocalDateTime endDate);
    Page<StockMovement> findAll(Pageable pageable);
}