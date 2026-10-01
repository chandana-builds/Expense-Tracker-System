package com.expensetracker.controller;

import com.expensetracker.dto.BudgetStatusDto;
import com.expensetracker.model.Category;
import com.expensetracker.model.TransactionType;
import com.expensetracker.model.User;
import com.expensetracker.service.BudgetService;
import com.expensetracker.service.CategoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@Controller
@RequestMapping("/budgets")
public class BudgetController extends BaseController {

    private final BudgetService budgetService;
    private final CategoryService categoryService;

    @Autowired
    public BudgetController(BudgetService budgetService, CategoryService categoryService) {
        this.budgetService = budgetService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String showBudgets(
            @RequestParam(required = false) String month,
            HttpSession session,
            Model model) {

        User user = getCurrentUser(session);
        if (user == null) return "redirect:/login";

        String selectedMonth = (month != null && !month.isBlank()) ? month : YearMonth.now().toString();

        List<BudgetStatusDto> budgetStatuses = budgetService.getBudgetStatusesForMonth(user, selectedMonth);
        List<Category> expenseCategories = categoryService.getCategoriesForUserAndType(user, TransactionType.EXPENSE);

        BigDecimal totalBudget = budgetStatuses.stream()
                .map(BudgetStatusDto::getMonthlyLimit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSpent = budgetStatuses.stream()
                .map(BudgetStatusDto::getSpentAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("user", user);
        model.addAttribute("budgetStatuses", budgetStatuses);
        model.addAttribute("categories", expenseCategories);
        model.addAttribute("selectedMonth", selectedMonth);
        model.addAttribute("totalBudget", totalBudget);
        model.addAttribute("totalSpent", totalSpent);
        model.addAttribute("activePage", "budgets");

        return "budgets/list";
    }

    @PostMapping("/set")
    public String setBudget(
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("budgetMonth") String budgetMonth,
            @RequestParam("monthlyLimit") BigDecimal monthlyLimit,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User user = getCurrentUser(session);
        if (user == null) return "redirect:/login";

        try {
            budgetService.setBudget(user, categoryId, budgetMonth, monthlyLimit);
            redirectAttributes.addFlashAttribute("successMessage", "Budget set successfully for " + budgetMonth);
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving budget: " + ex.getMessage());
        }

        return "redirect:/budgets?month=" + budgetMonth;
    }

    @PostMapping("/delete/{id}")
    public String deleteBudget(
            @PathVariable Long id,
            @RequestParam(value = "month", required = false) String month,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User user = getCurrentUser(session);
        if (user == null) return "redirect:/login";

        try {
            budgetService.deleteBudget(id, user);
            redirectAttributes.addFlashAttribute("successMessage", "Budget goal removed.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting budget: " + ex.getMessage());
        }

        return "redirect:/budgets" + (month != null ? "?month=" + month : "");
    }
}
