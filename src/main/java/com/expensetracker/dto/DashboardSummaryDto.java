package com.expensetracker.dto;

import java.math.BigDecimal;
import java.util.List;

public class DashboardSummaryDto {
    private BigDecimal totalBalance;
    private BigDecimal currentMonthIncome;
    private BigDecimal currentMonthExpense;
    private BigDecimal currentMonthSavings;
    private double savingsRate;
    private long totalTransactionsCount;
    private List<CategorySummaryDto> topCategories;

    public DashboardSummaryDto() {
    }

    public BigDecimal getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(BigDecimal totalBalance) {
        this.totalBalance = totalBalance;
    }

    public BigDecimal getCurrentMonthIncome() {
        return currentMonthIncome;
    }

    public void setCurrentMonthIncome(BigDecimal currentMonthIncome) {
        this.currentMonthIncome = currentMonthIncome;
    }

    public BigDecimal getCurrentMonthExpense() {
        return currentMonthExpense;
    }

    public void setCurrentMonthExpense(BigDecimal currentMonthExpense) {
        this.currentMonthExpense = currentMonthExpense;
    }

    public BigDecimal getCurrentMonthSavings() {
        return currentMonthSavings;
    }

    public void setCurrentMonthSavings(BigDecimal currentMonthSavings) {
        this.currentMonthSavings = currentMonthSavings;
    }

    public double getSavingsRate() {
        return savingsRate;
    }

    public void setSavingsRate(double savingsRate) {
        this.savingsRate = savingsRate;
    }

    public long getTotalTransactionsCount() {
        return totalTransactionsCount;
    }

    public void setTotalTransactionsCount(long totalTransactionsCount) {
        this.totalTransactionsCount = totalTransactionsCount;
    }

    public List<CategorySummaryDto> getTopCategories() {
        return topCategories;
    }

    public void setTopCategories(List<CategorySummaryDto> topCategories) {
        this.topCategories = topCategories;
    }
}
