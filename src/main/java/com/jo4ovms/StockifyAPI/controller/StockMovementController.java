package com.jo4ovms.StockifyAPI.controller;

import com.jo4ovms.StockifyAPI.model.DTO.StockMovementDTO;
import com.jo4ovms.StockifyAPI.service.StockMovementService;
import jakarta.validation.Valid;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stock-movements")
public class StockMovementController {

    private final StockMovementService stockMovementService;

    public StockMovementController(StockMovementService stockMovementService) {
        this.stockMovementService = stockMovementService;
    }

    @PostMapping
    @CacheEvict(value = "stockMovements", allEntries = true)
    public ResponseEntity<StockMovementDTO> registerMovement(

            @Valid @RequestBody StockMovementDTO stockMovementDTO) {
        StockMovementDTO registeredMovement = stockMovementService.registerMovement(stockMovementDTO);
        return new ResponseEntity<>(registeredMovement, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Cacheable(value = "stockMovements", key = "#id")
    public ResponseEntity<StockMovementDTO> getStockMovementById(
            @PathVariable Long id) {
        StockMovementDTO stockMovement = stockMovementService.getStockMovementById(id);
        return ResponseEntity.ok(stockMovement);
    }

    @GetMapping
    @Cacheable(value = "stockMovements")
    public ResponseEntity<List<StockMovementDTO>> getAllStockMovements() {
        List<StockMovementDTO> stockMovements = stockMovementService.getAllStockMovements();
        return ResponseEntity.ok(stockMovements);
    }
}
