package com.mpt.journal.service.impl;

import com.mpt.journal.dto.PageResponse;
import com.mpt.journal.entity.Product;
import com.mpt.journal.exception.ResourceNotFoundException;
import com.mpt.journal.repository.ProductRepository;
import com.mpt.journal.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .filter(p -> !p.getIsDeleted())  // ИСПРАВЛЕНО: getIsDeleted()
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<Product> getAllProducts(int page, int size, String name, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice) {
        PageRequest pr = PageRequest.of(page, size, Sort.by("name"));
        Page<Product> result = productRepository.filter(name, categoryId, minPrice, maxPrice, pr);
        return toPageResponse(result);
    }

    @Override
    public Product updateProduct(Long id, Product product) {
        Product existing = getProductById(id);
        existing.setName(product.getName());
        existing.setDescription(product.getDescription());
        existing.setPrice(product.getPrice());
        existing.setStock(product.getStock());
        existing.setCategory(product.getCategory());
        return productRepository.save(existing);
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    @Override
    public void softDeleteProduct(Long id) {
        Product product = getProductById(id);
        product.setIsDeleted(true);  // ИСПРАВЛЕНО: setIsDeleted()
        productRepository.save(product);
    }

    @Override
    public void deleteProducts(List<Long> ids) {
        ids.forEach(this::softDeleteProduct);
    }

    private <T> PageResponse<T> toPageResponse(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}