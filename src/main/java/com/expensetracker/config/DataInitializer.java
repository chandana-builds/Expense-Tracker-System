package com.expensetracker.config;

import com.expensetracker.model.*;
import com.expensetracker.repository.*;
import com.expensetracker.service.CategoryService;
import com.expensetracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;
    private final CategoryService categoryService;
    private final UserService userService;

    @Autowired
    public DataInitializer(UserRepository userRepository,
                           CategoryRepository categoryRepository,
                           ExpenseRepository expenseRepository,
                           BudgetRepository budgetRepository,
                           CategoryService categoryService,
                           UserService userService) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.expenseRepository = expenseRepository;
        this.budgetRepository = budgetRepository;
        this.categoryService = categoryService;
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        // 1. Seed default global categories
        categoryService.seedDefaultCategories();

        // 2. Check if demo user exists
        if (!userRepository.existsByUsername("demo")) {
            User demoUser = new User("demo", "demo@expensetracker.com", "Alex Morgan", userService.hashPassword("password123"));
            userRepository.save(demoUser);

            List<Category> allCategories = categoryRepository.findAll();
            Category food = findCat(allCategories, "Food & Dining");
            Category housing = findCat(allCategories, "Rent & Housing");
            Category transport = findCat(allCategories, "Transportation");
            Category utilities = findCat(allCategories, "Utilities & Bills");
            Category entertainment = findCat(allCategories, "Entertainment");
            Category shopping = findCat(allCategories, "Shopping");
            Category salary = findCat(allCategories, "Salary");
            Category freelance = findCat(allCategories, "Freelance & Side Gig");
            Category investments = findCat(allCategories, "Investments");

            LocalDate today = LocalDate.now();

            // Seed transactions for current and previous 2 months
            // This month:
            expenseRepository.save(new Expense("Monthly Tech Salary", BigDecimal.valueOf(6500.00), TransactionType.INCOME, salary, today.withDayOfMonth(1), PaymentMethod.NET_BANKING, "Direct deposit monthly salary", demoUser));
            expenseRepository.save(new Expense("Freelance Web Design", BigDecimal.valueOf(1200.00), TransactionType.INCOME, freelance, today.withDayOfMonth(10), PaymentMethod.UPI, "Client UI kit payment", demoUser));
            expenseRepository.save(new Expense("Apartment Rent", BigDecimal.valueOf(1800.00), TransactionType.EXPENSE, housing, today.withDayOfMonth(2), PaymentMethod.NET_BANKING, "Downtown 2BHK rental", demoUser));
            expenseRepository.save(new Expense("Organic Groceries", BigDecimal.valueOf(142.50), TransactionType.EXPENSE, food, today.withDayOfMonth(4), PaymentMethod.CREDIT_CARD, "Whole Foods weekly haul", demoUser));
            expenseRepository.save(new Expense("Electricity & High Speed Fiber", BigDecimal.valueOf(165.00), TransactionType.EXPENSE, utilities, today.withDayOfMonth(5), PaymentMethod.UPI, "Utility bill combined", demoUser));
            expenseRepository.save(new Expense("Subway Pass & Fuel", BigDecimal.valueOf(85.00), TransactionType.EXPENSE, transport, today.withDayOfMonth(8), PaymentMethod.DEBIT_CARD, "Monthly commute pass", demoUser));
            expenseRepository.save(new Expense("Team Dinner at Bistro", BigDecimal.valueOf(94.20), TransactionType.EXPENSE, food, today.withDayOfMonth(12), PaymentMethod.CREDIT_CARD, "Italian dinner with colleagues", demoUser));
            expenseRepository.save(new Expense("Index Fund ETF SIP", BigDecimal.valueOf(500.00), TransactionType.EXPENSE, investments, today.withDayOfMonth(15), PaymentMethod.NET_BANKING, "S&P 500 index investment", demoUser));
            expenseRepository.save(new Expense("Weekend Cinema & IMAX", BigDecimal.valueOf(45.00), TransactionType.EXPENSE, entertainment, today.withDayOfMonth(18), PaymentMethod.UPI, "Movie tickets and snacks", demoUser));
            expenseRepository.save(new Expense("New Ergonomic Desk Chair", BigDecimal.valueOf(220.00), TransactionType.EXPENSE, shopping, today.withDayOfMonth(20), PaymentMethod.CREDIT_CARD, "Home office upgrade", demoUser));

            // Previous month:
            LocalDate lastMonth = today.minusMonths(1);
            expenseRepository.save(new Expense("Monthly Tech Salary", BigDecimal.valueOf(6500.00), TransactionType.INCOME, salary, lastMonth.withDayOfMonth(1), PaymentMethod.NET_BANKING, "Monthly base salary", demoUser));
            expenseRepository.save(new Expense("Consulting Milestone", BigDecimal.valueOf(950.00), TransactionType.INCOME, freelance, lastMonth.withDayOfMonth(15), PaymentMethod.UPI, "Backend API milestone", demoUser));
            expenseRepository.save(new Expense("Apartment Rent", BigDecimal.valueOf(1800.00), TransactionType.EXPENSE, housing, lastMonth.withDayOfMonth(2), PaymentMethod.NET_BANKING, "Rent transfer", demoUser));
            expenseRepository.save(new Expense("Supermarket Groceries", BigDecimal.valueOf(410.00), TransactionType.EXPENSE, food, lastMonth.withDayOfMonth(10), PaymentMethod.CREDIT_CARD, "Monthly grocery supplies", demoUser));
            expenseRepository.save(new Expense("Water & Electricity", BigDecimal.valueOf(140.00), TransactionType.EXPENSE, utilities, lastMonth.withDayOfMonth(12), PaymentMethod.UPI, "Bills paid", demoUser));
            expenseRepository.save(new Expense("Concert Tickets", BigDecimal.valueOf(160.00), TransactionType.EXPENSE, entertainment, lastMonth.withDayOfMonth(22), PaymentMethod.CREDIT_CARD, "Live music event", demoUser));

            // Set monthly budgets for demo user
            String currentYm = YearMonth.now().toString();
            budgetRepository.save(new Budget(food, currentYm, BigDecimal.valueOf(500.00), demoUser));
            budgetRepository.save(new Budget(housing, currentYm, BigDecimal.valueOf(1900.00), demoUser));
            budgetRepository.save(new Budget(entertainment, currentYm, BigDecimal.valueOf(150.00), demoUser));
            budgetRepository.save(new Budget(shopping, currentYm, BigDecimal.valueOf(300.00), demoUser));
            budgetRepository.save(new Budget(transport, currentYm, BigDecimal.valueOf(200.00), demoUser));
        }
    }

    private Category findCat(List<Category> list, String name) {
        return list.stream().filter(c -> c.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
