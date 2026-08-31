package com.ecommerce.ProductService.repository;

import com.ecommerce.ProductService.dto.ProductUpdateRequestDTO;
import com.ecommerce.ProductService.entity.Product;
import com.ecommerce.ProductService.entity.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByCategory(ProductCategory category);
    List<Product> findByProductName(String productName);


}
