package com.mpt.journal.repository;

import com.mpt.journal.entity.OrderProduct;
import com.mpt.journal.entity.OrderProductId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderProductRepository extends JpaRepository<OrderProduct, OrderProductId> {
}