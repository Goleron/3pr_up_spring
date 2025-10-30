package com.mpt.journal.service;

import com.mpt.journal.dto.PageResponse;
import com.mpt.journal.entity.Order;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface OrderService {

    Order createOrder(Order order);

    // НОВЫЙ МЕТОД
    void createOrderWithProducts(Long userId, String status, Map<String, String> productQuantities);

    Order getOrderById(Long id);

    PageResponse<Order> getAllOrders(int page, int size, Long userId, String status,
                                     LocalDate dateFrom, LocalDate dateTo);

    void deleteOrder(Long id);

    void softDeleteOrder(Long id);

    void deleteOrders(List<Long> ids);
}