package com.mpt.journal.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Authentication authentication) {
        // Если не авторизован - на страницу логина
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        // Перенаправление в зависимости от роли
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return "index"; // Админ видит главную страницу со всеми разделами
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MANAGER"))) {
            return "redirect:/products"; // Менеджер сразу на товары
        } else {
            return "redirect:/products"; // Обычный пользователь тоже на товары
        }
    }
}