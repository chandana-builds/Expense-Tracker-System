package com.expensetracker.repository;

import com.expensetracker.model.Category;
import com.expensetracker.model.Expense;
import com.expensetracker.model.TransactionType;
import com.expensetracker.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUserOrderByTransactionDateDescCreatedAtDesc(User user);

    Page<Expense> findByUserOrderByTransactionDateDescCreatedAtDesc(User user, Pageable pageable);

    Optional<Expense> findByIdAndUser(Long id, User user);

    @Query("SELECT e FROM Expense e WHERE e.user = :user " +
           "AND (:type IS NULL OR e.type = :type) " +
           "AND (:categoryId IS NULL OR e.category.id = :categoryId) " +
           "AND (:startDate IS NULL OR e.transactionDate >= :startDate) " +
           "AND (:endDate IS NULL OR e.transactionDate <= :endDate) " +
           "AND (:keyword IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(e.notes) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY e.transactionDate DESC, e.createdAt DESC")
    List<Expense> filterExpenses(
            @Param("user") User user,
            @Param("type") TransactionType type,
            @Param("categoryId") Long categoryId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("keyword") String keyword
    );

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user = :user AND e.type = :type")
    BigDecimal getTotalAmountByUserAndType(@Param("user") User user, @Param("type") TransactionType type);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user = :user AND e.type = :type " +
           "AND e.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalAmountByUserAndTypeAndDateBetween(
            @Param("user") User user,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT e.category.name, e.category.color, SUM(e.amount) " +
           "FROM Expense e WHERE e.user = :user AND e.type = :type " +
           "AND e.transactionDate BETWEEN :startDate AND :endDate " +
           "GROUP BY e.category.id, e.category.name, e.category.color " +
           "ORDER BY SUM(e.amount) DESC")
    List<Object[]> getCategoryBreakdown(
            @Param("user") User user,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e " +
           "WHERE e.user = :user AND e.category = :category " +
           "AND e.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal getSpentByCategoryAndDateRange(
            @Param("user") User user,
            @Param("category") Category category,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    List<Expense> findTop5ByUserOrderByTransactionDateDescCreatedAtDesc(User user);
}
