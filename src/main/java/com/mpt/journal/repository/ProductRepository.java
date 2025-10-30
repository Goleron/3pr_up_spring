package com.mpt.journal.repository;

import com.mpt.journal.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Все неудалённые товары
    Page<Product> findByIsDeletedFalse(Pageable pageable);

    // Фильтрация по категории
    Page<Product> findByCategoryIdAndIsDeletedFalse(Long categoryId, Pageable pageable);

    // Поиск по названию
    @Query("SELECT p FROM Product p WHERE p.isDeleted = false AND LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Product> findByNameContainingIgnoreCaseAndIsDeletedFalse(@Param("name") String name, Pageable pageable);

    // Фильтрация по цене
    @Query("SELECT p FROM Product p WHERE p.isDeleted = false AND p.price BETWEEN :min AND :max")
    Page<Product> findByPriceBetweenAndIsDeletedFalse(@Param("min") BigDecimal min, @Param("max") BigDecimal max, Pageable pageable);

    // Комбинированный поиск + фильтр (ИСПРАВЛЕНО с nativeQuery)
    @Query(value = """
           SELECT * FROM products p 
           WHERE p.is_deleted = false 
           AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', CAST(:name AS VARCHAR), '%')))
           AND (:categoryId IS NULL OR p.category_id = :categoryId)
           AND (:minPrice IS NULL OR p.price >= :minPrice)
           AND (:maxPrice IS NULL OR p.price <= :maxPrice)
           ORDER BY p.name
           """,
            countQuery = """
           SELECT COUNT(*) FROM products p 
           WHERE p.is_deleted = false 
           AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', CAST(:name AS VARCHAR), '%')))
           AND (:categoryId IS NULL OR p.category_id = :categoryId)
           AND (:minPrice IS NULL OR p.price >= :minPrice)
           AND (:maxPrice IS NULL OR p.price <= :maxPrice)
           """,
            nativeQuery = true)
    Page<Product> filter(
            @Param("name") String name,
            @Param("categoryId") Long categoryId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );
}