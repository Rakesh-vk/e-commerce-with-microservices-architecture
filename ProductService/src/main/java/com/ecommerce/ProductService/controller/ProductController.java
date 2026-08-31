package com.ecommerce.ProductService.controller;

import com.ecommerce.ProductService.dto.ProductCreateRequestDTO;
import com.ecommerce.ProductService.dto.ProductResponseDTO;
import com.ecommerce.ProductService.dto.ProductUpdateRequestDTO;
import com.ecommerce.ProductService.service.ProductServiceImpl;
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
public class ProductController {
    private final ProductServiceImpl productServiceImpl;

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts(){
        return new ResponseEntity<>(productServiceImpl.getAllProduct(),HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductDetails(@PathVariable UUID id){
        return new ResponseEntity<>(productServiceImpl.getProductById(id),HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> saveProduct(@RequestBody ProductCreateRequestDTO createRequestDTO){
        log.debug("Entered saveProduct");
        return new ResponseEntity<>(productServiceImpl.addProduct(createRequestDTO),HttpStatus.CREATED);
    }

    @PatchMapping
    public ResponseEntity<ProductResponseDTO> updateProduct(@RequestBody ProductUpdateRequestDTO updateRequestDTO){
        return new ResponseEntity<>(productServiceImpl.updateProduct(updateRequestDTO),HttpStatus.OK);
    }
}
