package com.expensetracker.dto;

import java.math.BigDecimal;

public class CategorySummaryDto {
    private String categoryName;
    private String color;
    private BigDecimal totalAmount;
    private double percentage;

    public CategorySummaryDto() {
    }

    public CategorySummaryDto(String categoryName, String color, BigDecimal totalAmount, double percentage) {
        this.categoryName = categoryName;
        this.color = color;
        this.totalAmount = totalAmount;
        this.percentage = percentage;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
}
