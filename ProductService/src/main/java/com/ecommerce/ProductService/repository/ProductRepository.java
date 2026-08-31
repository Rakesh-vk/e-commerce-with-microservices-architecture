package com.ecommerce.ProductService.repository;

import com.ecommerce.ProductService.entity.Product;
import com.ecommerce.ProductService.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByCategory(ProductCategory category);
    List<Product> findByProductName(String productName);
    @Modifying
    @Query("""
        UPDATE Product p
        SET p.stockQty = p.stockQty - :quantity
        WHERE p.id = :productId
          AND p.stockQty >= :quantity
    """)
    int decreaseStock(
            @Param("productId") UUID productId,
            @Param("quantity") int quantity
    );
}
