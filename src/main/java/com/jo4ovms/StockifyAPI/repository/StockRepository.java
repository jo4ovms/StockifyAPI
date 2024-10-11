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
    Page<Stock> findByQuantityGreaterThanEqual(int quantity, Pageable pageable);
    Page<Stock> findByQuantityLessThan(int threshold, Pageable pageable);
    @Query("SELECT sm FROM StockMovement sm WHERE sm.movementDate BETWEEN :startDate AND :endDate")
    Page<StockMovement> findStockMovementsByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable);
    Page<Stock> findByProductSupplierId(Long supplierId, Pageable pageable);
    @Query("SELECT s FROM Stock s WHERE LOWER(s.product.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(s.product.supplier.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Stock> searchByProductNameOrSupplier(@Param("query") String query, Pageable pageable);
    @Query("SELECT s FROM Stock s WHERE s.quantity BETWEEN :minQuantity AND :maxQuantity " +
            "AND s.value BETWEEN :minValue AND :maxValue")
    Page<Stock> findByQuantityAndValueRange(@Param("minQuantity") int minQuantity,
                                            @Param("maxQuantity") int maxQuantity,
                                            @Param("minValue") double minValue,
                                            @Param("maxValue") double maxValue,
                                            Pageable pageable);
    @Query("SELECT MAX(s.quantity) FROM Stock s")
    Object findMaxQuantity();

    @Query("SELECT MAX(s.value) FROM Stock s")
    Object findMaxValue();


    @Query("SELECT s FROM Stock s WHERE " +
            "(:supplierId IS NULL OR s.product.supplier.id = :supplierId) AND " +
            "s.quantity BETWEEN :minQuantity AND :maxQuantity AND " +
            "s.value BETWEEN :minValue AND :maxValue")
    Page<Stock> findBySupplierAndQuantityAndValue(
            @Param("supplierId") Long supplierId,
            @Param("minQuantity") int minQuantity,
            @Param("maxQuantity") int maxQuantity,
            @Param("minValue") double minValue,
            @Param("maxValue") double maxValue,
            Pageable pageable);

    @Query("SELECT s FROM Stock s WHERE " +
            "(:query IS NULL OR LOWER(s.product.name) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
            "s.quantity BETWEEN :minQuantity AND :maxQuantity AND " +
            "s.value BETWEEN :minValue AND :maxValue")
    Page<Stock> searchByProductNameAndQuantityAndValue(
            @Param("query") String query,
            @Param("minQuantity") int minQuantity,
            @Param("maxQuantity") int maxQuantity,
            @Param("minValue") double minValue,
            @Param("maxValue") double maxValue,
            Pageable pageable);
    @Query("SELECT s FROM Stock s WHERE " +
            "(:query IS NULL OR LOWER(s.product.name) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
            "(:supplierId IS NULL OR s.product.supplier.id = :supplierId) AND " +
            "s.quantity BETWEEN :minQuantity AND :maxQuantity AND " +
            "s.value BETWEEN :minValue AND :maxValue")
    Page<Stock> searchByProductNameAndSupplierAndQuantityAndValue(
            @Param("query") String query,
            @Param("supplierId") Long supplierId,
            @Param("minQuantity") int minQuantity,
            @Param("maxQuantity") int maxQuantity,
            @Param("minValue") double minValue,
            @Param("maxValue") double maxValue,
            Pageable pageable);

    List<Stock> findByQuantityEquals(int quantity);
}



