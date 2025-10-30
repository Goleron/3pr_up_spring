// CategoryService.java
package com.mpt.journal.service;

import com.mpt.journal.entity.Category;
import com.mpt.journal.dto.PageResponse;

import java.util.List;

public interface CategoryService {
    Category createCategory(Category category);
    Category getCategoryById(Long id);
    PageResponse<Category> getAllCategories(int page, int size, String name);
    Category updateCategory(Long id, Category category);
    void deleteCategory(Long id);
    void softDeleteCategory(Long id);
    void deleteCategories(List<Long> ids);
}