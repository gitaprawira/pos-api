package com.soloware.pos.modules.product.service;

import com.soloware.pos.modules.product.dto.ProductRequestDTO;
import com.soloware.pos.modules.product.entity.ProductEntity;
import com.soloware.pos.modules.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public ProductEntity createProduct(ProductRequestDTO request) {
        ProductEntity product = new ProductEntity();
        product.setSku(request.sku());
        product.setName(request.name());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());
        
        return productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductEntity> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductEntity> getProductBySku(String sku) {
        return productRepository.findBySku(sku);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductEntity> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    @Transactional
    public ProductEntity updateProduct(Long id, ProductRequestDTO request) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        product.setSku(request.sku());
        product.setName(request.name());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());
        
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }
}
