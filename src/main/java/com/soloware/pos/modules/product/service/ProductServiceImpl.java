package com.soloware.pos.modules.product.service;

import com.soloware.pos.modules.product.dto.ProductRequestDTO;
import com.soloware.pos.modules.product.entity.ProductEntity;
import com.soloware.pos.modules.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public ProductEntity createProduct(ProductRequestDTO request) {
        log.debug("Creating product with SKU: {} and name: {}", request.sku(), request.name());
        
        ProductEntity product = new ProductEntity();
        product.setSku(request.sku());
        product.setName(request.name());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());
        
        ProductEntity savedProduct = productRepository.save(product);
        log.info("Product created: {} (ID: {}, SKU: {}) - Price: {}, Stock: {}", 
                savedProduct.getName(), savedProduct.getId(), savedProduct.getSku(), 
                savedProduct.getPrice(), savedProduct.getStockQuantity());
        
        return savedProduct;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductEntity> getProductById(Long id) {
        log.debug("Fetching product by ID: {}", id);
        Optional<ProductEntity> product = productRepository.findById(id);
        
        if (product.isPresent()) {
            log.debug("Product found: {} (SKU: {})", product.get().getName(), product.get().getSku());
        } else {
            log.debug("Product not found with ID: {}", id);
        }
        
        return product;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductEntity> getProductBySku(String sku) {
        log.debug("Fetching product by SKU: {}", sku);
        Optional<ProductEntity> product = productRepository.findBySku(sku);
        
        if (product.isPresent()) {
            log.debug("Product found: {} (ID: {})", product.get().getName(), product.get().getId());
        } else {
            log.debug("Product not found with SKU: {}", sku);
        }
        
        return product;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductEntity> getAllProducts() {
        log.debug("Fetching all products");
        List<ProductEntity> products = productRepository.findAll();
        log.debug("Retrieved {} products from database", products.size());
        return products;
    }

    @Override
    @Transactional
    public ProductEntity updateProduct(Long id, ProductRequestDTO request) {
        log.debug("Updating product ID: {} with new data - SKU: {}, Name: {}", id, request.sku(), request.name());
        
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Update failed: Product not found with ID: {}", id);
                    return new RuntimeException("Product not found with id: " + id);
                });
        
        String oldName = product.getName();
        String oldSku = product.getSku();
        
        product.setSku(request.sku());
        product.setName(request.name());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());
        
        ProductEntity updatedProduct = productRepository.save(product);
        log.info("Product updated: ID {} - Name: {} → {}, SKU: {} → {}, Price: {}, Stock: {}", 
                id, oldName, updatedProduct.getName(), oldSku, updatedProduct.getSku(), 
                updatedProduct.getPrice(), updatedProduct.getStockQuantity());
        
        return updatedProduct;
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        log.debug("Attempting to delete product with ID: {}", id);
        
        if (!productRepository.existsById(id)) {
            log.warn("Delete failed: Product not found with ID: {}", id);
            throw new RuntimeException("Product not found with id: " + id);
        }
        
        productRepository.deleteById(id);
        log.info("Product deleted successfully: ID {}", id);
    }
}
