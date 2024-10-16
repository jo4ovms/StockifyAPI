package com.jo4ovms.StockifyAPI.controller;

import com.jo4ovms.StockifyAPI.model.DTO.StockMovementDTO;
import com.jo4ovms.StockifyAPI.service.stock.StockMovementService;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock-movements")
@Tag(name = "Stock Movement", description = "Operations related to stock movements")
public class StockMovementController {

    private final StockMovementService stockMovementService;
    private final PagedResourcesAssembler<StockMovementDTO> pagedResourcesAssembler;

    public StockMovementController(StockMovementService stockMovementService, PagedResourcesAssembler<StockMovementDTO> pagedResourcesAssembler) {
        this.stockMovementService = stockMovementService;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(summary = "Register a new stock movement", description = "Registers a new stock movement.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Stock movement registered successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StockMovementDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PostMapping
   // @CacheEvict(value = "stockMovements", allEntries = true)
    public ResponseEntity<StockMovementDTO> registerMovement(

            @Valid @RequestBody StockMovementDTO stockMovementDTO) {
        StockMovementDTO registeredMovement = stockMovementService.registerMovement(stockMovementDTO);
        return new ResponseEntity<>(registeredMovement, HttpStatus.CREATED);
    }

    @Operation(summary = "Retrieve stock movement details", description = "Retrieve the details of a stock movement by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock movement details retrieved successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StockMovementDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Stock movement not found", content = @Content)
    })
    @GetMapping("/{id}")
   // @Cacheable(value = "stockMovements", key = "#id")
    public ResponseEntity<StockMovementDTO> getStockMovementById(
            @PathVariable Long id) {
        StockMovementDTO stockMovement = stockMovementService.getStockMovementById(id);
        return ResponseEntity.ok(stockMovement);
    }

    @Operation(summary = "Retrieve all stock movements", description = "Retrieve a list of all stock movements.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock movements retrieved successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class)) })
    })
    @GetMapping
    //@Cacheable(value = "stockMovements")
    public ResponseEntity<PagedModel<EntityModel<StockMovementDTO>>> getAllStockMovements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<StockMovementDTO> stockMovements = stockMovementService.getAllStockMovements(pageable);
        PagedModel<EntityModel<StockMovementDTO>> pagedModel = pagedResourcesAssembler.toModel(stockMovements);
        return ResponseEntity.ok(pagedModel);
    }
}
