package com.mpt.journal.repository;

import com.mpt.journal.model.CategoryModel;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryCategoryRepository {
    private final List<CategoryModel> categories = new ArrayList<>();
    private final AtomicInteger idCounter = new AtomicInteger(1);

    // --- CRUD ---
    public List<CategoryModel> findAll(boolean includeDeleted) {
        if (includeDeleted) return new ArrayList<>(categories);
        return categories.stream().filter(c -> !c.isDeleted()).collect(Collectors.toList());
    }

    public CategoryModel save(CategoryModel category) {
        if (category.getId() == 0) {
            category.setId(idCounter.getAndIncrement());
            categories.add(category);
        } else {
            categories.removeIf(c -> c.getId() == category.getId());
            categories.add(category);
        }
        return category;
    }

    public void deleteByIdPhysical(int id) {
        categories.removeIf(c -> c.getId() == id);
    }

    public void deleteByIdLogical(int id) {
        categories.stream().filter(c -> c.getId() == id).findFirst().ifPresent(c -> c.setDeleted(true));
    }

    public void deleteMany(List<Integer> ids, boolean hard) {
        if (hard) {
            categories.removeIf(c -> ids.contains(c.getId()));
        } else {
            categories.stream().filter(c -> ids.contains(c.getId())).forEach(c -> c.setDeleted(true));
        }
    }

    // --- Поиск ---
    public List<CategoryModel> search(String query, boolean includeDeleted) {
        if (query == null || query.isBlank()) return findAll(includeDeleted);
        String low = query.toLowerCase().trim();
        return findAll(true).stream()
                .filter(c -> includeDeleted || !c.isDeleted())
                .filter(c -> c.getName() != null && c.getName().toLowerCase().contains(low))
                .collect(Collectors.toList());
    }

    // --- Фильтрация ---
    public List<CategoryModel> filter(String name, Integer minId, Integer maxId, boolean includeDeleted) {
        return findAll(includeDeleted).stream()
                .filter(c -> name == null || name.isBlank() || (c.getName() != null && c.getName().toLowerCase().contains(name.toLowerCase())))
                .filter(c -> minId == null || c.getId() >= minId)
                .filter(c -> maxId == null || c.getId() <= maxId)
                .collect(Collectors.toList());
    }

    // --- Пагинация ---
    public List<CategoryModel> page(List<CategoryModel> source, int page, int size) {
        if (size < 10) size = 10;
        if (page < 1) page = 1;
        int from = (page - 1) * size;
        if (from >= source.size()) return Collections.emptyList();
        return source.stream().skip(from).limit(size).collect(Collectors.toList());
    }
}