-- ===================================================================
-- Expense Tracker System - MySQL 8.0 Schema
-- Database: expense_tracker_db
-- ===================================================================

CREATE DATABASE IF NOT EXISTS `expense_tracker_db`
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `expense_tracker_db`;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS `users` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `full_name` VARCHAR(100) NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Categories Table
CREATE TABLE IF NOT EXISTS `categories` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(60) NOT NULL,
    `type` VARCHAR(20) NOT NULL,
    `color` VARCHAR(20) DEFAULT '#6366f1',
    `icon` VARCHAR(50) DEFAULT 'bi-tag',
    `is_default` BOOLEAN DEFAULT FALSE,
    `user_id` BIGINT NULL,
    CONSTRAINT `fk_category_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Expenses & Incomes (Transactions) Table
CREATE TABLE IF NOT EXISTS `expenses` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `title` VARCHAR(120) NOT NULL,
    `amount` DECIMAL(12, 2) NOT NULL,
    `type` VARCHAR(20) NOT NULL,
    `category_id` BIGINT NOT NULL,
    `transaction_date` DATE NOT NULL,
    `payment_method` VARCHAR(30) DEFAULT 'CASH',
    `notes` VARCHAR(255) NULL,
    `user_id` BIGINT NOT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_expense_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`),
    CONSTRAINT `fk_expense_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    INDEX `idx_expense_date` (`transaction_date`),
    INDEX `idx_expense_user` (`user_id`),
    INDEX `idx_expense_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Budgets Table
CREATE TABLE IF NOT EXISTS `budgets` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `category_id` BIGINT NOT NULL,
    `budget_month` VARCHAR(7) NOT NULL,
    `monthly_limit` DECIMAL(12, 2) NOT NULL,
    `user_id` BIGINT NOT NULL,
    CONSTRAINT `fk_budget_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`),
    CONSTRAINT `fk_budget_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    UNIQUE KEY `uk_budget_user_cat_month` (`user_id`, `category_id`, `budget_month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
