package com.jo4ovms.StockifyAPI.repository;

import com.jo4ovms.StockifyAPI.model.AggregatedSale;
import com.jo4ovms.StockifyAPI.model.DTO.SaleSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AggregatedSaleRepository extends JpaRepository<AggregatedSale, Long> {

    @Query("SELECT new com.jo4ovms.StockifyAPI.model.DTO.SaleSummaryDTO(a.stock.product.name, a.totalQuantitySold) " +
            "FROM AggregatedSale a " +
            "WHERE (:searchTerm IS NULL OR LOWER(a.stock.product.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "AND (:supplierId IS NULL OR a.stock.product.supplier.id = :supplierId)")
    Page<SaleSummaryDTO> findSalesGroupedByProductAndSupplier(String searchTerm, Long supplierId, Pageable pageable);

    Optional<AggregatedSale> findByProductIdAndStockId(Long productId, Long stockId);

}
