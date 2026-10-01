package com.expensetracker.controller;

import com.expensetracker.model.Category;
import com.expensetracker.model.Expense;
import com.expensetracker.model.PaymentMethod;
import com.expensetracker.model.TransactionType;
import com.expensetracker.model.User;
import com.expensetracker.service.CategoryService;
import com.expensetracker.service.ExpenseService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/expenses")
public class ExpenseController extends BaseController {

    private final ExpenseService expenseService;
    private final CategoryService categoryService;

    @Autowired
    public ExpenseController(ExpenseService expenseService, CategoryService categoryService) {
        this.expenseService = expenseService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String listExpenses(
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String keyword,
            HttpSession session,
            Model model) {

        User user = getCurrentUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        List<Expense> expenses;
        if (type != null || categoryId != null || startDate != null || endDate != null || (keyword != null && !keyword.isBlank())) {
            expenses = expenseService.filterExpenses(user, type, categoryId, startDate, endDate, keyword);
        } else {
            expenses = expenseService.getAllExpensesForUser(user);
        }

        List<Category> categories = categoryService.getCategoriesForUser(user);

        // Calculate filtered total income and expenses
        BigDecimal totalIncome = expenses.stream()
                .filter(e -> e.getType() == TransactionType.INCOME)
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = expenses.stream()
                .filter(e -> e.getType() == TransactionType.EXPENSE)
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("user", user);
        model.addAttribute("expenses", expenses);
        model.addAttribute("categories", categories);
        model.addAttribute("paymentMethods", PaymentMethod.values());
        model.addAttribute("transactionTypes", TransactionType.values());
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedCategory", categoryId);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("keyword", keyword);
        model.addAttribute("totalIncome", totalIncome);
        model.addAttribute("totalExpense", totalExpense);
        model.addAttribute("netBalance", totalIncome.subtract(totalExpense));
        model.addAttribute("activePage", "expenses");

        return "expenses/list";
    }

    @PostMapping("/add")
    public String addExpense(
            @RequestParam("title") String title,
            @RequestParam("amount") BigDecimal amount,
            @RequestParam("type") TransactionType type,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("transactionDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate transactionDate,
            @RequestParam(value = "paymentMethod", defaultValue = "CASH") PaymentMethod paymentMethod,
            @RequestParam(value = "notes", required = false) String notes,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User user = getCurrentUser(session);
        if (user == null) return "redirect:/login";

        try {
            expenseService.createExpense(title, amount, type, categoryId, transactionDate, paymentMethod, notes, user);
            redirectAttributes.addFlashAttribute("successMessage", "Transaction added successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving transaction: " + ex.getMessage());
        }

        return "redirect:/expenses";
    }

    @PostMapping("/edit/{id}")
    public String updateExpense(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("amount") BigDecimal amount,
            @RequestParam("type") TransactionType type,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("transactionDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate transactionDate,
            @RequestParam(value = "paymentMethod", defaultValue = "CASH") PaymentMethod paymentMethod,
            @RequestParam(value = "notes", required = false) String notes,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User user = getCurrentUser(session);
        if (user == null) return "redirect:/login";

        try {
            expenseService.updateExpense(id, title, amount, type, categoryId, transactionDate, paymentMethod, notes, user);
            redirectAttributes.addFlashAttribute("successMessage", "Transaction updated successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating transaction: " + ex.getMessage());
        }

        return "redirect:/expenses";
    }

    @PostMapping("/delete/{id}")
    public String deleteExpense(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(session);
        if (user == null) return "redirect:/login";

        try {
            expenseService.deleteExpense(id, user);
            redirectAttributes.addFlashAttribute("successMessage", "Transaction deleted successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not delete transaction: " + ex.getMessage());
        }

        return "redirect:/expenses";
    }

    @GetMapping("/export")
    public void exportToCsv(
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String keyword,
            HttpSession session,
            HttpServletResponse response) throws IOException {

        User user = getCurrentUser(session);
        if (user == null) return;

        List<Expense> expenses;
        if (type != null || categoryId != null || startDate != null || endDate != null || (keyword != null && !keyword.isBlank())) {
            expenses = expenseService.filterExpenses(user, type, categoryId, startDate, endDate, keyword);
        } else {
            expenses = expenseService.getAllExpensesForUser(user);
        }

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"expense_records_" + LocalDate.now() + ".csv\"");

        PrintWriter writer = response.getWriter();
        writer.println("ID,Date,Title,Type,Category,Amount,Payment Method,Notes");

        for (Expense e : expenses) {
            writer.printf("%d,%s,\"%s\",%s,\"%s\",%.2f,%s,\"%s\"%n",
                    e.getId(),
                    e.getTransactionDate(),
                    e.getTitle().replace("\"", "\"\""),
                    e.getType().name(),
                    e.getCategory() != null ? e.getCategory().getName() : "Uncategorized",
                    e.getAmount(),
                    e.getPaymentMethod() != null ? e.getPaymentMethod().getDisplayName() : "",
                    e.getNotes() != null ? e.getNotes().replace("\"", "\"\"") : ""
            );
        }
        writer.flush();
    }
}
