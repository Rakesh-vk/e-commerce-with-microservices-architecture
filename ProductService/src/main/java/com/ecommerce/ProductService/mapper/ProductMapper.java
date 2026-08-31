package com.ecommerce.ProductService.mapper;

import com.ecommerce.ProductService.dto.ProductCreateRequest;
import com.ecommerce.ProductService.dto.ProductResponse;
import com.ecommerce.ProductService.entity.Product;

public class ProductMapper {

    public static Product toEntity(ProductCreateRequest request) {
        return Product.builder()
                .productName(request.productName())
                .price(request.price())
                .stockQty(request.stockQty())
                .category(request.category())
                .build();
    }

    public static ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getProductName(),
                product.getPrice(),
                product.getStockQty(),
                product.getCategory(),
                product.getCreateAt()
        );
    }
}
