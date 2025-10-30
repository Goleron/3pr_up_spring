package com.mpt.journal.controller;

import com.mpt.journal.entity.Category;
import com.mpt.journal.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public String listCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            Model model) {

        var response = categoryService.getAllCategories(page, size, name);
        model.addAttribute("categories", response.content());
        model.addAttribute("currentPage", response.page());
        model.addAttribute("totalPages", response.totalPages());
        model.addAttribute("name", name);
        return "category/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("category", new Category());
        return "category/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute Category category, BindingResult result) {
        if (result.hasErrors()) return "category/form";
        categoryService.createCategory(category);
        return "redirect:/categories";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("category", categoryService.getCategoryById(id));
        return "category/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute Category category, BindingResult result) {
        if (result.hasErrors()) return "category/form";
        categoryService.updateCategory(id, category);
        return "redirect:/categories";
    }

    // Логическое удаление
    @PostMapping("/delete")
    public String softDelete(@RequestParam("ids") List<Long> ids) {
        categoryService.deleteCategories(ids);
        return "redirect:/categories";
    }

    // Физическое удаление
    @PostMapping("/hard-delete")
    public String hardDelete(@RequestParam("ids") List<Long> ids) {
        ids.forEach(categoryService::deleteCategory);
        return "redirect:/categories";
    }
}