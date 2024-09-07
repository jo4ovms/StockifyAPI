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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockMapper stockMapper;

    @CacheEvict(value = "stocks", allEntries = true)
    public StockDTO createStock(StockDTO stockDTO) {
        Product product = productRepository.findById(stockDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + stockDTO.getProductId() + " not found"));

        Stock stock = stockMapper.toStock(stockDTO);
        stock.setProduct(product);
        Stock savedStock = stockRepository.save(stock);
        return stockMapper.toStockDTO(savedStock);
    }

    @CacheEvict(value = "stocks", allEntries = true)
    public StockDTO updateStock(Long id, StockDTO stockDTO) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock with id " + id + " not found"));

        Product product = productRepository.findById(stockDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + stockDTO.getProductId() + " not found"));

        stock.setQuantity(stockDTO.getQuantity());
        stock.setValue(stockDTO.getValue());
        stock.setProduct(product);

        Stock updatedStock = stockRepository.save(stock);
        return stockMapper.toStockDTO(updatedStock);
    }

    public Page<StockDTO> getAllStocks(Pageable pageable) {
        return stockRepository.findAll(pageable).map(stockMapper::toStockDTO);
    }

    @Cacheable(value = "stocks", key = "#id")
    public StockDTO getStockById(Long id) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock with id " + id + " not found"));
        return stockMapper.toStockDTO(stock);
    }

    @CacheEvict(value = "stocks", allEntries = true)
    public void deleteStock(Long id) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock with id " + id + " not found"));
        stockRepository.delete(stock);
    }
}
