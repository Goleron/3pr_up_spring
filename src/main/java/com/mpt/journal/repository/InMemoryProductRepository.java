package com.mpt.journal.repository;

import com.mpt.journal.entity.ProductEntity;
import com.mpt.journal.model.CategoryModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryProductRepository {
    private final List<ProductEntity> products = new ArrayList<>();
    private final AtomicInteger idCounter = new AtomicInteger(1);

    @Autowired
    private InMemoryCategoryRepository categoryRepo;

    // --- CRUD ---
    public List<ProductEntity> findAll(boolean includeDeleted) {
        if (includeDeleted) return new ArrayList<>(products);
        return products.stream().filter(p -> !p.isDeleted()).collect(Collectors.toList());
    }

    public Optional<ProductEntity> findById(int id) {
        return products.stream().filter(p -> p.getId() == id).findFirst();
    }

    public ProductEntity save(ProductEntity product) {
        // Ensure category exists
        if (product.getCategory() != null && !product.getCategory().isBlank()) {
            CategoryModel category = categoryRepo.findAll(true).stream()
                    .filter(c -> c.getName().equalsIgnoreCase(product.getCategory()))
                    .findFirst()
                    .orElseGet(() -> categoryRepo.save(new CategoryModel(0, product.getCategory())));
        }

        if (product.getId() == 0) {
            product.setId(idCounter.getAndIncrement());
            products.add(product);
            return product;
        } else {
            findById(product.getId()).ifPresentOrElse(existing -> {
                existing.setName(product.getName());
                existing.setCategory(product.getCategory());
                existing.setPrice(product.getPrice());
                existing.setQuantity(product.getQuantity());
                existing.setBrand(product.getBrand());
                existing.setDeleted(product.isDeleted());
            }, () -> products.add(product));
            return product;
        }
    }

    public void deleteByIdPhysical(int id) {
        products.removeIf(p -> p.getId() == id);
    }

    public void deleteByIdLogical(int id) {
        findById(id).ifPresent(p -> p.setDeleted(true));
    }

    public void deleteMany(List<Integer> ids, boolean hardDelete) {
        if (hardDelete) {
            products.removeIf(p -> ids.contains(p.getId()));
        } else {
            products.stream().filter(p -> ids.contains(p.getId())).forEach(p -> p.setDeleted(true));
        }
    }

    // --- Поиск ---
    public List<ProductEntity> search(String q, boolean includeDeleted) {
        if (q == null || q.isBlank()) return findAll(includeDeleted);
        String low = q.toLowerCase().trim();
        return findAll(true).stream()
                .filter(p -> includeDeleted || !p.isDeleted())
                .filter(p -> (p.getName() != null && p.getName().toLowerCase().contains(low))
                        || (p.getBrand() != null && p.getBrand().toLowerCase().contains(low))
                        || (p.getCategory() != null && p.getCategory().toLowerCase().contains(low)))
                .collect(Collectors.toList());
    }

    // --- Фильтрация ---
    public List<ProductEntity> filter(String category, String brand, Double minPrice, Double maxPrice, boolean includeDeleted) {
        return findAll(includeDeleted).stream()
                .filter(p -> category == null || category.isBlank() || (p.getCategory() != null && p.getCategory().equalsIgnoreCase(category)))
                .filter(p -> brand == null || brand.isBlank() || (p.getBrand() != null && p.getBrand().equalsIgnoreCase(brand)))
                .filter(p -> minPrice == null || p.getPrice() >= minPrice)
                .filter(p -> maxPrice == null || p.getPrice() <= maxPrice)
                .collect(Collectors.toList());
    }

    // --- Пагинация ---
    public List<ProductEntity> page(List<ProductEntity> source, int page, int size) {
        if (size < 10) size = 10;
        if (page < 1) page = 1;
        int from = (page - 1) * size;
        if (from >= source.size()) return Collections.emptyList();
        return source.stream().skip(from).limit(size).collect(Collectors.toList());
    }

    // --- Инициализация тестовых данных ---
    public void initSampleData() {
        if (!products.isEmpty()) return;
        String[] sampleCategories = {"Мячи", "Обувь", "Ракетки", "Одежда", "Тренажёры", "Разное"};
        for (String cat : sampleCategories) {
            categoryRepo.save(new CategoryModel(0, cat));
        }
        save(new ProductEntity(0, "Футбольный мяч Adidas", "Мячи", 1999.99, 15, "Adidas"));
        save(new ProductEntity(0, "Кроссовки Nike Air", "Обувь", 7499.00, 7, "Nike"));
        save(new ProductEntity(0, "Ракетка для тенниса", "Ракетки", 8999.50, 3, "Wilson"));
        save(new ProductEntity(0, "Шорты для бега", "Одежда", 1299.00, 20, "Asics"));
        save(new ProductEntity(0, "Эспандер", "Тренажёры", 499.00, 30, "Generic"));
        for (int i = 0; i < 30; i++) {
            save(new ProductEntity(0, "Товар #" + (i+1), "Разное", 100 + i, 5 + i, "Brand" + ((i%5)+1)));
        }
    }
}