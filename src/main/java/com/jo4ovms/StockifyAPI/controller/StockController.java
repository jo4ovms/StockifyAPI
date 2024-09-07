package com.jo4ovms.StockifyAPI.controller;

import com.jo4ovms.StockifyAPI.model.DTO.StockDTO;
import com.jo4ovms.StockifyAPI.service.stock.StockService;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/stocks")
@Tag(name = "Stock", description = "Operations related to stock management")
public class StockController {

    private final StockService stockService;
    private final PagedResourcesAssembler<StockDTO> pagedResourcesAssembler;

    public StockController(StockService stockService, PagedResourcesAssembler<StockDTO> pagedResourcesAssembler) {
        this.stockService = stockService;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(summary = "Create a new stock", description = "Creates a new stock entry.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Stock created successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StockDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PostMapping
    @CacheEvict(value = "stocks", allEntries = true)
    public ResponseEntity<StockDTO> createStock(@Valid @RequestBody StockDTO stockDTO) {
        StockDTO createdStock = stockService.createStock(stockDTO);
        return new ResponseEntity<>(createdStock, HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing stock", description = "Updates an existing stock entry by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock updated successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StockDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Stock not found", content = @Content)
    })
    @PutMapping("/{id}")
    @CacheEvict(value = "stocks", allEntries = true)
    public ResponseEntity<StockDTO> updateStock(
            @PathVariable Long id,
            @Valid @RequestBody StockDTO stockDTO) {
        StockDTO updatedStock = stockService.updateStock(id, stockDTO);
        return ResponseEntity.ok(updatedStock);
    }


    @Operation(summary = "Retrieve all stocks", description = "Retrieve a paginated list of all stock entries.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stocks retrieved successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)) })
    })
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

    @Operation(summary = "Retrieve stock details", description = "Retrieve the details of a stock entry by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock details retrieved successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StockDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Stock not found", content = @Content)
    })
    @GetMapping("/{id}")
    @Cacheable(value = "stocks", key = "#id")
    public ResponseEntity<StockDTO> getStockById(
            @PathVariable Long id) {
        StockDTO stock = stockService.getStockById(id);
        return ResponseEntity.ok(stock);
    }

    @Operation(summary = "Delete a stock", description = "Deletes a stock entry by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Stock deleted successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Stock not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    @CacheEvict(value = "stocks", allEntries = true)
    public ResponseEntity<Void> deleteStock(
            @PathVariable Long id) {
        stockService.deleteStock(id);
        return ResponseEntity.noContent().build();
    }
}
