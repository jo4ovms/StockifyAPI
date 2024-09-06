package com.jo4ovms.StockifyAPI.controller;

import com.jo4ovms.StockifyAPI.model.DTO.ProductDTO;
import com.jo4ovms.StockifyAPI.service.ProductService;
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
public class ProductController {

    private final ProductService productService;
    private final PagedResourcesAssembler<ProductDTO> pagedResourcesAssembler;

    public ProductController(ProductService productService, PagedResourcesAssembler<ProductDTO> pagedResourcesAssembler) {
        this.productService = productService;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @PostMapping
    @CacheEvict(value = "products", allEntries = true)
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        ProductDTO createdProduct = productService.createProduct(productDTO);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @CacheEvict(value = "products", allEntries = true)
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductDTO productDTO) {
        ProductDTO updatedProduct = productService.updateProduct(id, productDTO);
        return ResponseEntity.ok(updatedProduct);
    }

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

    @GetMapping("/{id}")
    @Cacheable(value = "products", key = "#id")
    public ResponseEntity<ProductDTO> getProductById(
            @PathVariable Long id) {
        ProductDTO product = productService.findProductById(id);
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/{id}")
    @CacheEvict(value = "products", allEntries = true)
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
