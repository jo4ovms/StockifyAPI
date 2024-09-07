package com.jo4ovms.StockifyAPI.controller;

import com.jo4ovms.StockifyAPI.model.DTO.StockDTO;
import com.jo4ovms.StockifyAPI.service.stock.StockReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/reports")
@Tag(name = "Stock Report", description = "Endpoints for generating stock reports")
public class StockReportController {

    private final StockReportService stockReportService;

    public StockReportController(StockReportService stockReportService) {
        this.stockReportService = stockReportService;
    }

    @Operation(summary = "Generate low stock report", description = "Generate a report for products with stock below a certain threshold.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Report generated",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @GetMapping("/low-stock")
    public ResponseEntity<Map<String, Object>> generateLowStockReport(@RequestParam int threshold) {
        // Validação do parâmetro threshold
        if (threshold <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Threshold must be greater than zero."));
        }

        List<StockDTO> report = stockReportService.generateLowStockReport(threshold);

        if (report.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "No products found below the specified threshold."));
        }

        // Adiciona informações extras ao retorno
        Map<String, Object> response = new HashMap<>();
        response.put("reportDate", LocalDate.now());
        response.put("totalProducts", report.size());
        response.put("products", report);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Generate high stock report", description = "Generate a report for products with stock above a certain threshold.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Report generated",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @GetMapping("/high-stock")
    public ResponseEntity<Map<String, Object>> generateHighStockReport(@RequestParam int threshold) {
        // Validação do parâmetro threshold
        if (threshold <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Threshold must be greater than zero."));
        }

        List<StockDTO> report = stockReportService.generateHighStockReport(threshold);

        if (report.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "No products found above the specified threshold."));
        }

        // Adiciona informações extras ao retorno
        Map<String, Object> response = new HashMap<>();
        response.put("reportDate", LocalDate.now());
        response.put("totalProducts", report.size());
        response.put("products", report);

        return ResponseEntity.ok(response);
    }
}