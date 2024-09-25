package com.jo4ovms.StockifyAPI.mapper;

import com.jo4ovms.StockifyAPI.model.DTO.ProductDTO;
import com.jo4ovms.StockifyAPI.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "supplier.name", target = "supplierName")
    @Mapping(source = "supplier.id", target = "supplierId")
    @Mapping(source = "quantity", target = "quantity")
    ProductDTO toProductDTO(Product product);

    @Mapping(source = "supplierId", target = "supplier.id")
    Product toProduct(ProductDTO productDTO);
}
