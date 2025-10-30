package com.mpt.journal.controller;

import com.mpt.journal.dto.RegistrationDto;
import com.mpt.journal.entity.Role;
import com.mpt.journal.entity.User;
import com.mpt.journal.service.RoleService;
import com.mpt.journal.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            @RequestParam(required = false) String expired,
                            @RequestParam(required = false) String reason,
                            Model model) {
        if (error != null) {
            model.addAttribute("error", "Неверное имя пользователя или пароль");
        }
        if (logout != null) {
            model.addAttribute("message", "Вы успешно вышли из системы");
        }
        if (expired != null) {
            if ("lifetime".equals(reason)) {
                model.addAttribute("error", "⏰ Сессия истекла: прошло более 15 минут с момента входа");
            } else if ("inactive".equals(reason)) {
                model.addAttribute("error", "💤 Сессия истекла: вы бездействовали более 3 минут");
            } else {
                model.addAttribute("error", "Ваша сессия истекла. Войдите снова");
            }
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new RegistrationDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") RegistrationDto registrationDto,
                           BindingResult result,
                           Model model) {

        // Проверка на совпадение паролей
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.user", "Пароли не совпадают");
        }

        if (result.hasErrors()) {
            return "auth/register";
        }

        try {
            // Создаём пользователя
            User user = User.builder()
                    .username(registrationDto.getUsername())
                    .email(registrationDto.getEmail())
                    .password(passwordEncoder.encode(registrationDto.getPassword()))
                    .isDeleted(false)
                    .build();

            // По умолчанию роль USER
            Role userRole = roleService.getRoleByName("USER");
            Set<Role> roles = new HashSet<>();
            roles.add(userRole);
            user.setRoles(roles);

            userService.createUser(user);

            model.addAttribute("success", "Регистрация успешна! Теперь вы можете войти");
            return "auth/login";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка регистрации: " + e.getMessage());
            return "auth/register";
        }
    }
}