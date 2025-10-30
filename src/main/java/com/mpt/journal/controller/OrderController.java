package com.mpt.journal.controller;

import com.mpt.journal.entity.Order;
import com.mpt.journal.service.OrderService;
import com.mpt.journal.service.ProductService;
import com.mpt.journal.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final ProductService productService;

    @GetMapping
    public String listOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            Model model) {

        var response = orderService.getAllOrders(page, size, userId, status, dateFrom, dateTo);
        model.addAttribute("orders", response.content());
        model.addAttribute("currentPage", response.page());
        model.addAttribute("totalPages", response.totalPages());
        model.addAttribute("users", userService.getAllUsers(0, 1000, null).content());
        model.addAttribute("userId", userId);
        model.addAttribute("status", status);
        model.addAttribute("dateFrom", dateFrom);
        model.addAttribute("dateTo", dateTo);
        return "order/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("users", userService.getAllUsers(0, 1000, null).content());
        model.addAttribute("products", productService.getAllProducts(0, 1000, null, null, null, null).content());
        return "order/form";
    }

    @PostMapping
    public String createOrder(
            @RequestParam Long userId,
            @RequestParam String status,
            @RequestParam Map<String, String> productQuantities,
            Model model) {

        try {
            orderService.createOrderWithProducts(userId, status, productQuantities);
            return "redirect:/orders";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при создании заказа: " + e.getMessage());
            return createForm(model);
        }
    }

    @GetMapping("/{id}")
    public String viewOrder(@PathVariable Long id, Model model) {
        model.addAttribute("order", orderService.getOrderById(id));
        return "order/view";
    }

    // Логическое удаление
    @PostMapping("/delete")
    public String softDelete(@RequestParam("ids") List<Long> ids) {
        orderService.deleteOrders(ids);
        return "redirect:/orders";
    }

    // Физическое удаление
    @PostMapping("/hard-delete")
    public String hardDelete(@RequestParam("ids") List<Long> ids) {
        ids.forEach(orderService::deleteOrder);
        return "redirect:/orders";
    }
}