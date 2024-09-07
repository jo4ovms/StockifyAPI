package com.jo4ovms.StockifyAPI.controller;

import com.jo4ovms.StockifyAPI.model.DTO.ProductDTO;
import com.jo4ovms.StockifyAPI.service.ProductService;
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

@RestController
@RequestMapping("/products")
@Tag(name = "Product", description = "API for managing products")
public class ProductController {

    private final ProductService productService;
    private final PagedResourcesAssembler<ProductDTO> pagedResourcesAssembler;

    public ProductController(ProductService productService, PagedResourcesAssembler<ProductDTO> pagedResourcesAssembler) {
        this.productService = productService;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(summary = "Create a new product", description = "Create a new product and return the created product's details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
    })
    @PostMapping
    @CacheEvict(value = "products", allEntries = true)
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        ProductDTO createdProduct = productService.createProduct(productDTO);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }


    @Operation(summary = "Update an existing product", description = "Update the product with the specified ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    @PutMapping("/{id}")
    @CacheEvict(value = "products", allEntries = true)
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductDTO productDTO) {
        ProductDTO updatedProduct = productService.updateProduct(id, productDTO);
        return ResponseEntity.ok(updatedProduct);
    }


    @Operation(summary = "Retrieve all products", description = "Retrieve a paginated list of all products.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PagedModel.class)) })
    })
    @GetMapping
    @Cacheable(value = "products")
    public ResponseEntity<PagedModel<EntityModel<ProductDTO>>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String nameFilter) {

        Page<ProductDTO> products = productService.findAllProducts(page, size, nameFilter);
        PagedModel<EntityModel<ProductDTO>> pagedModel = pagedResourcesAssembler.toModel(products);
        return ResponseEntity.ok(pagedModel);
    }

    @Operation(summary = "Retrieve a product by ID", description = "Retrieve the details of a product by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product retrieved",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    @GetMapping("/{id}")
    @Cacheable(value = "products", key = "#id")
    public ResponseEntity<ProductDTO> getProductById(
            @PathVariable Long id) {
        ProductDTO product = productService.findProductById(id);
        return ResponseEntity.ok(product);
    }


    @Operation(summary = "Delete a product", description = "Delete a product by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    @CacheEvict(value = "products", allEntries = true)
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
