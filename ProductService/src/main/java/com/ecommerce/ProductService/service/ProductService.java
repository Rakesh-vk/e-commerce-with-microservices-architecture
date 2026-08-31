package com.ecommerce.ProductService.service;

import com.ecommerce.ProductService.dto.ProductCreateRequestDTO;
import com.ecommerce.ProductService.dto.ProductResponseDTO;
import com.ecommerce.ProductService.dto.ProductUpdateRequestDTO;
import com.ecommerce.ProductService.entity.Product;
import com.ecommerce.ProductService.entity.ProductCategory;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductResponseDTO updateProduct(ProductUpdateRequestDTO requestDTO);
    void decreaseStock(UUID productId, int quantity);
    public List<ProductResponseDTO> getAllProduct();
    public List<Product> getAllProductByCategory(ProductCategory category);
    public ProductResponseDTO getProductById(UUID id);
    public List<Product> getAllProductsByName(String name);
    public ProductResponseDTO addProduct(ProductCreateRequestDTO product);
    public void deleteProduct(Long productId);
}
