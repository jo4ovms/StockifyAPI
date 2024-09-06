package com.jo4ovms.StockifyAPI.controller;

import com.jo4ovms.StockifyAPI.model.DTO.StockDTO;
import com.jo4ovms.StockifyAPI.service.StockService;
import jakarta.validation.Valid;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stocks")
public class StockController {

    private final StockService stockService;
    private final PagedResourcesAssembler<StockDTO> pagedResourcesAssembler;

    public StockController(StockService stockService, PagedResourcesAssembler<StockDTO> pagedResourcesAssembler) {
        this.stockService = stockService;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @PostMapping
    @CacheEvict(value = "stocks", allEntries = true)
    public ResponseEntity<StockDTO> createStock(@Valid @RequestBody StockDTO stockDTO) {
        StockDTO createdStock = stockService.createStock(stockDTO);
        return new ResponseEntity<>(createdStock, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @CacheEvict(value = "stocks", allEntries = true)
    public ResponseEntity<StockDTO> updateStock(
            @PathVariable Long id,
            @Valid @RequestBody StockDTO stockDTO) {
        StockDTO updatedStock = stockService.updateStock(id, stockDTO);
        return ResponseEntity.ok(updatedStock);
    }

    @GetMapping("/{id}")
    @Cacheable(value = "stocks", key = "#id")
    public ResponseEntity<StockDTO> getStockById(
            @PathVariable Long id) {
        StockDTO stock = stockService.getStockById(id);
        return ResponseEntity.ok(stock);
    }

    @GetMapping
    @Cacheable(value = "stocks")
    public ResponseEntity<PagedModel<EntityModel<StockDTO>>> getAllStocks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<StockDTO> stocks = stockService.getAllStocks(pageable);
        PagedModel<EntityModel<StockDTO>> pagedModel = pagedResourcesAssembler.toModel(stocks);
        return ResponseEntity.ok(pagedModel);
    }

    @DeleteMapping("/{id}")
    @CacheEvict(value = "stocks", allEntries = true)
    public ResponseEntity<Void> deleteStock(
            @PathVariable Long id) {
        stockService.deleteStock(id);
        return ResponseEntity.noContent().build();
    }
}
