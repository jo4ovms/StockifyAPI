package com.jo4ovms.StockifyAPI.controller;

import com.jo4ovms.StockifyAPI.model.DTO.SupplierDTO;
import com.jo4ovms.StockifyAPI.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/suppliers")
public class SupplierController {

    private final SupplierService supplierService;
    private final PagedResourcesAssembler<SupplierDTO> pagedResourcesAssembler;

    public SupplierController(SupplierService supplierService, PagedResourcesAssembler<SupplierDTO> pagedResourcesAssembler) {
        this.supplierService = supplierService;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @PostMapping
    @CacheEvict(value = "suppliers", allEntries = true)
    public ResponseEntity<SupplierDTO> createSupplier(
            @Valid @RequestBody SupplierDTO supplierDTO) {
        SupplierDTO createdSupplier = supplierService.createSupplier(supplierDTO);
        return new ResponseEntity<>(createdSupplier, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @CacheEvict(value = "suppliers", allEntries = true)
    public ResponseEntity<SupplierDTO> updateSupplier(
            @PathVariable Long id,
            @Valid @RequestBody SupplierDTO supplierDTO) {
        SupplierDTO updatedSupplier = supplierService.updateSupplier(id, supplierDTO);
        return ResponseEntity.ok(updatedSupplier);
    }

    @GetMapping
    @Cacheable(value = "suppliers")
    public ResponseEntity<PagedModel<EntityModel<SupplierDTO>>> getAllSuppliers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<SupplierDTO> suppliers = supplierService.findAllSuppliers(page, size);
        PagedModel<EntityModel<SupplierDTO>> pagedModel = pagedResourcesAssembler.toModel(suppliers);
        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{id}")
    @Cacheable(value = "suppliers", key = "#id")
    public ResponseEntity<SupplierDTO> getSupplierById(
            @PathVariable Long id) {
        SupplierDTO supplier = supplierService.findSupplierById(id);
        return ResponseEntity.ok(supplier);
    }

    @DeleteMapping("/{id}")
    @CacheEvict(value = "suppliers", allEntries = true)
    public ResponseEntity<Void> deleteSupplier(
            @PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }
}
