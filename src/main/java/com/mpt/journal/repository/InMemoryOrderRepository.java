package com.mpt.journal.repository;

import com.mpt.journal.entity.ProductEntity;
import com.mpt.journal.model.OrderModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryOrderRepository {
    private final List<OrderModel> orders = new ArrayList<>();
    private final AtomicInteger idCounter = new AtomicInteger(1);

    @Autowired
    private InMemoryProductRepository productRepo;

    // --- CRUD ---
    public List<OrderModel> findAll(boolean includeDeleted) {
        if (includeDeleted) return new ArrayList<>(orders);
        return orders.stream().filter(o -> !o.isDeleted()).collect(Collectors.toList());
    }

    public Optional<OrderModel> findById(int id) {
        return orders.stream().filter(o -> o.getId() == id).findFirst();
    }

    public OrderModel save(OrderModel order) {
        if (order.getId() == 0) {
            order.setId(idCounter.getAndIncrement());
            orders.add(order);
        } else {
            orders.removeIf(o -> o.getId() == order.getId());
            orders.add(order);
        }
        return order;
    }

    public void deleteByIdPhysical(int id) {
        orders.removeIf(o -> o.getId() == id);
    }

    public void deleteByIdLogical(int id) {
        orders.stream().filter(o -> o.getId() == id).findFirst().ifPresent(o -> o.setDeleted(true));
    }

    public void deleteMany(List<Integer> ids, boolean hard) {
        if (hard) {
            orders.removeIf(o -> ids.contains(o.getId()));
        } else {
            orders.stream().filter(o -> ids.contains(o.getId())).forEach(o -> o.setDeleted(true));
        }
    }

    // --- Поиск ---
    public List<OrderModel> search(String query, boolean includeDeleted) {
        if (query == null || query.isBlank()) return findAll(includeDeleted);
        String low = query.toLowerCase().trim();
        return findAll(true).stream()
                .filter(o -> includeDeleted || !o.isDeleted())
                .filter(o -> o.getCustomerName() != null && o.getCustomerName().toLowerCase().contains(low))
                .collect(Collectors.toList());
    }

    // --- Фильтрация ---
    public List<OrderModel> filter(String customerName, Double minTotal, Double maxTotal, Integer minProducts, boolean includeDeleted) {
        return findAll(includeDeleted).stream()
                .filter(o -> customerName == null || customerName.isBlank() || (o.getCustomerName() != null && o.getCustomerName().toLowerCase().contains(customerName.toLowerCase())))
                .filter(o -> minTotal == null || o.getTotalPrice() >= minTotal)
                .filter(o -> maxTotal == null || o.getTotalPrice() <= maxTotal)
                .filter(o -> minProducts == null || o.getProductIds().size() >= minProducts)
                .collect(Collectors.toList());
    }

    // --- Пагинация ---
    public List<OrderModel> page(List<OrderModel> source, int page, int size) {
        if (size < 10) size = 10;
        if (page < 1) page = 1;
        int from = (page - 1) * size;
        if (from >= source.size()) return Collections.emptyList();
        return source.stream().skip(from).limit(size).collect(Collectors.toList());
    }

    // --- Инициализация тестовых данных ---
    public void initSampleData() {
        if (!orders.isEmpty()) return;
        List<ProductEntity> products = productRepo.findAll(false);
        if (products.isEmpty()) return;
        save(new OrderModel(0, "Иван Иванов", List.of(products.get(0).getId(), products.get(1).getId()), 9498.99));
        save(new OrderModel(0, "Мария Петрова", List.of(products.get(2).getId()), 8999.50));
    }
}