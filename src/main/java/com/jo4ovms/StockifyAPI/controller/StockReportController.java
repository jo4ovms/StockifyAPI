package com.jo4ovms.StockifyAPI.controller;

import com.jo4ovms.StockifyAPI.model.DTO.StockDTO;
import com.jo4ovms.StockifyAPI.model.DTO.StockMovementDTO;
import com.jo4ovms.StockifyAPI.service.stock.StockReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stock/reports")
@Tag(name = "Stock Report", description = "Endpoints for generating stock reports")
public class StockReportController {

    private final StockReportService stockReportService;
    private final PagedResourcesAssembler<StockDTO> stockPagedResourcesAssembler;
    private final PagedResourcesAssembler<StockMovementDTO> movementPagedResourcesAssembler;

    public StockReportController(StockReportService stockReportService,
                                 PagedResourcesAssembler<StockDTO> stockPagedResourcesAssembler,
                                 PagedResourcesAssembler<StockMovementDTO> movementPagedResourcesAssembler) {
        this.stockReportService = stockReportService;
        this.stockPagedResourcesAssembler = stockPagedResourcesAssembler;
        this.movementPagedResourcesAssembler = movementPagedResourcesAssembler;
    }

    @Operation(summary = "Generate low stock report", description = "Generate a report for products with stock below a certain threshold.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Report generated",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @GetMapping("/low-stock")
    public ResponseEntity<?> generateLowStockReport(
            @RequestParam int threshold,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (threshold <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Threshold must be greater than zero."));
        }

        PageRequest pageable = PageRequest.of(page, size);
        Page<StockDTO> report = stockReportService.generateLowStockReport(threshold, pageable);

        if (report.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "No products found below the specified threshold."));
        }

        PagedModel<EntityModel<StockDTO>> pagedModel = stockPagedResourcesAssembler.toModel(report,
                stockDTO -> EntityModel.of(stockDTO,
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(StockReportController.class)
                                        .generateLowStockReport(threshold, page, size))
                                .withSelfRel()));

        return ResponseEntity.ok(pagedModel);
    }

    @Operation(summary = "Generate high stock report", description = "Generate a report for products with stock above a certain threshold.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Report generated",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @GetMapping("/high-stock")
    public ResponseEntity<?> generateHighStockReport(
            @RequestParam int threshold,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (threshold <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Threshold must be greater than zero."));
        }

        PageRequest pageable = PageRequest.of(page, size);
        Page<StockDTO> report = stockReportService.generateHighStockReport(threshold, pageable);

        if (report.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "No products found above the specified threshold."));
        }

        PagedModel<EntityModel<StockDTO>> pagedModel = stockPagedResourcesAssembler.toModel(report,
                stockDTO -> EntityModel.of(stockDTO,
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(StockReportController.class)
                                        .generateHighStockReport(threshold, page, size))
                                .withSelfRel()));

        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/movements-report")
    public ResponseEntity<?> generateStockMovementReport(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageRequest pageable = PageRequest.of(page, size);
        Page<StockMovementDTO> report = stockReportService.generateStockMovementReport(startDate, endDate, pageable);

        if (report.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "No stock movements found for the specified period."));
        }

        PagedModel<EntityModel<StockMovementDTO>> pagedModel = movementPagedResourcesAssembler.toModel(report,
                stockMovementDTO -> EntityModel.of(stockMovementDTO,
                        WebMvcLinkBuilder.linkTo(
                                        WebMvcLinkBuilder.methodOn(StockReportController.class)
                                                .generateStockMovementReport(startDate, endDate, page, size))
                                .withSelfRel()));

        return ResponseEntity.ok(pagedModel);
    }

    @Operation(summary = "Generate out of stock report", description = "Generate a paginated report for products that are out of stock.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Report generated", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @GetMapping("/out-of-stock")
    public ResponseEntity<?> generateOutOfStockReport(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            PagedResourcesAssembler<StockDTO> pagedResourcesAssembler) {

        Pageable pageable = PageRequest.of(page, size);
        Page<StockDTO> report = stockReportService.getOutOfStockProducts(pageable);

        if (report.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "No products are currently out of stock."));
        }

        PagedModel<EntityModel<StockDTO>> pagedModel = pagedResourcesAssembler.toModel(report);
        return ResponseEntity.ok(pagedModel);
    }

}