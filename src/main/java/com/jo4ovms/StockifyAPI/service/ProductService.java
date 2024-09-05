package com.jo4ovms.StockifyAPI.service;

import com.jo4ovms.StockifyAPI.exception.ResourceNotFoundException;
import com.jo4ovms.StockifyAPI.exception.ValidationException;
import com.jo4ovms.StockifyAPI.mapper.ProductMapper;
import com.jo4ovms.StockifyAPI.model.DTO.ProductDTO;
import com.jo4ovms.StockifyAPI.model.DTO.SupplierDTO;
import com.jo4ovms.StockifyAPI.model.Product;
import com.jo4ovms.StockifyAPI.model.Supplier;
import com.jo4ovms.StockifyAPI.repository.ProductRepository;
import com.jo4ovms.StockifyAPI.repository.SupplierRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ProductMapper productMapper;

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


    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toProductDTO)
                .collect(Collectors.toList());
    }


    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));
        return productMapper.toProductDTO(product);
    }


    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));
        productRepository.delete(product);
    }
}