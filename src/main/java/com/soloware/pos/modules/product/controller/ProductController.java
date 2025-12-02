package com.soloware.pos.modules.product.controller;

import com.soloware.pos.core.annotation.CurrentUser;
import com.soloware.pos.core.utils.ApiResponse;
import com.soloware.pos.modules.auth.entity.UserEntity;
import com.soloware.pos.modules.product.dto.ProductRequestDTO;
import com.soloware.pos.modules.product.entity.ProductEntity;
import com.soloware.pos.modules.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product management API endpoints")
@SecurityRequirement(name = "bearerAuth")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create a new product (Admin/Manager only)")
    public ResponseEntity<ApiResponse<ProductEntity>> createProduct(
            @Valid @RequestBody ProductRequestDTO request,
            @CurrentUser UserEntity user
    ) {
        ProductEntity product = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created successfully", product));
    }

    @GetMapping
    @Operation(summary = "Get all products")
    public ResponseEntity<ApiResponse<List<ProductEntity>>> getAllProducts() {
        List<ProductEntity> products = productService.getAllProducts();
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<ApiResponse<ProductEntity>> getProductById(@PathVariable Long id) {
        ProductEntity product = productService.getProductById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        return ResponseEntity.ok(ApiResponse.success(product));
    }

    @GetMapping("/sku/{sku}")
    @Operation(summary = "Get product by SKU")
    public ResponseEntity<ApiResponse<ProductEntity>> getProductBySku(@PathVariable String sku) {
        ProductEntity product = productService.getProductBySku(sku)
                .orElseThrow(() -> new RuntimeException("Product not found with SKU: " + sku));
        return ResponseEntity.ok(ApiResponse.success(product));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update a product (Admin/Manager only)")
    public ResponseEntity<ApiResponse<ProductEntity>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDTO request,
            @CurrentUser UserEntity user
    ) {
        ProductEntity product = productService.updateProduct(id, request);
        return ResponseEntity.ok(ApiResponse.success("Product updated successfully", product));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a product (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable Long id,
            @CurrentUser UserEntity user
    ) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully", null));
    }
}
