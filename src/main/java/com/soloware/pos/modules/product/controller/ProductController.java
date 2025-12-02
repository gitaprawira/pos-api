package com.soloware.pos.modules.product.controller;

import com.soloware.pos.core.annotation.AuthCheck;
import com.soloware.pos.core.annotation.CurrentUser;
import com.soloware.pos.core.utils.ApiResponse;
import com.soloware.pos.modules.auth.entity.UserEntity;
import com.soloware.pos.modules.product.dto.ProductRequestDTO;
import com.soloware.pos.modules.product.entity.ProductEntity;
import com.soloware.pos.modules.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
@Tag(name = "Products", description = "Product management API endpoints")
@SecurityRequirement(name = "bearerAuth")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @AuthCheck
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create a new product (Admin/Manager only)", description = "Creates a new product in the system.")
    public ResponseEntity<ApiResponse<ProductEntity>> createProduct(
            @Valid @RequestBody ProductRequestDTO request,
            @Parameter(hidden = true) @CurrentUser UserEntity user
    ) {
        log.info("Creating new product: {} (SKU: {}) by user: {}", request.name(), request.sku(), user.getUsername());
        try {
            ProductEntity product = productService.createProduct(request);
            log.info("Product created successfully with ID: {} (SKU: {})", product.getId(), product.getSku());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Product created successfully", product));
        } catch (Exception e) {
            log.error("Failed to create product: {} - Error: {}", request.name(), e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping
    @AuthCheck
    @Operation(summary = "Get all products", description = "Retrieves a list of all products.")
    public ResponseEntity<ApiResponse<List<ProductEntity>>> getAllProducts() {
        log.info("Fetching all products");
        List<ProductEntity> products = productService.getAllProducts();
        log.info("Retrieved {} products", products.size());
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @GetMapping("/{id}")
    @AuthCheck
    @Operation(summary = "Get product by ID", description = "Retrieves a product by its unique ID.")
    public ResponseEntity<ApiResponse<ProductEntity>> getProductById(@PathVariable Long id) {
        log.info("Fetching product with ID: {}", id);
        ProductEntity product = productService.getProductById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found with ID: {}", id);
                    return new RuntimeException("Product not found with id: " + id);
                });
        log.info("Product retrieved: {} (SKU: {})", product.getName(), product.getSku());
        return ResponseEntity.ok(ApiResponse.success(product));
    }

    @GetMapping("/sku/{sku}")
    @AuthCheck
    @Operation(summary = "Get product by SKU", description = "Retrieves a product by its SKU.")
    public ResponseEntity<ApiResponse<ProductEntity>> getProductBySku(@PathVariable String sku) {
        log.info("Fetching product with SKU: {}", sku);
        ProductEntity product = productService.getProductBySku(sku)
                .orElseThrow(() -> {
                    log.warn("Product not found with SKU: {}", sku);
                    return new RuntimeException("Product not found with SKU: " + sku);
                });
        log.info("Product retrieved by SKU: {} (ID: {})", product.getSku(), product.getId());
        return ResponseEntity.ok(ApiResponse.success(product));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update a product (Admin/Manager only)", description = "Updates an existing product in the system.")
    public ResponseEntity<ApiResponse<ProductEntity>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDTO request,
            @Parameter(hidden = true) @CurrentUser UserEntity user
    ) {
        log.info("Updating product ID: {} with SKU: {} by user: {}", id, request.sku(), user.getUsername());
        try {
            ProductEntity product = productService.updateProduct(id, request);
            log.info("Product updated successfully: {} (ID: {})", product.getName(), product.getId());
            return ResponseEntity.ok(ApiResponse.success("Product updated successfully", product));
        } catch (Exception e) {
            log.error("Failed to update product ID: {} - Error: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @AuthCheck
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a product (Admin only)", description = "Deletes a product from the system.")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable Long id,
            @Parameter(hidden = true) @CurrentUser UserEntity user
    ) {
        log.info("Deleting product with ID: {} by user: {}", id, user.getUsername());
        try {
            productService.deleteProduct(id);
            log.info("Product deleted successfully: ID {}", id);
            return ResponseEntity.ok(ApiResponse.success("Product deleted successfully", null));
        } catch (Exception e) {
            log.error("Failed to delete product ID: {} - Error: {}", id, e.getMessage(), e);
            throw e;
        }
    }
}
