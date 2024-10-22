package com.jo4ovms.StockifyAPI.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jo4ovms.StockifyAPI.exception.ResourceNotFoundException;
import com.jo4ovms.StockifyAPI.mapper.SaleMapper;
import com.jo4ovms.StockifyAPI.model.DTO.*;
import com.jo4ovms.StockifyAPI.model.Log;
import com.jo4ovms.StockifyAPI.model.Sale;
import com.jo4ovms.StockifyAPI.model.Stock;
import com.jo4ovms.StockifyAPI.repository.AggregatedSaleRepository;
import com.jo4ovms.StockifyAPI.repository.SaleRepository;
import com.jo4ovms.StockifyAPI.repository.StockRepository;
import com.jo4ovms.StockifyAPI.util.LogUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SaleService {

    private final StockRepository stockRepository;
    private final SaleMapper saleMapper;
    private final SaleRepository saleRepository;
    private final LogService logService;
    private final AggregatedSaleService aggregatedSaleService;
    private final AggregatedSaleRepository aggregatedSaleRepository;
    private final LogUtils logUtils;


    @Autowired
    public SaleService(StockRepository stockRepository, SaleMapper saleMapper, SaleRepository saleRepository, LogService logService, AggregatedSaleService aggregatedSaleService, AggregatedSaleRepository aggregatedSaleRepository, LogUtils logUtils) {
        this.stockRepository = stockRepository;
        this.saleMapper = saleMapper;
        this.saleRepository = saleRepository;
        this.logService = logService;
        this.aggregatedSaleService = aggregatedSaleService;
        this.aggregatedSaleRepository = aggregatedSaleRepository;
        this.logUtils = logUtils;
    }

    @Transactional
    public SaleDTO registerSale(SaleDTO saleDTO) {

        Stock stock = stockRepository.findByProductId(saleDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Stock for product ID " + saleDTO.getProductId() + " not found"));


        if (stock.getQuantity() < saleDTO.getQuantity()) {
            throw new IllegalArgumentException("Insufficient stock. Requested quantity exceeds available stock.");
        }


        stock.setQuantity(stock.getQuantity() - saleDTO.getQuantity());
        stock.setAvailable(stock.getQuantity() > 0);
        stockRepository.save(stock);


        Sale sale = new Sale();
        sale.setProduct(stock.getProduct());
        sale.setQuantity(saleDTO.getQuantity());
        sale.setStockValueAtSale(stock.getValue());


        Sale savedSale = saleRepository.save(sale);


        SaleDTO saleLogDTO = saleMapper.toSaleDTO(savedSale);
        saleLogDTO.setProductId(stock.getProduct().getId());
        saleLogDTO.setProductName(stock.getProduct().getName());
        saleLogDTO.setStockValueAtSale(stock.getValue());


        LogDTO logDTO = new LogDTO();
        logDTO.setTimestamp(savedSale.getSaleDate());
        logUtils.populateLog(logDTO, "Sale", savedSale.getId(), Log.OperationType.CREATE.toString(),
                saleLogDTO, null, "Sale registered: Product ID " + stock.getProduct().getId() +
                        ", Quantity: " + saleDTO.getQuantity());


        logService.createLog(logDTO);


        aggregatedSaleService.updateAggregatedSales(stock.getId(), stock.getProduct().getId(), saleDTO.getQuantity().longValue());

        return saleLogDTO;
    }



    public List<BestSellingItemDTO> getBestSellingItems() {
        return saleRepository.findBestSellingItems();
    }



    public Page<SaleSummaryDTO> getAllSalesGroupedByProduct(String searchTerm, Long supplierId, int page, int size, String sortDirection, LocalDate startDate, LocalDate endDate) {
        Sort sort = Sort.by("totalQuantitySold");
        sort = "asc".equalsIgnoreCase(sortDirection) ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(page, size, sort);


        if (startDate != null && endDate != null) {
            return aggregatedSaleRepository.findSalesGroupedByProductAndSupplierAndDate(searchTerm, supplierId, startDate, endDate, pageable);
        } else {
            return aggregatedSaleRepository.findSalesGroupedByProductAndSupplier(searchTerm, supplierId, pageable);
        }
    }

    public List<DailySalesDTO> getSalesGroupedByDay(int month) {
        return saleRepository.findSalesGroupedByDay(month);
    }







}
