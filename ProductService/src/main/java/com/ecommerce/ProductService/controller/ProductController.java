package com.ecommerce.ProductService.controller;

import com.ecommerce.ProductService.dto.ProductCreateRequestDTO;
import com.ecommerce.ProductService.dto.ProductResponseDTO;
import com.ecommerce.ProductService.dto.ProductUpdateRequestDTO;
import com.ecommerce.ProductService.dto.StockUpdateRequestDTO;
import com.ecommerce.ProductService.service.ProductServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Product APIs", description = "APIs for managing products and product stock")
public class ProductController {

    private final ProductServiceImpl productServiceImpl;

    @Operation(summary = "Get all products", description = "Fetches all available products")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Products fetched successfully",
                    content = @Content(schema = @Schema(implementation = ProductResponseDTO.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        log.info("Fetching all products");

        List<ProductResponseDTO> products = productServiceImpl.getAllProduct();

        log.info("Fetched {} products", products.size());
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Get product by ID", description = "Fetches product details using the given product ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Product fetched successfully",
                    content = @Content(schema = @Schema(implementation = ProductResponseDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductDetails(@PathVariable UUID id) {
        log.info("Fetching product details for id: {}", id);

        ProductResponseDTO product = productServiceImpl.getProductById(id);

        log.info("Product fetched successfully with id: {}", id);
        return ResponseEntity.ok(product);
    }

    @Operation(summary = "Create product", description = "Creates a new product")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Product created successfully",
                    content = @Content(schema = @Schema(implementation = ProductResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid product request", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ProductResponseDTO> saveProduct(
            @Valid @RequestBody ProductCreateRequestDTO createRequestDTO) {

        log.info("Creating product with name: {}", createRequestDTO.productName());

        ProductResponseDTO product = productServiceImpl.addProduct(createRequestDTO);

        log.info("Product created successfully with id: {}", product.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @Operation(summary = "Update product", description = "Updates product details")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Product updated successfully",
                    content = @Content(schema = @Schema(implementation = ProductResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid product request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PatchMapping
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @Valid @RequestBody ProductUpdateRequestDTO updateRequestDTO) {

        log.info("Updating product with id: {}", updateRequestDTO.id());

        ProductResponseDTO product = productServiceImpl.updateProduct(updateRequestDTO);

        log.info("Product updated successfully with id: {}", product.id());
        return ResponseEntity.ok(product);
    }

    @Operation(summary = "Decrease product stock", description = "Decreases available stock for a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Stock decreased successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid stock update request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Insufficient stock", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PatchMapping("/{id}/stock")
    public ResponseEntity<Void> decreaseStock(
            @PathVariable UUID id,
            @Valid @RequestBody StockUpdateRequestDTO request) {

        log.info("Decreasing stock for product id: {}, quantity: {}", id, request.quantity());

        productServiceImpl.decreaseStock(id, request.quantity());

        log.info("Stock decreased successfully for product id: {}", id);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{id}/stock/restore")
    public ResponseEntity<Void> increaseStock(
            @PathVariable UUID id,
            @Valid @RequestBody StockUpdateRequestDTO request) {

        productServiceImpl.increaseStock(id, request.quantity());
        return ResponseEntity.noContent().build();
    }
}