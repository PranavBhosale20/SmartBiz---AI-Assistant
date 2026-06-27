package com.smartbiz.service;

import com.smartbiz.dto.ProductMapper;
import com.smartbiz.dto.ProductRequestDTO;
import com.smartbiz.dto.ProductResponseDTO;
import com.smartbiz.model.Product;
import com.smartbiz.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponseDTO addProduct(ProductRequestDTO dto) {
        // --- Business Rule: no negative quantity or price ---
        // Doesn't make sense to have -5 units of medicine in stock,
        // or a product priced at -100. Catching this here, before
        // it ever reaches the database, keeps bad data out entirely.
        if (dto.getQuantity() < 0) {
            throw new RuntimeException("Product quantity cannot be negative!");
        }
        if (dto.getPrice() < 0) {
            throw new RuntimeException("Product price cannot be negative!");
        }

        Product product = ProductMapper.toEntity(dto);
        Product saved = productRepository.save(product);
        return ProductMapper.toResponseDTO(saved);
    }

    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(ProductMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        return ProductMapper.toResponseDTO(product);
    }

    public List<ProductResponseDTO> getProductsByCategory(String category) {
        return productRepository.findByCategory(category)
                .stream()
                .map(ProductMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ProductResponseDTO> searchProducts(String name) {
        return productRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(ProductMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ProductResponseDTO> getAvailableProducts() {
        // Only products with stock left - quantity strictly greater
        // than 0. Sold-out items won't show up in this list.
        return productRepository.findByQuantityGreaterThan(0)
                .stream()
                .map(ProductMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO dto) {
        if (dto.getQuantity() < 0) {
            throw new RuntimeException("Product quantity cannot be negative!");
        }
        if (dto.getPrice() < 0) {
            throw new RuntimeException("Product price cannot be negative!");
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        product.setName(dto.getName());
        product.setCategory(dto.getCategory());
        product.setQuantity(dto.getQuantity());
        product.setPrice(dto.getPrice());

        Product updated = productRepository.save(product);
        return ProductMapper.toResponseDTO(updated);
    }

    public void deleteProduct(Long id) {
        productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        productRepository.deleteById(id);
    }
}