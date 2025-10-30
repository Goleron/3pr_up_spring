package com.mpt.journal.config;

import com.mpt.journal.entity.Role;
import com.mpt.journal.entity.User;
import com.mpt.journal.repository.RoleRepository;
import com.mpt.journal.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        // Создаём роли, если их нет
        createRoleIfNotExists("ADMIN");
        createRoleIfNotExists("MANAGER");
        createRoleIfNotExists("USER");

        // Создаём тестовых пользователей
        createUserIfNotExists("admin", "admin@sportshop.com", "Admin123!", "ADMIN");
        createUserIfNotExists("manager", "manager@sportshop.com", "Manager123!", "MANAGER");
        createUserIfNotExists("user", "user@sportshop.com", "User123!", "USER");
    }

    private void createRoleIfNotExists(String roleName) {
        roleRepository.findByName(roleName).orElseGet(() -> {
            Role role = Role.builder()
                    .name(roleName)
                    .build();
            return roleRepository.save(role);
        });
    }

    private void createUserIfNotExists(String username, String email, String password, String roleName) {
        userRepository.findByUsernameAndIsDeletedFalse(username).orElseGet(() -> {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

            Set<Role> roles = new HashSet<>();
            roles.add(role);

            User user = User.builder()
                    .username(username)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .roles(roles)
                    .isDeleted(false)
                    .build();

            return userRepository.save(user);
        });
    }
}