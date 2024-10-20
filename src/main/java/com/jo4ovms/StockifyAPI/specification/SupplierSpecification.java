package com.jo4ovms.StockifyAPI.specification;

import com.jo4ovms.StockifyAPI.model.Supplier;
import org.springframework.data.jpa.domain.Specification;

public class SupplierSpecification {
    public static Specification<Supplier> hasName(String name) {
        return (root, query, criteriaBuilder) ->
                name == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Supplier> hasProductType(String productType) {
        return (root, query, criteriaBuilder) -> {

            if (productType == null || productType.isEmpty() || "All".equalsIgnoreCase(productType)) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("productType"), productType);
        };
    }


}

