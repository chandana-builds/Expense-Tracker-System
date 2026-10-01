package com.expensetracker.dto;

import com.expensetracker.model.Category;

import java.math.BigDecimal;

public class BudgetStatusDto {
    private Long id;
    private Category category;
    private BigDecimal monthlyLimit;
    private BigDecimal spentAmount;
    private BigDecimal remainingAmount;
    private double percentageSpent;
    private boolean isOverBudget;

    public BudgetStatusDto() {
    }

    public BudgetStatusDto(Long id, Category category, BigDecimal monthlyLimit, BigDecimal spentAmount) {
        this.id = id;
        this.category = category;
        this.monthlyLimit = monthlyLimit;
        this.spentAmount = spentAmount;
        this.remainingAmount = monthlyLimit.subtract(spentAmount);
        if (monthlyLimit.compareTo(BigDecimal.ZERO) > 0) {
            this.percentageSpent = spentAmount.multiply(BigDecimal.valueOf(100))
                    .divide(monthlyLimit, 2, java.math.RoundingMode.HALF_UP).doubleValue();
        } else {
            this.percentageSpent = 0.0;
        }
        this.isOverBudget = spentAmount.compareTo(monthlyLimit) > 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public BigDecimal getMonthlyLimit() {
        return monthlyLimit;
    }

    public void setMonthlyLimit(BigDecimal monthlyLimit) {
        this.monthlyLimit = monthlyLimit;
    }

    public BigDecimal getSpentAmount() {
        return spentAmount;
    }

    public void setSpentAmount(BigDecimal spentAmount) {
        this.spentAmount = spentAmount;
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public double getPercentageSpent() {
        return percentageSpent;
    }

    public void setPercentageSpent(double percentageSpent) {
        this.percentageSpent = percentageSpent;
    }

    public boolean isOverBudget() {
        return isOverBudget;
    }

    public void setOverBudget(boolean overBudget) {
        isOverBudget = overBudget;
    }
}
