package com.ecommerce.ProductService.mapper;

import com.ecommerce.ProductService.dto.ProductCreateRequestDTO;
import com.ecommerce.ProductService.dto.ProductResponseDTO;
import com.ecommerce.ProductService.entity.Product;

public class ProductMapper {

    public static Product toEntity(ProductCreateRequestDTO request) {
        return Product.builder()
                .productName(request.productName())
                .price(request.price())
                .stockQty(request.stockQty())
                .category(request.category())
                .build();
    }

    public static ProductResponseDTO toResponse(Product product) {
        return new ProductResponseDTO(
                product.getId(),
                product.getProductName(),
                product.getPrice(),
                product.getStockQty(),
                product.getCategory(),
                product.getCreateAt()
        );
    }
}
