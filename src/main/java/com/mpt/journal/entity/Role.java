package com.mpt.journal.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Название роли обязательно")
    @Size(max = 50, message = "Название роли: до 50 символов")
    @Column(nullable = false, unique = true)
    private String name;

    // УДАЛИЛИ @ManyToMany связь с User - она нам не нужна
    // Связь управляется только со стороны User
}