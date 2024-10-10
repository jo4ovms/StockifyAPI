package com.jo4ovms.StockifyAPI.service.stock;

import com.jo4ovms.StockifyAPI.exception.ResourceNotFoundException;
import com.jo4ovms.StockifyAPI.mapper.StockMapper;
import com.jo4ovms.StockifyAPI.model.DTO.StockDTO;
import com.jo4ovms.StockifyAPI.model.Product;
import com.jo4ovms.StockifyAPI.model.Stock;
import com.jo4ovms.StockifyAPI.repository.ProductRepository;
import com.jo4ovms.StockifyAPI.repository.StockRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;


import java.util.List;

import java.util.stream.Collectors;


@Service
@Transactional
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockMapper stockMapper;

   // @CacheEvict(value = "stocks", allEntries = true)
   public StockDTO createStock(StockDTO stockDTO) {
       Product product = productRepository.findById(stockDTO.getProductId())
               .orElseThrow(() -> new ResourceNotFoundException("Product with id " + stockDTO.getProductId() + " not found"));

       Stock stock = stockMapper.toStock(stockDTO);
       stock.setProduct(product);


       stock.setAvailable(stockDTO.getQuantity() > 0);

       Stock savedStock = stockRepository.save(stock);

       return stockMapper.toStockDTO(savedStock);
   }

   // @CacheEvict(value = "stocks", allEntries = true)
   public StockDTO updateStock(Long id, StockDTO stockDTO) {
       Stock stock = stockRepository.findById(id)
               .orElseThrow(() -> new ResourceNotFoundException("Stock with id " + id + " not found"));

       Product product = productRepository.findById(stockDTO.getProductId())
               .orElseThrow(() -> new ResourceNotFoundException("Product with id " + stockDTO.getProductId() + " not found"));

       stock.setQuantity(stockDTO.getQuantity());
       stock.setValue(stockDTO.getValue());
       stock.setProduct(product);


       stock.setAvailable(stockDTO.getQuantity() > 0);

       Stock updatedStock = stockRepository.save(stock);

       return stockMapper.toStockDTO(updatedStock);
   }

    public Page<StockDTO> getAllStocks(Pageable pageable) {
        return stockRepository.findAll(pageable).map(stock -> {
            StockDTO stockDTO = stockMapper.toStockDTO(stock);
            stockDTO.setAvailable(stockDTO.getQuantity() > 0); // Set available based on quantity
            return stockDTO;
        });
    }

   // @Cacheable(value = "stocks", key = "#id")
   public StockDTO getStockById(Long id) {
       Stock stock = stockRepository.findById(id)
               .orElseThrow(() -> new ResourceNotFoundException("Stock with id " + id + " not found"));

       StockDTO stockDTO = stockMapper.toStockDTO(stock);
       stockDTO.setAvailable(stockDTO.getQuantity() > 0);

       return stockDTO;
   }

   // @CacheEvict(value = "stocks", allEntries = true)
    @Transactional
   public void deleteStock(Long id) {
       Stock stock = stockRepository.findById(id)
               .orElseThrow(() -> new ResourceNotFoundException("Stock with id " + id + " not found"));
       stockRepository.delete(stock);
        stockRepository.flush();
   }

    public Page<StockDTO> getStocksBySupplier(Long supplierId, Pageable pageable) {
        return stockRepository.findByProductSupplierId(supplierId, pageable)
                .map(stockMapper::toStockDTO);
    }

    public Page<StockDTO> searchStocks(String query, Pageable pageable) {
        if (query == null || query.isEmpty()) {
            return stockRepository.findAll(pageable).map(stockMapper::toStockDTO);
        }
        return stockRepository.searchByProductNameOrSupplier(query, pageable)
                .map(stockMapper::toStockDTO);
    }

    public Page<StockDTO> getFilteredStocks(String query, Long supplierId, int minQuantity, int maxQuantity, double minValue, double maxValue, Pageable pageable) {
        if (query != null && supplierId != null) {

            return stockRepository.searchByProductNameAndSupplierAndQuantityAndValue(query, supplierId, minQuantity, maxQuantity, minValue, maxValue, pageable)
                    .map(stockMapper::toStockDTO);
        } else if (query != null) {

            return stockRepository.searchByProductNameAndQuantityAndValue(query, minQuantity, maxQuantity, minValue, maxValue, pageable)
                    .map(stockMapper::toStockDTO);
        } else if (supplierId != null) {

            return stockRepository.findBySupplierAndQuantityAndValue(supplierId, minQuantity, maxQuantity, minValue, maxValue, pageable)
                    .map(stockMapper::toStockDTO);
        } else {

            return stockRepository.findByQuantityAndValueRange(minQuantity, maxQuantity, minValue, maxValue, pageable)
                    .map(stockMapper::toStockDTO);
        }
    }

    public Object getMaxQuantity() {
        return stockRepository.findMaxQuantity();
    }

    public Object getMaxValue() {
        return stockRepository.findMaxValue();
    }

    public List<StockDTO> getAllStocksNonPaged() {

        return stockRepository.findAll().stream()
                .map(stockMapper::toStockDTO)
                .collect(Collectors.toList());
    }

}
