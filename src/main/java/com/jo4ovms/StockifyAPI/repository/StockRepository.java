package com.jo4ovms.StockifyAPI.repository;

import com.jo4ovms.StockifyAPI.model.Product;
import com.jo4ovms.StockifyAPI.model.Stock;
import com.jo4ovms.StockifyAPI.model.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByProduct(Product product);
    List<Stock> findByQuantityGreaterThan(int quantity);
    List<Stock> findByQuantityLessThan(int threshold);
    @Query("SELECT sm FROM StockMovement sm WHERE sm.movementDate BETWEEN :startDate AND :endDate")
    Page<StockMovement> findStockMovementsByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable);
    Page<Stock> findByProductSupplierId(Long supplierId, Pageable pageable);
    @Query("SELECT s FROM Stock s WHERE LOWER(s.product.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(s.product.supplier.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Stock> searchByProductNameOrSupplier(@Param("query") String query, Pageable pageable);


}
