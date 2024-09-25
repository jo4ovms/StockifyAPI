package com.jo4ovms.StockifyAPI.repository;

import com.jo4ovms.StockifyAPI.model.Product;
import com.jo4ovms.StockifyAPI.model.Supplier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findAll(Pageable pageable);
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    //@Cacheable(value = "productsBySupplier", key = "#supplier.id")
    Page<Product> findBySupplier(Supplier supplier, Pageable pageable);
   // @Cacheable(value = "productsByQuantity", key = "#quantity")
    List<Product> findByQuantityGreaterThan(Integer quantity);
}
