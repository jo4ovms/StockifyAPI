package com.jo4ovms.StockifyAPI.service;

import com.jo4ovms.StockifyAPI.exception.ResourceNotFoundException;
import com.jo4ovms.StockifyAPI.exception.ValidationException;
import com.jo4ovms.StockifyAPI.mapper.ProductMapper;
import com.jo4ovms.StockifyAPI.model.DTO.ProductDTO;
import com.jo4ovms.StockifyAPI.model.Product;
import com.jo4ovms.StockifyAPI.model.Supplier;
import com.jo4ovms.StockifyAPI.repository.ProductRepository;
import com.jo4ovms.StockifyAPI.repository.SupplierRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ProductMapper productMapper;

    //@CachePut(value = "products", key = "#result.id")
    public ProductDTO createProduct(ProductDTO productDTO) {
        Supplier supplier = supplierRepository.findById(productDTO.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier with id " + productDTO.getSupplierId() + " not found."));
        if (productDTO.getValue() <= 0) {
            throw new ValidationException("Product value must be greater than zero.");
        }

        Product product = productMapper.toProduct(productDTO);
        product.setSupplier(supplier);
        Product savedProduct = productRepository.save(product);
        return productMapper.toProductDTO(savedProduct);
    }

    //@CachePut(value = "products", key = "#id")
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));
        Supplier supplier = supplierRepository.findById(productDTO.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier with id " + productDTO.getSupplierId() + " not found"));

        product.setName(productDTO.getName());
        product.setValue(productDTO.getValue());
        product.setQuantity(productDTO.getQuantity());
        product.setSupplier(supplier);

        Product updatedProduct = productRepository.save(product);
        return productMapper.toProductDTO(updatedProduct);
    }

   // @Cacheable(value = "products")
    public Page<ProductDTO> findAllProducts(int page, int size, String nameFilter) {
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("Page number or size must not be less than zero.");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products;

        if (nameFilter != null && !nameFilter.isEmpty()) {

            products = productRepository.findByNameContainingIgnoreCase(nameFilter, pageable);
        } else {
            products = productRepository.findAll(pageable);
        }

        return products.map(productMapper::toProductDTO);
    }

    //@Cacheable(value = "products", key = "#id")
    public ProductDTO findProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));
        return productMapper.toProductDTO(product);
    }

   // @CacheEvict(value = "products", key = "#id")
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));
        productRepository.delete(product);
    }


   // @Cacheable(value = "productsByQuantity", key = "#quantity")
   public Page<ProductDTO> findProductsBySupplier(Long supplierId, int page, int size) {
       Supplier supplier = supplierRepository.findById(supplierId)
               .orElseThrow(() -> new ResourceNotFoundException("Supplier with id " + supplierId + " not found."));

       Pageable pageable = PageRequest.of(page, size);
       Page<Product> products = productRepository.findBySupplier(supplier, pageable);

       return products.map(productMapper::toProductDTO);
   }
}