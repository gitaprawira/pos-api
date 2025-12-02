package com.soloware.pos.modules.product.service;

import com.soloware.pos.modules.product.dto.ProductRequestDTO;
import com.soloware.pos.modules.product.entity.ProductEntity;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    ProductEntity createProduct(ProductRequestDTO request);

    Optional<ProductEntity> getProductById(Long id);

    Optional<ProductEntity> getProductBySku(String sku);

    List<ProductEntity> getAllProducts();

    ProductEntity updateProduct(Long id, ProductRequestDTO request);

    void deleteProduct(Long id);
}
