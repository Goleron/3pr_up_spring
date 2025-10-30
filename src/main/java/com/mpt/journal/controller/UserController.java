package com.mpt.journal.controller;

import com.mpt.journal.entity.User;
import com.mpt.journal.service.RoleService;
import com.mpt.journal.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    @GetMapping
    public String listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            Model model) {

        var response = userService.getAllUsers(page, size, search);
        model.addAttribute("users", response.content());
        model.addAttribute("currentPage", response.page());
        model.addAttribute("totalPages", response.totalPages());
        model.addAttribute("search", search);
        return "user/list";
    }

    @GetMapping("/new")
    public String createUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "user/form";
    }

    @PostMapping
    public String createUser(@Valid @ModelAttribute User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("allRoles", roleService.getAllRoles());
            return "user/form";
        }
        userService.createUser(user);
        return "redirect:/users";
    }

    @GetMapping("/{id}/edit")
    public String editUserForm(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.getUserById(id));
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "user/form";
    }

    @PostMapping("/{id}")
    public String updateUser(@PathVariable Long id, @Valid @ModelAttribute User user,
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("allRoles", roleService.getAllRoles());
            return "user/form";
        }
        userService.updateUser(id, user);
        return "redirect:/users";
    }

    // Логическое удаление
    @PostMapping("/delete")
    public String softDeleteUsers(@RequestParam("ids") List<Long> ids) {
        userService.deleteUsers(ids);
        return "redirect:/users";
    }

    // Физическое удаление
    @PostMapping("/hard-delete")
    public String hardDeleteUsers(@RequestParam("ids") List<Long> ids) {
        ids.forEach(userService::deleteUser);
        return "redirect:/users";
    }
}