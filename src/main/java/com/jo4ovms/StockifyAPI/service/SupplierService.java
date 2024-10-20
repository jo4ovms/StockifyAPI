package com.jo4ovms.StockifyAPI.service;

import com.jo4ovms.StockifyAPI.exception.DuplicateResourceException;
import com.jo4ovms.StockifyAPI.exception.ResourceNotFoundException;
import com.jo4ovms.StockifyAPI.mapper.SupplierMapper;
import com.jo4ovms.StockifyAPI.model.DTO.LogDTO;
import com.jo4ovms.StockifyAPI.model.DTO.SupplierDTO;
import com.jo4ovms.StockifyAPI.model.Supplier;
import com.jo4ovms.StockifyAPI.repository.SupplierRepository;
import com.jo4ovms.StockifyAPI.specification.SupplierSpecification;
import com.jo4ovms.StockifyAPI.util.LogUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.jo4ovms.StockifyAPI.model.Log.OperationType;
import java.util.List;


@Service
@Transactional
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;
    private final LogService logService;
    private final LogUtils logUtils;

    @Autowired
    public SupplierService(SupplierRepository supplierRepository, SupplierMapper supplierMapper, LogService logService, LogUtils logUtils) {
        this.supplierRepository = supplierRepository;
        this.supplierMapper = supplierMapper;
        this.logService = logService;
        this.logUtils = logUtils;
    }


    //@CachePut(value = "suppliers", key = "#result.id")
    public SupplierDTO createSupplier(SupplierDTO supplierDTO) {
        if (supplierRepository.existsByCnpj(supplierDTO.getCnpj())) {
            throw new DuplicateResourceException("Supplier with CNPJ " + supplierDTO.getCnpj() + " already exists.");
        }
        Supplier supplier = supplierMapper.toSupplier(supplierDTO);
        Supplier savedSupplier = supplierRepository.save(supplier);


        LogDTO logDTO = new LogDTO();
        logDTO.setTimestamp(savedSupplier.getCreatedAt());
         logUtils.populateLog(logDTO, "Supplier", savedSupplier.getId(), OperationType.CREATE.toString(),
                 supplierMapper.toSupplierDTO(savedSupplier), null, "Created new supplier");

         logService.createLog(logDTO);
        return supplierMapper.toSupplierDTO(savedSupplier);
    }

    //@CachePut(value = "suppliers", key = "#id")
    public SupplierDTO updateSupplier(Long id, SupplierDTO supplierDTO) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier with id " + id + " not found."));

        SupplierDTO oldSupplierDTO = supplierMapper.toSupplierDTO(supplier);


        boolean hasChanges = false;

        if (!supplierDTO.getName().equals(supplier.getName())) {
            supplier.setName(supplierDTO.getName());
            hasChanges = true;
        }

        if (!supplierDTO.getPhone().equals(supplier.getPhone())) {
            supplier.setPhone(supplierDTO.getPhone());
            hasChanges = true;
        }

        if (!supplierDTO.getEmail().equals(supplier.getEmail())) {
            supplier.setEmail(supplierDTO.getEmail());
            hasChanges = true;
        }

        if (!supplierDTO.getProductType().equals(supplier.getProductType())) {
            supplier.setProductType(supplierDTO.getProductType());
            hasChanges = true;
        }

        if (!hasChanges) {
            return supplierMapper.toSupplierDTO(supplier);
        }
        Supplier updatedSupplier = supplierRepository.save(supplier);


        LogDTO logDTO = new LogDTO();
        logDTO.setTimestamp(updatedSupplier.getUpdatedAt());
        logUtils.populateLog(logDTO, "Supplier", updatedSupplier.getId(), OperationType.UPDATE.toString(),
                supplierMapper.toSupplierDTO(updatedSupplier), oldSupplierDTO, "Updated supplier");


        logService.createLog(logDTO);

        return supplierMapper.toSupplierDTO(updatedSupplier);
    }


    //@Cacheable(value = "suppliers", key = "#page + '-' + #size")
    public Page<SupplierDTO> findAllSuppliers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Supplier> suppliers = supplierRepository.findAll(pageable);
        return suppliers.map(supplierMapper::toSupplierDTO);
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

       SupplierDTO oldSupplierDTO = supplierMapper.toSupplierDTO(supplier);
       supplierRepository.delete(supplier);


       LogDTO logDTO = new LogDTO();
       logDTO.setTimestamp(supplier.getUpdatedAt());
       logUtils.populateLog(logDTO, "Supplier", supplier.getId(), OperationType.DELETE.toString(),
               null, oldSupplierDTO, "Deleted supplier");

       logService.createLog(logDTO);
   }


   // @Cacheable(value = "suppliersByName", key = "#name")
   public Page<SupplierDTO> findSuppliersByName(String name, int page, int size) {
       Pageable pageable = PageRequest.of(page, size);
       Page<Supplier> suppliers = supplierRepository.findByNameContainingIgnoreCase(name, pageable);
       if (suppliers.isEmpty()) {
           throw new ResourceNotFoundException("No suppliers found with name containing: " + name);
       }
       return suppliers.map(supplierMapper::toSupplierDTO);
   }

    public Page<SupplierDTO> filterSuppliers(String name, String productType, int page, int size, String sortBy, String sortDirection) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), sortBy));
        Specification<Supplier> specification = Specification.where(SupplierSpecification.hasName(name))
                .and(SupplierSpecification.hasProductType(productType));

        Page<Supplier> suppliers = supplierRepository.findAll(specification, pageable);

            return suppliers.map(supplierMapper::toSupplierDTO);


    }


    public List<String> findAllProductTypes() {
        return supplierRepository.findDistinctProductTypes();
    }


    public List<SupplierDTO> findAll() {
        return supplierRepository.findAll().stream().map(supplierMapper::toSupplierDTO).toList();
    }
}

