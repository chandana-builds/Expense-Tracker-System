package com.expensetracker.controller;

import com.expensetracker.model.Category;
import com.expensetracker.model.TransactionType;
import com.expensetracker.model.User;
import com.expensetracker.service.CategoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/categories")
public class CategoryController extends BaseController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String listCategories(HttpSession session, Model model) {
        User user = getCurrentUser(session);
        if (user == null) return "redirect:/login";

        List<Category> categories = categoryService.getCategoriesForUser(user);

        model.addAttribute("user", user);
        model.addAttribute("categories", categories);
        model.addAttribute("transactionTypes", TransactionType.values());
        model.addAttribute("activePage", "categories");

        return "categories/list";
    }

    @PostMapping("/add")
    public String addCategory(
            @RequestParam("name") String name,
            @RequestParam("type") TransactionType type,
            @RequestParam(value = "color", defaultValue = "#6366f1") String color,
            @RequestParam(value = "icon", defaultValue = "bi-tag") String icon,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User user = getCurrentUser(session);
        if (user == null) return "redirect:/login";

        try {
            categoryService.createCategory(name, type, color, icon, user);
            redirectAttributes.addFlashAttribute("successMessage", "New category '" + name + "' created successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not create category: " + ex.getMessage());
        }

        return "redirect:/categories";
    }
}
