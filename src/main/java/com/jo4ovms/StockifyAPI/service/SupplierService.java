package com.jo4ovms.StockifyAPI.service;

import com.jo4ovms.StockifyAPI.exception.DuplicateResourceException;
import com.jo4ovms.StockifyAPI.exception.ResourceNotFoundException;
import com.jo4ovms.StockifyAPI.exception.ValidationException;
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

import java.util.List;


@Service
@Transactional
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private SupplierMapper supplierMapper;

    //@CachePut(value = "suppliers", key = "#result.id")
    public SupplierDTO createSupplier(SupplierDTO supplierDTO) {
        if (supplierRepository.existsByCnpj(supplierDTO.getCnpj())) {
            throw new DuplicateResourceException("Supplier with CNPJ " + supplierDTO.getCnpj() + " already exists.");
        }
        Supplier supplier = supplierMapper.toSupplier(supplierDTO);
        Supplier savedSupplier = supplierRepository.save(supplier);
        return supplierMapper.toSupplierDTO(savedSupplier);
    }

    //@CachePut(value = "suppliers", key = "#id")
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

    //@Cacheable(value = "suppliers", key = "#page + '-' + #size")
    public List<SupplierDTO> findAllSuppliers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Supplier> suppliers = supplierRepository.findAll(pageable);
        List<SupplierDTO> supplierDTOs = suppliers.stream()
                .map(supplierMapper::toSupplierDTO)
                .toList();

        return supplierDTOs;
    }

   // @Cacheable(value = "suppliers", key = "#id")
    public SupplierDTO findSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier with id " + id + " not found."));
        return supplierMapper.toSupplierDTO(supplier);
    }

   // @CacheEvict(value = "suppliers", key = "#id")
    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier with id " + id + " not found."));
        supplierRepository.delete(supplier);
    }


   // @Cacheable(value = "suppliersByName", key = "#name")
    public List<SupplierDTO> findSuppliersByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Name must not be null or empty.");
        }

        List<Supplier> suppliers = supplierRepository.findByNameContainingIgnoreCase(name);
        if (suppliers.isEmpty()) {
            throw new ResourceNotFoundException("No suppliers found with name containing: " + name);
        }

        return suppliers.stream().map(supplierMapper::toSupplierDTO).toList();
    }
}

