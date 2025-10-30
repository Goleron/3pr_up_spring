// ProductService.java
package com.mpt.journal.service;

import com.mpt.journal.entity.Product;
import com.mpt.journal.dto.PageResponse;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    Product createProduct(Product product);
    Product getProductById(Long id);
    PageResponse<Product> getAllProducts(int page, int size, String name, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice);
    Product updateProduct(Long id, Product product);
    void deleteProduct(Long id);
    void softDeleteProduct(Long id);
    void deleteProducts(List<Long> ids);
}