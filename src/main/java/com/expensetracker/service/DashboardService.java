package com.expensetracker.service;

import com.expensetracker.dto.CategorySummaryDto;
import com.expensetracker.dto.DashboardSummaryDto;
import com.expensetracker.dto.MonthlyTrendDto;
import com.expensetracker.model.TransactionType;
import com.expensetracker.model.User;
import com.expensetracker.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {

    private final ExpenseRepository expenseRepository;

    @Autowired
    public DashboardService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public DashboardSummaryDto getDashboardSummary(User user) {
        DashboardSummaryDto summary = new DashboardSummaryDto();

        // All-time totals for balance
        BigDecimal allTimeIncome = expenseRepository.getTotalAmountByUserAndType(user, TransactionType.INCOME);
        BigDecimal allTimeExpense = expenseRepository.getTotalAmountByUserAndType(user, TransactionType.EXPENSE);
        summary.setTotalBalance(allTimeIncome.subtract(allTimeExpense));

        // Current month totals
        LocalDate now = LocalDate.now();
        YearMonth currentYearMonth = YearMonth.from(now);
        LocalDate monthStart = currentYearMonth.atDay(1);
        LocalDate monthEnd = currentYearMonth.atEndOfMonth();

        BigDecimal monthIncome = expenseRepository.getTotalAmountByUserAndTypeAndDateBetween(
                user, TransactionType.INCOME, monthStart, monthEnd);
        BigDecimal monthExpense = expenseRepository.getTotalAmountByUserAndTypeAndDateBetween(
                user, TransactionType.EXPENSE, monthStart, monthEnd);

        summary.setCurrentMonthIncome(monthIncome);
        summary.setCurrentMonthExpense(monthExpense);
        summary.setCurrentMonthSavings(monthIncome.subtract(monthExpense));

        if (monthIncome.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal savings = monthIncome.subtract(monthExpense);
            double rate = savings.multiply(BigDecimal.valueOf(100))
                    .divide(monthIncome, 1, RoundingMode.HALF_UP).doubleValue();
            summary.setSavingsRate(rate);
        } else {
            summary.setSavingsRate(0.0);
        }

        summary.setTotalTransactionsCount(expenseRepository.count());

        // Top Expense Categories for current month
        List<Object[]> rawCategories = expenseRepository.getCategoryBreakdown(
                user, TransactionType.EXPENSE, monthStart, monthEnd);
        List<CategorySummaryDto> categorySummaries = new ArrayList<>();

        for (Object[] row : rawCategories) {
            String catName = (String) row[0];
            String color = (String) row[1];
            BigDecimal amount = (BigDecimal) row[2];
            double pct = 0.0;
            if (monthExpense.compareTo(BigDecimal.ZERO) > 0) {
                pct = amount.multiply(BigDecimal.valueOf(100))
                        .divide(monthExpense, 1, RoundingMode.HALF_UP).doubleValue();
            }
            categorySummaries.add(new CategorySummaryDto(catName, color, amount, pct));
        }

        summary.setTopCategories(categorySummaries);
        return summary;
    }

    public List<MonthlyTrendDto> getLast6MonthsTrend(User user) {
        List<MonthlyTrendDto> trends = new ArrayList<>();
        YearMonth current = YearMonth.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");

        // Iterate last 6 months backwards, then reverse
        for (int i = 5; i >= 0; i--) {
            YearMonth targetMonth = current.minusMonths(i);
            LocalDate start = targetMonth.atDay(1);
            LocalDate end = targetMonth.atEndOfMonth();

            BigDecimal income = expenseRepository.getTotalAmountByUserAndTypeAndDateBetween(
                    user, TransactionType.INCOME, start, end);
            BigDecimal expense = expenseRepository.getTotalAmountByUserAndTypeAndDateBetween(
                    user, TransactionType.EXPENSE, start, end);
            BigDecimal net = income.subtract(expense);

            trends.add(new MonthlyTrendDto(targetMonth.format(formatter), income, expense, net));
        }

        return trends;
    }

    public List<CategorySummaryDto> getCategoryBreakdown(User user, TransactionType type, LocalDate start, LocalDate end) {
        if (start == null) {
            start = YearMonth.now().atDay(1);
        }
        if (end == null) {
            end = YearMonth.now().atEndOfMonth();
        }

        BigDecimal total = expenseRepository.getTotalAmountByUserAndTypeAndDateBetween(user, type, start, end);
        List<Object[]> rawCategories = expenseRepository.getCategoryBreakdown(user, type, start, end);
        List<CategorySummaryDto> categorySummaries = new ArrayList<>();

        for (Object[] row : rawCategories) {
            String catName = (String) row[0];
            String color = (String) row[1];
            BigDecimal amount = (BigDecimal) row[2];
            double pct = 0.0;
            if (total.compareTo(BigDecimal.ZERO) > 0) {
                pct = amount.multiply(BigDecimal.valueOf(100))
                        .divide(total, 1, RoundingMode.HALF_UP).doubleValue();
            }
            categorySummaries.add(new CategorySummaryDto(catName, color, amount, pct));
        }

        return categorySummaries;
    }
}
