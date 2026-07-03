package com.smartbiz.dto;

import com.smartbiz.model.Product;

// This mapper doesn't need to look anything up in the database -
// Product has no relationships to other entities (unlike Appointment,
// which needed to fetch a User). That means it can stay a simple
// utility class with static methods, no @Component, no constructor,
// no Spring involvement at all. Just plain field-to-field copying.
public class ProductMapper {

    public static Product toEntity(ProductRequestDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setCategory(dto.getCategory());
        product.setQuantity(dto.getQuantity());
        product.setPrice(dto.getPrice());
        return product;
    }

    public static ProductResponseDTO toResponseDTO(Product product) {
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getCategory(),
                product.getQuantity(),
                product.getPrice(),
                product.getCreatedAt()
        );
    }
}