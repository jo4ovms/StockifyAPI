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
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/stock")
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
   // @CacheEvict(value = "stocks", allEntries = true)
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
  //  @CacheEvict(value = "stocks", allEntries = true)
    public ResponseEntity<StockDTO> updateStock(
            @PathVariable Long id,
            @Valid @RequestBody StockDTO stockDTO) {
        StockDTO updatedStock = stockService.updateStock(id, stockDTO);
        return ResponseEntity.ok(updatedStock);
    }


    @Operation(summary = "Retrieve all stocks", description = "Retrieve a paginated list of all stock entries, with optional filtering by quantity and value.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stocks retrieved successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)) })
    })
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<StockDTO>>> getAllStocks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer minQuantity,
            @RequestParam(required = false) Integer maxQuantity,
            @RequestParam(required = false) Double minValue,
            @RequestParam(required = false) Double maxValue,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long supplierId) {

        Object maxQuantityFromBackend = stockService.getMaxQuantity();
        Object maxValueFromBackend = stockService.getMaxValue();

        int minQty = (minQuantity != null) ? minQuantity : 0;
        int maxQty = (maxQuantity != null) ? maxQuantity : (Integer) maxQuantityFromBackend;
        double minVal = (minValue != null) ? minValue : 0.0;
        double maxVal = (maxValue != null) ? maxValue : (Double) maxValueFromBackend;

        PageRequest pageable = PageRequest.of(page, size);


        Page<StockDTO> stocks = stockService.getFilteredStocks(query, supplierId, minQty, maxQty, minVal, maxVal, pageable);

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
  //  @Cacheable(value = "stocks", key = "#id")
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
  //  @CacheEvict(value = "stocks", allEntries = true)
    public ResponseEntity<Void> deleteStock(
            @PathVariable Long id) {
        stockService.deleteStock(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/by-supplier")
    public ResponseEntity<PagedModel<EntityModel<StockDTO>>> getStocksBySupplier(
            @RequestParam("supplierId") Long supplierId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<StockDTO> stocks = stockService.getStocksBySupplier(supplierId, pageable);
        PagedModel<EntityModel<StockDTO>> pagedModel = pagedResourcesAssembler.toModel(stocks);
        return ResponseEntity.ok(pagedModel);
    }

    @Operation(summary = "Search stocks", description = "Search stocks by product name or supplier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stocks retrieved successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)) })
    })
    @GetMapping("/search")
    public ResponseEntity<PagedModel<EntityModel<StockDTO>>> searchStocks(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<StockDTO> stocks = stockService.searchStocks(query, pageable);
        PagedModel<EntityModel<StockDTO>> pagedModel = pagedResourcesAssembler.toModel(stocks);
        return ResponseEntity.ok(pagedModel);
    }

    @Operation(summary = "Retrieve filtered stocks", description = "Retrieve a paginated list of stocks filtered by product name, supplier, quantity, and value.")
    @GetMapping("/filtered")
    public ResponseEntity<PagedModel<EntityModel<StockDTO>>> getFilteredStocks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) Integer minQuantity,
            @RequestParam(required = false) Integer maxQuantity,
            @RequestParam(required = false) Double minValue,
            @RequestParam(required = false) Double maxValue) {


        int maxQty = (maxQuantity != null) ? maxQuantity : (Integer) stockService.getMaxQuantity();
        double maxVal = (maxValue != null) ? maxValue : (Double) stockService.getMaxValue();
        int minQty = (minQuantity != null) ? minQuantity : 0;
        double minVal = (minValue != null) ? minValue : 0.0;


        Pageable pageable = PageRequest.of(page, size);


        Page<StockDTO> filteredStocks = stockService.getFilteredStocks(query, supplierId, minQty, maxQty, minVal, maxVal, pageable);

        PagedModel<EntityModel<StockDTO>> pagedModel = pagedResourcesAssembler.toModel(filteredStocks);
        return ResponseEntity.ok(pagedModel);
    }


    @Operation(summary = "Get min/max values for stock", description = "Retrieve minimum and maximum quantity and value in the stock")
    @GetMapping("/limits")
    public ResponseEntity<Map<String, Object>> getMinMaxLimits() {
        Object maxQuantity = stockService.getMaxQuantity();
        Object maxValue = stockService.getMaxValue();

        Map<String, Object> response = new HashMap<>();

        response.put("minQuantity", 0);
        response.put("maxQuantity", maxQuantity);

        response.put("minValue", 0);
        response.put("maxValue", maxValue);

        return ResponseEntity.ok(response);
    }


}
