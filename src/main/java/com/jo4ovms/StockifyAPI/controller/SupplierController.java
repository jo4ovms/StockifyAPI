package com.jo4ovms.StockifyAPI.controller;

import com.jo4ovms.StockifyAPI.model.DTO.SupplierDTO;
import com.jo4ovms.StockifyAPI.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@Tag(name = "Supplier", description = "API for managing suppliers")
public class SupplierController {

    private final SupplierService supplierService;
    private final PagedResourcesAssembler<SupplierDTO> pagedResourcesAssembler;

    public SupplierController(SupplierService supplierService, PagedResourcesAssembler<SupplierDTO> pagedResourcesAssembler) {
        this.supplierService = supplierService;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(summary = "Create a new supplier", description = "Create a new supplier and return the created supplier's details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Supplier created",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SupplierDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
    })
    @PostMapping
   // @CacheEvict(value = "suppliers", allEntries = true)
    public ResponseEntity<SupplierDTO> createSupplier(
            @Valid @RequestBody SupplierDTO supplierDTO) {
        SupplierDTO createdSupplier = supplierService.createSupplier(supplierDTO);
        return new ResponseEntity<>(createdSupplier, HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing supplier", description = "Update the supplier with the specified ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Supplier updated",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SupplierDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Supplier not found", content = @Content)
    })
    @PutMapping("/{id}")
   // @CacheEvict(value = "suppliers", allEntries = true)
    public ResponseEntity<SupplierDTO> updateSupplier(
            @PathVariable Long id,
            @Valid @RequestBody SupplierDTO supplierDTO) {
        SupplierDTO updatedSupplier = supplierService.updateSupplier(id, supplierDTO);
        return ResponseEntity.ok(updatedSupplier);
    }

    @Operation(summary = "Retrieve all suppliers", description = "Retrieve a paginated list of all suppliers.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Suppliers retrieved",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)) })
    })

    @GetMapping
   // @Cacheable(value = "suppliers")
    public ResponseEntity<PagedModel<EntityModel<SupplierDTO>>> getAllSuppliers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<SupplierDTO> suppliers = supplierService.findAllSuppliers(page, size);
        PagedModel<EntityModel<SupplierDTO>> pagedModel = pagedResourcesAssembler.toModel(suppliers);
        return ResponseEntity.ok(pagedModel);
    }


    @Operation(summary = "Retrieve a supplier by ID", description = "Retrieve the details of a supplier by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Supplier retrieved",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SupplierDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Supplier not found", content = @Content)
    })
    @GetMapping("/{id}")
   // @Cacheable(value = "suppliers", key = "#id")
    public ResponseEntity<SupplierDTO> getSupplierById(
            @PathVariable Long id) {
        SupplierDTO supplier = supplierService.findSupplierById(id);
        return ResponseEntity.ok(supplier);
    }

    @Operation(summary = "Delete a supplier", description = "Delete a supplier by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Supplier deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Supplier not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    //@CacheEvict(value = "suppliers", allEntries = true)
    public ResponseEntity<Void> deleteSupplier(
            @PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/search")
    public ResponseEntity<PagedModel<EntityModel<SupplierDTO>>> searchSuppliersByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<SupplierDTO> suppliers = supplierService.findSuppliersByName(name, page, size);
        PagedModel<EntityModel<SupplierDTO>> pagedModel = pagedResourcesAssembler.toModel(suppliers);
        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/filter")
    public ResponseEntity<PagedModel<EntityModel<SupplierDTO>>> filterSuppliers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String productType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<SupplierDTO> suppliers = supplierService.filterSuppliers(name, productType, page, size);
        PagedModel<EntityModel<SupplierDTO>> pagedModel = pagedResourcesAssembler.toModel(suppliers);
        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/product-types")
    public ResponseEntity<List<String>> getAllProductTypes() {
        List<String> productTypes = supplierService.findAllProductTypes();
        return ResponseEntity.ok(productTypes);
    }
}
