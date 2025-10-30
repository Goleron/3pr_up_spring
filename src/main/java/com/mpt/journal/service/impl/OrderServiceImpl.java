package com.mpt.journal.service.impl;

import com.mpt.journal.dto.PageResponse;
import com.mpt.journal.entity.*;
import com.mpt.journal.exception.ResourceNotFoundException;
import com.mpt.journal.repository.OrderProductRepository;
import com.mpt.journal.repository.OrderRepository;
import com.mpt.journal.repository.ProductRepository;
import com.mpt.journal.repository.UserRepository;
import com.mpt.journal.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderProductRepository orderProductRepository;

    @Override
    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public void createOrderWithProducts(Long userId, String status, Map<String, String> productQuantities) {
        // Находим пользователя
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Создаём заказ
        Order order = Order.builder()
                .user(user)
                .status(status != null ? status : "PENDING")
                .orderDate(LocalDateTime.now())
                .isDeleted(false)
                .build();

        // Сначала сохраняем заказ, чтобы получить ID
        order = orderRepository.save(order);

        BigDecimal totalAmount = BigDecimal.ZERO;

        // Обрабатываем товары
        for (Map.Entry<String, String> entry : productQuantities.entrySet()) {
            if (!entry.getKey().startsWith("productQuantities[")) continue;

            // Извлекаем ID товара из ключа: productQuantities[123] -> 123
            String productIdStr = entry.getKey().replace("productQuantities[", "").replace("]", "");
            Long productId = Long.parseLong(productIdStr);
            int quantity = Integer.parseInt(entry.getValue());

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));

            // Проверяем наличие на складе
            if (product.getStock() < quantity) {
                throw new IllegalArgumentException("Недостаточно товара: " + product.getName());
            }

            // Уменьшаем остаток
            product.setStock(product.getStock() - quantity);
            productRepository.save(product);

            // Создаём составной ключ
            OrderProductId orderProductId = new OrderProductId(order.getId(), product.getId());

            // Добавляем товар в заказ через промежуточную таблицу
            OrderProduct orderProduct = OrderProduct.builder()
                    .id(orderProductId)
                    .order(order)
                    .product(product)
                    .quantity(quantity)
                    .priceAtPurchase(product.getPrice())
                    .build();

            order.getOrderProducts().add(orderProduct);

            // Увеличиваем общую сумму
            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        }

        order.setTotalAmount(totalAmount);
        orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .filter(o -> !o.getIsDeleted())  // ИСПРАВЛЕНО: getIsDeleted() вместо isDeleted()
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<Order> getAllOrders(int page, int size, Long userId, String status,
                                            LocalDate dateFrom, LocalDate dateTo) {

        Specification<Order> spec = Specification.where(deletedFalse());

        if (userId != null) {
            spec = spec.and(userIdEquals(userId));
        }
        if (status != null && !status.isBlank()) {
            spec = spec.and(statusEquals(status));
        }
        if (dateFrom != null) {
            spec = spec.and(orderDateGreaterThanOrEqual(dateFrom.atStartOfDay()));
        }
        if (dateTo != null) {
            spec = spec.and(orderDateLessThanOrEqual(dateTo.atTime(23, 59, 59)));
        }

        PageRequest pr = PageRequest.of(page, size, Sort.by("orderDate").descending());
        Page<Order> result = orderRepository.findAll(spec, pr);

        return toPageResponse(result);
    }

    @Override
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order not found with id: " + id);
        }
        orderRepository.deleteById(id);
    }

    @Override
    public void softDeleteOrder(Long id) {
        Order order = getOrderById(id);
        order.setIsDeleted(true);  // ИСПРАВЛЕНО: setIsDeleted() вместо setDeleted()
        orderRepository.save(order);
    }

    @Override
    public void deleteOrders(List<Long> ids) {
        ids.forEach(this::softDeleteOrder);
    }

    // === Specifications ===

    private static Specification<Order> deletedFalse() {
        return (root, query, cb) -> cb.isFalse(root.get("isDeleted"));
    }

    private static Specification<Order> userIdEquals(Long userId) {
        return (root, query, cb) -> cb.equal(root.get("user").get("id"), userId);
    }

    private static Specification<Order> statusEquals(String status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    private static Specification<Order> orderDateGreaterThanOrEqual(LocalDateTime date) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("orderDate"), date);
    }

    private static Specification<Order> orderDateLessThanOrEqual(LocalDateTime date) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("orderDate"), date);
    }

    // === Helper ===

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