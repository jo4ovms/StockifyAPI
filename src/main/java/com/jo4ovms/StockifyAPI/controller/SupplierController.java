package com.jo4ovms.StockifyAPI.controller;

import com.jo4ovms.StockifyAPI.exception.DuplicateResourceException;
import com.jo4ovms.StockifyAPI.exception.ResourceNotFoundException;
import com.jo4ovms.StockifyAPI.model.DTO.SupplierDTO;
import com.jo4ovms.StockifyAPI.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }


    @PostMapping
    public ResponseEntity<SupplierDTO> createSupplier(@Valid @RequestBody SupplierDTO supplierDTO) {
        SupplierDTO createdSupplier = supplierService.createSupplier(supplierDTO);
        return new ResponseEntity<>(createdSupplier, HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<SupplierDTO> updateSupplier(@PathVariable Long id, @Valid @RequestBody SupplierDTO supplierDTO) {
        SupplierDTO updatedSupplier = supplierService.updateSupplier(id, supplierDTO);
        return ResponseEntity.ok(updatedSupplier);
    }


    @GetMapping
    public ResponseEntity<Page<SupplierDTO>> getAllSuppliers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<SupplierDTO> suppliers = supplierService.findAllSuppliers(page, size);
        return ResponseEntity.ok(suppliers);
    }


    @GetMapping("/{id}")
    public ResponseEntity<SupplierDTO> getSupplierById(@PathVariable Long id) {
        SupplierDTO supplier = supplierService.findSupplierById(id);
        return ResponseEntity.ok(supplier);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }
}

