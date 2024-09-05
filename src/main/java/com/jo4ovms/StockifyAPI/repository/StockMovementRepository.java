package com.jo4ovms.StockifyAPI.repository;
import com.jo4ovms.StockifyAPI.model.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
}
