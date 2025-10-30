package com.mpt.journal.repository;

import com.mpt.journal.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    Page<Order> findByIsDeletedFalse(Pageable pageable);

    Page<Order> findByUserIdAndIsDeletedFalse(Long userId, Pageable pageable);

    @Query("""
           SELECT o FROM Order o 
           WHERE o.isDeleted = false 
           AND (:userId IS NULL OR o.user.id = :userId)
           AND (:status IS NULL OR o.status = :status)
           AND (:startDate IS NULL OR o.orderDate >= :startDate)
           AND (:endDate IS NULL OR o.orderDate <= :endDate)
           """)
    Page<Order> filter(
            @Param("userId") Long userId,
            @Param("status") String status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );
}