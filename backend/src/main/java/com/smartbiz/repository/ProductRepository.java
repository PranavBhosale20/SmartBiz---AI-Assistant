package com.smartbiz.repository;

import com.smartbiz.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

// Just like UserRepository and AppointmentRepository, extending
// JpaRepository<Product, Long> gives us save(), findAll(), findById(),
// deleteById() etc. for free - no SQL written by us at all.
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Lets staff filter inventory by category (e.g. show only
    // "Medicine" items). Spring reads the method name and builds:
    // SELECT * FROM products WHERE category = ?
    List<Product> findByCategory(String category);

    // Powers a search bar - "ContainingIgnoreCase" means it matches
    // partial text anywhere in the name, regardless of upper/lower
    // case. Searching "para" would still find "Paracetamol".
    List<Product> findByNameContainingIgnoreCase(String name);

    // Used for an "available products" list - only show items that
    // actually have stock left (quantity > 0), hiding sold-out ones.
    List<Product> findByQuantityGreaterThan(int quantity);
}