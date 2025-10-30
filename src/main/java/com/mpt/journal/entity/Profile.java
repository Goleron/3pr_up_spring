package com.mpt.journal.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Size(max = 50, message = "Имя: до 50 символов")
    private String firstName;

    @Size(max = 50, message = "Фамилия: до 50 символов")
    private String lastName;

    @Size(max = 20, message = "Телефон: до 20 символов")
    private String phone;

    @Size(max = 255, message = "Адрес: до 255 символов")
    private String address;
}