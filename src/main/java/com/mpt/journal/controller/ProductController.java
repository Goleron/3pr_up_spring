package com.mpt.journal.controller;

import com.mpt.journal.entity.Product;
import com.mpt.journal.service.CategoryService;
import com.mpt.journal.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @GetMapping
    public String listProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            Model model) {

        var response = productService.getAllProducts(page, size, name, categoryId, minPrice, maxPrice);
        model.addAttribute("products", response.content());
        model.addAttribute("currentPage", response.page());
        model.addAttribute("totalPages", response.totalPages());
        model.addAttribute("categories", categoryService.getAllCategories(0, 100, null).content());
        model.addAttribute("name", name);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        return "product/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories(0, 100, null).content());
        return "product/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute Product product, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories(0, 100, null).content());
            return "product/form";
        }
        productService.createProduct(product);
        return "redirect:/products";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getProductById(id));
        model.addAttribute("categories", categoryService.getAllCategories(0, 100, null).content());
        return "product/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute Product product, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories(0, 100, null).content());
            return "product/form";
        }
        productService.updateProduct(id, product);
        return "redirect:/products";
    }

    // Логическое удаление (soft delete)
    @PostMapping("/delete")
    public String softDelete(@RequestParam("ids") List<Long> ids) {
        productService.deleteProducts(ids);
        return "redirect:/products";
    }

    // Физическое удаление (hard delete)
    @PostMapping("/hard-delete")
    public String hardDelete(@RequestParam("ids") List<Long> ids) {
        ids.forEach(productService::deleteProduct);
        return "redirect:/products";
    }
}