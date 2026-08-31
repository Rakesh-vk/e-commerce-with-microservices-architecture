package com.ecommerce.ProductService.service;

import com.ecommerce.ProductService.dto.ProductCreateRequestDTO;
import com.ecommerce.ProductService.dto.ProductResponseDTO;
import com.ecommerce.ProductService.dto.ProductUpdateRequestDTO;
import com.ecommerce.ProductService.entity.Product;
import com.ecommerce.ProductService.entity.ProductCategory;
import com.ecommerce.ProductService.exception.InsufficientStockException;
import com.ecommerce.ProductService.exception.ProductNotFoundException;
import com.ecommerce.ProductService.mapper.ProductMapper;
import com.ecommerce.ProductService.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public List<ProductResponseDTO> getAllProduct() {
        log.info("Fetching all products");

        List<ProductResponseDTO> products = productRepository.findAll().stream()
                .map(ProductMapper::toResponse)
                .toList();

        log.info("Fetched {} products", products.size());
        return products;
    }

    @Override
    public List<Product> getAllProductByCategory(ProductCategory category) {
        log.info("Fetching products by category: {}", category);

        List<Product> products = productRepository.findByCategory(category);

        log.info("Fetched {} products for category: {}", products.size(), category);
        return products;
    }

    @Override
    public ProductResponseDTO getProductById(UUID id) {
        log.info("Fetching product with id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found with id: {}", id);
                    return new ProductNotFoundException("Product not found with id: " + id);
                });

        log.info("Product found with id: {}", id);
        return ProductMapper.toResponse(product);
    }

    @Override
    public List<Product> getAllProductsByName(String name) {
        log.info("Fetching products by name: {}", name);

        List<Product> products = productRepository.findByProductName(name);

        log.info("Fetched {} products with name: {}", products.size(), name);
        return products;
    }

    @Override
    public ProductResponseDTO updateProduct(ProductUpdateRequestDTO requestDTO) {
        log.info("Updating product with id: {}", requestDTO.id());

        Product product = productRepository.findById(requestDTO.id())
                .orElseThrow(() -> {
                    log.warn("Cannot update. Product not found with id: {}", requestDTO.id());
                    return new ProductNotFoundException("Product not found with id: " + requestDTO.id());
                });

        product.setProductName(requestDTO.productName());
        product.setCategory(requestDTO.category());
        product.setPrice(requestDTO.price());
        product.setStockQty(requestDTO.stockQty());

        Product updatedProduct = productRepository.save(product);

        log.info("Product updated successfully with id: {}", updatedProduct.getId());
        return ProductMapper.toResponse(updatedProduct);
    }

    @Override
    public ProductResponseDTO addProduct(ProductCreateRequestDTO requestDTO) {
        log.info("Adding new product with name: {}", requestDTO.productName());

        Product product = ProductMapper.toEntity(requestDTO);
        Product savedProduct = productRepository.save(product);

        log.info("Product added successfully with id: {}", savedProduct.getId());
        return ProductMapper.toResponse(savedProduct);
    }

    @Override
    public void deleteProduct(Long productId) {
        log.info("deleteProduct called with product id: {}", productId);
    }

    @Override
    @Transactional
    public void decreaseStock(UUID productId, int quantity) {
        log.info("Decreasing stock for product id: {}, quantity: {}", productId, quantity);

        int updatedRows = productRepository.decreaseStock(productId, quantity);

        if (updatedRows == 1) {
            log.info("Stock decreased successfully for product id: {}", productId);
            return;
        }

        if (!productRepository.existsById(productId)) {
            log.warn("Cannot decrease stock. Product not found with id: {}", productId);
            throw new ProductNotFoundException("Product does not exist");
        }

        log.warn("Insufficient stock for product id: {}, requested quantity: {}", productId, quantity);
        throw new InsufficientStockException(
                "Insufficient stock for product " + productId
        );
    }
}