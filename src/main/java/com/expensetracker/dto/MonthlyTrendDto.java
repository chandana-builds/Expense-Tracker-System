package com.expensetracker.dto;

import java.math.BigDecimal;

public class MonthlyTrendDto {
    private String monthLabel;
    private BigDecimal income;
    private BigDecimal expense;
    private BigDecimal netSavings;

    public MonthlyTrendDto() {
    }

    public MonthlyTrendDto(String monthLabel, BigDecimal income, BigDecimal expense, BigDecimal netSavings) {
        this.monthLabel = monthLabel;
        this.income = income;
        this.expense = expense;
        this.netSavings = netSavings;
    }

    public String getMonthLabel() {
        return monthLabel;
    }

    public void setMonthLabel(String monthLabel) {
        this.monthLabel = monthLabel;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public BigDecimal getExpense() {
        return expense;
    }

    public void setExpense(BigDecimal expense) {
        this.expense = expense;
    }

    public BigDecimal getNetSavings() {
        return netSavings;
    }

    public void setNetSavings(BigDecimal netSavings) {
        this.netSavings = netSavings;
    }
}
