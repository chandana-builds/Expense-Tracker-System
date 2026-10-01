package com.expensetracker.controller;

import com.expensetracker.dto.CategorySummaryDto;
import com.expensetracker.dto.MonthlyTrendDto;
import com.expensetracker.model.Expense;
import com.expensetracker.model.TransactionType;
import com.expensetracker.model.User;
import com.expensetracker.service.DashboardService;
import com.expensetracker.service.ExpenseService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ExpenseApiController extends BaseController {

    private final ExpenseService expenseService;
    private final DashboardService dashboardService;

    @Autowired
    public ExpenseApiController(ExpenseService expenseService, DashboardService dashboardService) {
        this.expenseService = expenseService;
        this.dashboardService = dashboardService;
    }

    @GetMapping("/expenses/{id}")
    public ResponseEntity<?> getExpenseDetails(@PathVariable Long id, HttpSession session) {
        User user = getCurrentUser(session);
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        Expense expense = expenseService.getExpenseByIdAndUser(id, user);
        return ResponseEntity.ok(expense);
    }

    @GetMapping("/analytics/monthly-trends")
    public ResponseEntity<?> getMonthlyTrends(HttpSession session) {
        User user = getCurrentUser(session);
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        List<MonthlyTrendDto> trends = dashboardService.getLast6MonthsTrend(user);
        return ResponseEntity.ok(trends);
    }

    @GetMapping("/analytics/category-breakdown")
    public ResponseEntity<?> getCategoryBreakdown(
            @RequestParam(defaultValue = "EXPENSE") TransactionType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            HttpSession session) {

        User user = getCurrentUser(session);
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        List<CategorySummaryDto> breakdown = dashboardService.getCategoryBreakdown(user, type, startDate, endDate);
        return ResponseEntity.ok(breakdown);
    }

    @GetMapping("/expenses/filter")
    public ResponseEntity<?> filterExpensesAjax(
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String keyword,
            HttpSession session) {

        User user = getCurrentUser(session);
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        List<Expense> results = expenseService.filterExpenses(user, type, categoryId, startDate, endDate, keyword);

        double totalAmount = results.stream().mapToDouble(e -> e.getAmount().doubleValue()).sum();

        Map<String, Object> response = new HashMap<>();
        response.put("expenses", results);
        response.put("count", results.size());
        response.put("totalAmount", totalAmount);

        return ResponseEntity.ok(response);
    }
}
