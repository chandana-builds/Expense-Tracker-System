package com.expensetracker.repository;

import com.expensetracker.model.Budget;
import com.expensetracker.model.Category;
import com.expensetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByUserAndBudgetMonth(User user, String budgetMonth);
    Optional<Budget> findByUserAndCategoryAndBudgetMonth(User user, Category category, String budgetMonth);
    Optional<Budget> findByIdAndUser(Long id, User user);
}
