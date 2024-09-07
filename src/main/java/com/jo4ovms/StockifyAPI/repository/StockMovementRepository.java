package com.jo4ovms.StockifyAPI.repository;
import com.jo4ovms.StockifyAPI.model.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    @Query("SELECT sm FROM StockMovement sm WHERE sm.movementDate BETWEEN :startDate AND :endDate")
    List<StockMovement> findStockMovementsByDateRange(LocalDate startDate, LocalDate endDate);
    List<StockMovement> findByStockProductIdAndMovementDateBetween(Long productId, LocalDateTime startDate, LocalDateTime endDate);
}