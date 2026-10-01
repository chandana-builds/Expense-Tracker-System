package com.expensetracker.service;

import com.expensetracker.dto.BudgetStatusDto;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.model.Budget;
import com.expensetracker.model.Category;
import com.expensetracker.model.User;
import com.expensetracker.repository.BudgetRepository;
import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;

    @Autowired
    public BudgetService(BudgetRepository budgetRepository, CategoryRepository categoryRepository, ExpenseRepository expenseRepository) {
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
        this.expenseRepository = expenseRepository;
    }

    public List<BudgetStatusDto> getBudgetStatusesForMonth(User user, String yearMonthStr) {
        YearMonth ym = YearMonth.parse(yearMonthStr);
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate = ym.atEndOfMonth();

        List<Budget> budgets = budgetRepository.findByUserAndBudgetMonth(user, yearMonthStr);
        List<BudgetStatusDto> statusList = new ArrayList<>();

        for (Budget budget : budgets) {
            BigDecimal spent = expenseRepository.getSpentByCategoryAndDateRange(user, budget.getCategory(), startDate, endDate);
            statusList.add(new BudgetStatusDto(budget.getId(), budget.getCategory(), budget.getMonthlyLimit(), spent));
        }

        return statusList;
    }

    @Transactional
    public Budget setBudget(User user, Long categoryId, String yearMonth, BigDecimal monthlyLimit) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + categoryId));

        Optional<Budget> existing = budgetRepository.findByUserAndCategoryAndBudgetMonth(user, category, yearMonth);
        Budget budget;
        if (existing.isPresent()) {
            budget = existing.get();
            budget.setMonthlyLimit(monthlyLimit);
        } else {
            budget = new Budget(category, yearMonth, monthlyLimit, user);
        }
        return budgetRepository.save(budget);
    }

    @Transactional
    public void deleteBudget(Long id, User user) {
        Budget budget = budgetRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with ID: " + id));
        budgetRepository.delete(budget);
    }
}
