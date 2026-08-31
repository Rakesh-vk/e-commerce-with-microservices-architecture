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
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements  ProductService{
    private final ProductRepository productRepository;

    @Override
    public List<ProductResponseDTO> getAllProduct() {
        return productRepository.findAll().stream()
                .map(ProductMapper::toResponse)
                .toList();
    }

    @Override
    public List<Product> getAllProductByCategory(ProductCategory category) {
        return productRepository.findByCategory(category);
    }

    @Override
    public ProductResponseDTO getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException("Product does not exist")
                );

        return ProductMapper.toResponse(product);
    }

    @Override
    public List<Product> getAllProductsByName(String name) {
        return productRepository.findByProductName(name);
    }
    public ProductResponseDTO updateProduct(ProductUpdateRequestDTO requestDTO){
        Product product = productRepository.findById(requestDTO.id())
                .orElseThrow(() ->
                        new ProductNotFoundException("Product not found")
                );
        product.setProductName(requestDTO.productName());
        product.setCategory(requestDTO.category());
        product.setPrice(requestDTO.price());
        product.setStockQty(requestDTO.stockQty());

        Product updatedProduct = productRepository.save(product);

        return ProductMapper.toResponse(updatedProduct);

    }

    @Override
    public ProductResponseDTO addProduct(ProductCreateRequestDTO requestDTO) {
        log.debug("entered add product");

        Product product = new Product();
        product.setProductName(requestDTO.productName());
        product.setCategory(requestDTO.category());
        product.setPrice(requestDTO.price());
        product.setStockQty(requestDTO.stockQty());
        Product savedProduct = productRepository.save(product);

        ProductResponseDTO responseDTO= new ProductResponseDTO(
                savedProduct.getId(),
                savedProduct.getProductName(),
                savedProduct.getPrice(),
                savedProduct.getStockQty(),
                savedProduct.getCategory(),
                savedProduct.getCreateAt()
        );
        return responseDTO;
    }

    @Override
    public void deleteProduct(Long productId) {

    }

    @Override
    @Transactional
    public void decreaseStock(UUID productId, int quantity) {

        int updatedRows = productRepository.decreaseStock(
                productId,
                quantity
        );

        if (updatedRows == 1) {
            return;
        }

        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException(
                    "Product does not exist"
            );
        }

        throw new InsufficientStockException(
                "Insufficient stock for product " + productId
        );
    }
}
