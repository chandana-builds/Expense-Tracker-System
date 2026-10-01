package com.expensetracker.controller;

import com.expensetracker.dto.DashboardSummaryDto;
import com.expensetracker.dto.MonthlyTrendDto;
import com.expensetracker.model.Category;
import com.expensetracker.model.Expense;
import com.expensetracker.model.PaymentMethod;
import com.expensetracker.model.TransactionType;
import com.expensetracker.model.User;
import com.expensetracker.service.BudgetService;
import com.expensetracker.service.CategoryService;
import com.expensetracker.service.DashboardService;
import com.expensetracker.service.ExpenseService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.YearMonth;
import java.util.List;

@Controller
public class DashboardController extends BaseController {

    private final DashboardService dashboardService;
    private final ExpenseService expenseService;
    private final CategoryService categoryService;
    private final BudgetService budgetService;

    @Autowired
    public DashboardController(DashboardService dashboardService,
                               ExpenseService expenseService,
                               CategoryService categoryService,
                               BudgetService budgetService) {
        this.dashboardService = dashboardService;
        this.expenseService = expenseService;
        this.categoryService = categoryService;
        this.budgetService = budgetService;
    }

    @GetMapping({"/", "/dashboard"})
    public String showDashboard(HttpSession session, Model model) {
        User user = getCurrentUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        DashboardSummaryDto summary = dashboardService.getDashboardSummary(user);
        List<Expense> recentTransactions = expenseService.getRecentTransactions(user);
        List<Category> categories = categoryService.getCategoriesForUser(user);
        List<MonthlyTrendDto> monthlyTrends = dashboardService.getLast6MonthsTrend(user);
        var budgetStatuses = budgetService.getBudgetStatusesForMonth(user, YearMonth.now().toString());

        model.addAttribute("user", user);
        model.addAttribute("summary", summary);
        model.addAttribute("recentTransactions", recentTransactions);
        model.addAttribute("categories", categories);
        model.addAttribute("monthlyTrends", monthlyTrends);
        model.addAttribute("budgetStatuses", budgetStatuses);
        model.addAttribute("paymentMethods", PaymentMethod.values());
        model.addAttribute("transactionTypes", TransactionType.values());
        model.addAttribute("activePage", "dashboard");

        return "dashboard";
    }
}
