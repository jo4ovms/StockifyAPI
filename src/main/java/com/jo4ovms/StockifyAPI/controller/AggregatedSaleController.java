package com.jo4ovms.StockifyAPI.controller;

import com.jo4ovms.StockifyAPI.model.DTO.SaleSummaryDTO;
import com.jo4ovms.StockifyAPI.service.AggregatedSaleService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/aggregated-sales")
public class AggregatedSaleController {
    private final AggregatedSaleService aggregatedSaleService;

    public AggregatedSaleController(AggregatedSaleService aggregatedSaleService) {
        this.aggregatedSaleService = aggregatedSaleService;
    }

    @GetMapping
    public ResponseEntity<Page<SaleSummaryDTO>> getAllAggregatedSales(
            @RequestParam(required = false, defaultValue = "") String searchTerm,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Page<SaleSummaryDTO> salesPage = aggregatedSaleService.getAllAggregatedSales(searchTerm, supplierId, page, size, sortDirection);
        return ResponseEntity.ok(salesPage);
    }
}
