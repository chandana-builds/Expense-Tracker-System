package com.expensetracker.service;

import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.model.Category;
import com.expensetracker.model.Expense;
import com.expensetracker.model.PaymentMethod;
import com.expensetracker.model.TransactionType;
import com.expensetracker.model.User;
import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository, CategoryRepository categoryRepository) {
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Expense> getAllExpensesForUser(User user) {
        return expenseRepository.findByUserOrderByTransactionDateDescCreatedAtDesc(user);
    }

    public Page<Expense> getExpensesPaged(User user, Pageable pageable) {
        return expenseRepository.findByUserOrderByTransactionDateDescCreatedAtDesc(user, pageable);
    }

    public Expense getExpenseByIdAndUser(Long id, User user) {
        return expenseRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + id));
    }

    public List<Expense> filterExpenses(User user, TransactionType type, Long categoryId,
                                        LocalDate startDate, LocalDate endDate, String keyword) {
        return expenseRepository.filterExpenses(user, type, categoryId, startDate, endDate, keyword);
    }

    @Transactional
    public Expense createExpense(String title, BigDecimal amount, TransactionType type,
                                 Long categoryId, LocalDate date, PaymentMethod paymentMethod,
                                 String notes, User user) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + categoryId));

        Expense expense = new Expense(title, amount, type, category, date, paymentMethod, notes, user);
        return expenseRepository.save(expense);
    }

    @Transactional
    public Expense updateExpense(Long id, String title, BigDecimal amount, TransactionType type,
                                 Long categoryId, LocalDate date, PaymentMethod paymentMethod,
                                 String notes, User user) {
        Expense expense = getExpenseByIdAndUser(id, user);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + categoryId));

        expense.setTitle(title);
        expense.setAmount(amount);
        expense.setType(type);
        expense.setCategory(category);
        expense.setTransactionDate(date);
        expense.setPaymentMethod(paymentMethod);
        expense.setNotes(notes);

        return expenseRepository.save(expense);
    }

    @Transactional
    public void deleteExpense(Long id, User user) {
        Expense expense = getExpenseByIdAndUser(id, user);
        expenseRepository.delete(expense);
    }

    public List<Expense> getRecentTransactions(User user) {
        return expenseRepository.findTop5ByUserOrderByTransactionDateDescCreatedAtDesc(user);
    }
}
