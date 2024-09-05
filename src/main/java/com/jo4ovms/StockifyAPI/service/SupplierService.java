package com.jo4ovms.StockifyAPI.service;

import com.jo4ovms.StockifyAPI.exception.DuplicateResourceException;
import com.jo4ovms.StockifyAPI.exception.ResourceNotFoundException;
import com.jo4ovms.StockifyAPI.mapper.SupplierMapper;
import com.jo4ovms.StockifyAPI.model.DTO.SupplierDTO;
import com.jo4ovms.StockifyAPI.model.Supplier;
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


@Service
@Transactional
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private SupplierMapper supplierMapper;

    @CachePut(value = "suppliers", key = "#result.id")
    public SupplierDTO createSupplier(SupplierDTO supplierDTO) {
        if (supplierRepository.existsByCnpj(supplierDTO.getCnpj())) {
            throw new DuplicateResourceException("Supplier with CNPJ " + supplierDTO.getCnpj() + " already exists.");
        }
        Supplier supplier = supplierMapper.toSupplier(supplierDTO);
        Supplier savedSupplier = supplierRepository.save(supplier);
        return supplierMapper.toSupplierDTO(savedSupplier);
    }

    @CachePut(value = "suppliers", key = "#id")
    public SupplierDTO updateSupplier(Long id, SupplierDTO supplierDTO) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier with id " + id + " not found."));
        supplier.setName(supplierDTO.getName());
        supplier.setPhone(supplierDTO.getPhone());
        supplier.setEmail(supplierDTO.getEmail());
        supplier.setProductType(supplierDTO.getProductType());

        Supplier updatedSupplier = supplierRepository.save(supplier);
        return supplierMapper.toSupplierDTO(updatedSupplier);
    }

    @Cacheable(value = "suppliers")
    public Page<SupplierDTO> findAllSuppliers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Supplier> suppliers = supplierRepository.findAll(pageable);
        return suppliers.map(supplierMapper::toSupplierDTO);
    }

    @Cacheable(value = "suppliers", key = "#id")
    public SupplierDTO findSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier with id " + id + " not found."));
        return supplierMapper.toSupplierDTO(supplier);
    }

    @CacheEvict(value = "suppliers", key = "#id")
    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier with id " + id + " not found."));
        supplierRepository.delete(supplier);
    }
}

