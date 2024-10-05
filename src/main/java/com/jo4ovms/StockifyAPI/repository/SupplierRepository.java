package com.jo4ovms.StockifyAPI.repository;

import com.jo4ovms.StockifyAPI.model.Supplier;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    Page<Supplier> findAll(Pageable pageable);

    Optional<Supplier> findByCnpj(String cnpj);
    Page<Supplier> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Supplier> findByProductType(String productType, Pageable pageable);

    Page<Supplier> findByNameContainingIgnoreCaseAndProductType(String name, String productType, Pageable pageable);

    boolean existsByCnpj(String cnpj);

    @Query("SELECT DISTINCT s.productType FROM Supplier s")
    List<String> findDistinctProductTypes();

}
