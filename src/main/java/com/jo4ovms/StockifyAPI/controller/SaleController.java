package com.jo4ovms.StockifyAPI.controller;

import com.jo4ovms.StockifyAPI.model.DTO.BestSellingItemDTO;
import com.jo4ovms.StockifyAPI.model.DTO.SaleDTO;
import com.jo4ovms.StockifyAPI.model.DTO.SaleSummaryDTO;
import com.jo4ovms.StockifyAPI.service.SaleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping
    public ResponseEntity<SaleDTO> registerSale(@RequestBody SaleDTO saleDTO) {
        SaleDTO registeredSale = saleService.registerSale(saleDTO);
        return new ResponseEntity<>(registeredSale, HttpStatus.CREATED);
    }

    @GetMapping("/best-sellers")
    public ResponseEntity<List<BestSellingItemDTO>> getBestSellingItems() {
        List<BestSellingItemDTO> bestSellingItems = saleService.getBestSellingItems();
        return new ResponseEntity<>(bestSellingItems, HttpStatus.OK);
    }


    @GetMapping
    public ResponseEntity<Page<SaleSummaryDTO>> getAllSales(
            @RequestParam(required = false, defaultValue = "") String searchTerm,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(defaultValue = "desc") String sortDirection,
            Pageable pageable) {


        if (!sortDirection.equalsIgnoreCase("asc") && !sortDirection.equalsIgnoreCase("desc")) {
            throw new IllegalArgumentException("Invalid value '" + sortDirection + "' for sort direction. Must be 'asc' or 'desc'");
        }

        Page<SaleSummaryDTO> salesPage = saleService.getAllSalesGroupedByProduct(searchTerm, supplierId, pageable, sortDirection);
        return ResponseEntity.ok(salesPage);
    }



}
