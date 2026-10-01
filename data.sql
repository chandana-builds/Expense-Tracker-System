-- ===================================================================
-- Expense Tracker System - Sample Seed Data
-- ===================================================================

USE `expense_tracker_db`;

-- Default Categories (Expense)
INSERT INTO `categories` (`name`, `type`, `color`, `icon`, `is_default`, `user_id`) VALUES
('Food & Dining', 'EXPENSE', '#ef4444', 'bi-egg-fried', 1, NULL),
('Rent & Housing', 'EXPENSE', '#f97316', 'bi-house-door', 1, NULL),
('Transportation', 'EXPENSE', '#f59e0b', 'bi-car-front', 1, NULL),
('Utilities & Bills', 'EXPENSE', '#10b981', 'bi-lightning-charge', 1, NULL),
('Entertainment', 'EXPENSE', '#06b6d4', 'bi-controller', 1, NULL),
('Shopping', 'EXPENSE', '#8b5cf6', 'bi-bag-check', 1, NULL),
('Healthcare', 'EXPENSE', '#ec4899', 'bi-heart-pulse', 1, NULL),
('Education', 'EXPENSE', '#3b82f6', 'bi-mortarboard', 1, NULL),
('Groceries', 'EXPENSE', '#14b8a6', 'bi-basket', 1, NULL),
('Travel', 'EXPENSE', '#6366f1', 'bi-airplane', 1, NULL),
('Investments', 'EXPENSE', '#84cc16', 'bi-graph-up-arrow', 1, NULL);

-- Default Categories (Income)
INSERT INTO `categories` (`name`, `type`, `color`, `icon`, `is_default`, `user_id`) VALUES
('Salary', 'INCOME', '#22c55e', 'bi-cash-coin', 1, NULL),
('Freelance & Side Gig', 'INCOME', '#10b981', 'bi-laptop', 1, NULL),
('Investment Dividends', 'INCOME', '#14b8a6', 'bi-pie-chart', 1, NULL),
('Rental Income', 'INCOME', '#06b6d4', 'bi-building', 1, NULL),
('Gift / Bonus', 'INCOME', '#a855f7', 'bi-gift', 1, NULL);
