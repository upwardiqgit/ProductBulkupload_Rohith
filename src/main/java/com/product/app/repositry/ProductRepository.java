package com.product.app.repositry;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.product.app.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Correct query method for multiple fields
    List<Product> findByProductNameContainingAndProductTypeContaining(String productName, String productType);
    
    @Query("SELECT DISTINCT p.productType FROM Product p")
    List<String> findDistinctProductTypes();
    
 // Find products by product name
    List<Product> findByProductName(String productName);
    
    // Find products by product type
    List<Product> findByProductType(String productType);

}
