package com.expensetracker.service;

import com.expensetracker.model.Category;
import com.expensetracker.model.TransactionType;
import com.expensetracker.model.User;
import com.expensetracker.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getCategoriesForUser(User user) {
        return categoryRepository.findAllForUser(user);
    }

    public List<Category> getCategoriesForUserAndType(User user, TransactionType type) {
        return categoryRepository.findForUserAndType(user, type);
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Transactional
    public Category createCategory(String name, TransactionType type, String color, String icon, User user) {
        Category category = new Category(name, type, color, icon, false, user);
        return categoryRepository.save(category);
    }

    @Transactional
    public void seedDefaultCategories() {
        if (categoryRepository.count() == 0) {
            List<Category> defaults = new ArrayList<>();
            // Expense Categories
            defaults.add(new Category("Food & Dining", TransactionType.EXPENSE, "#ef4444", "bi-egg-fried", true, null));
            defaults.add(new Category("Rent & Housing", TransactionType.EXPENSE, "#f97316", "bi-house-door", true, null));
            defaults.add(new Category("Transportation", TransactionType.EXPENSE, "#f59e0b", "bi-car-front", true, null));
            defaults.add(new Category("Utilities & Bills", TransactionType.EXPENSE, "#10b981", "bi-lightning-charge", true, null));
            defaults.add(new Category("Entertainment", TransactionType.EXPENSE, "#06b6d4", "bi-controller", true, null));
            defaults.add(new Category("Shopping", TransactionType.EXPENSE, "#8b5cf6", "bi-bag-check", true, null));
            defaults.add(new Category("Healthcare", TransactionType.EXPENSE, "#ec4899", "bi-heart-pulse", true, null));
            defaults.add(new Category("Education", TransactionType.EXPENSE, "#3b82f6", "bi-mortarboard", true, null));
            defaults.add(new Category("Groceries", TransactionType.EXPENSE, "#14b8a6", "bi-basket", true, null));
            defaults.add(new Category("Travel", TransactionType.EXPENSE, "#6366f1", "bi-airplane", true, null));
            defaults.add(new Category("Investments", TransactionType.EXPENSE, "#84cc16", "bi-graph-up-arrow", true, null));

            // Income Categories
            defaults.add(new Category("Salary", TransactionType.INCOME, "#22c55e", "bi-cash-coin", true, null));
            defaults.add(new Category("Freelance & Side Gig", TransactionType.INCOME, "#10b981", "bi-laptop", true, null));
            defaults.add(new Category("Investment Dividends", TransactionType.INCOME, "#14b8a6", "bi-pie-chart", true, null));
            defaults.add(new Category("Rental Income", TransactionType.INCOME, "#06b6d4", "bi-building", true, null));
            defaults.add(new Category("Gift / Bonus", TransactionType.INCOME, "#a855f7", "bi-gift", true, null));

            categoryRepository.saveAll(defaults);
        }
    }
}
