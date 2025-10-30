package com.mpt.journal.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "order_date", nullable = false)
    @Builder.Default
    private LocalDateTime orderDate = LocalDateTime.now();

    @Column(length = 50)
    @Builder.Default
    private String status = "PENDING";

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    // Связь через промежуточную таблицу OrderProduct
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    private Set<OrderProduct> orderProducts = new HashSet<>();

    // Вспомогательный метод для добавления товара в заказ
    public void addProduct(Product product, int quantity, BigDecimal price) {
        OrderProduct orderProduct = OrderProduct.builder()
                .order(this)
                .product(product)
                .quantity(quantity)
                .priceAtPurchase(price)
                .build();

        // Устанавливаем ID вручную
        OrderProductId id = new OrderProductId(this.id, product.getId());
        orderProduct.setId(id);

        orderProducts.add(orderProduct);
    }
}