package com.ecommerce.ProductService.service;

import com.ecommerce.ProductService.entity.Product;
import com.ecommerce.ProductService.entity.ProductCategory;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    public List<Product> getAllProduct();
    public List<Product> getAllProductByCategory(ProductCategory category);
    public Product getProductById(UUID id);
    public List<Product> getAllProductsByName(String name);
    public Product addProduct(Product product);
    public void deleteProduct(Long productId);
}
