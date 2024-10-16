package com.jo4ovms.StockifyAPI.repository;

import com.jo4ovms.StockifyAPI.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
}
