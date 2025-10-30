// CategoryServiceImpl.java
package com.mpt.journal.service.impl;

import com.mpt.journal.dto.PageResponse;
import com.mpt.journal.entity.Category;
import com.mpt.journal.exception.ResourceNotFoundException;
import com.mpt.journal.repository.CategoryRepository;
import com.mpt.journal.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    @Transactional(readOnly = true)
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .filter(c -> !c.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<Category> getAllCategories(int page, int size, String name) {
        PageRequest pr = PageRequest.of(page, size, Sort.by("name"));
        Page<Category> result = name != null && !name.isBlank()
                ? categoryRepository.findByNameContainingIgnoreCaseAndIsDeletedFalse(name, pr)
                : categoryRepository.findByIsDeletedFalse(pr);

        return toPageResponse(result);
    }

    @Override
    public Category updateCategory(Long id, Category category) {
        Category existing = getCategoryById(id);
        existing.setName(category.getName());
        existing.setDescription(category.getDescription());
        return categoryRepository.save(existing);
    }

    @Override
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }

    @Override
    public void softDeleteCategory(Long id) {
        Category category = getCategoryById(id);
        category.setDeleted(true);
        categoryRepository.save(category);
    }

    @Override
    public void deleteCategories(List<Long> ids) {
        ids.forEach(this::softDeleteCategory);
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