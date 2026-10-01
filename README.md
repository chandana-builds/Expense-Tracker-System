# 💳 SpendWise — Enterprise Expense Tracker System

A modern, full-stack, enterprise-grade financial management and expense tracking web application built with **Spring Boot 3**, **Hibernate / JPA**, **MySQL**, **Bootstrap 5**, **JavaScript & jQuery**, and **Chart.js**.

---

## 🌟 Key Features

- **📊 Executive Financial Dashboard**: Real-time KPI summary widgets (Total Balance, Monthly Inflow, Monthly Outflow, Net Savings, and Savings Rate percentage).
- **📈 Interactive Financial Analytics**:
  - 6-month historical cash flow trends (Income vs. Expense vs. Net Savings) powered by **Chart.js**.
  - Monthly expense breakdown doughnut chart with interactive tooltips and category percentage calculation.
- **⚡ jQuery & AJAX Dynamic Filtering**:
  - Live filtering by transaction type (Income/Expense), category, date range, and keyword search without full page reload.
  - Dynamic transaction edit and delete modal populated via REST API endpoints (`/api/expenses/{id}`).
- **🎯 Smart Budget Goals & Tracking**:
  - Category-level monthly spending limits.
  - Visual dynamic progress bars with green/yellow/red threshold alerts (over-budget warnings).
- **🏷️ Customizable Category System**:
  - Preloaded default categories (Food, Housing, Utilities, Transportation, Salary, Freelance, etc.).
  - User custom category creator with color picker and Bootstrap icon selector.
- **📥 CSV Export**: One-click export of filtered transaction histories directly into CSV format.
- **🌓 Dark / Light Theme Toggle**: Persistent theme switcher powered by Bootstrap 5.3 and localStorage.
- **🔐 Multi-User Authentication**: Complete user registration, authentication, session isolation, and password hashing.
- **🚀 Dual Database Profile Support**:
  - **Dev Profile (H2)**: Zero-configuration instant launch with persistence.
  - **Prod Profile (MySQL 8.0)**: Production-ready MySQL connection pooling with HikariCP and Hibernate auto-DDL.

---

## 🛠️ Technology Stack Breakdown

| Layer | Technologies Used |
|---|---|
| **Frontend** | HTML5 Semantic markup, CSS3 (Glassmorphism, custom design system), Bootstrap 5.3.3, Bootstrap Icons |
| **Client Scripting** | JavaScript (ES6+), jQuery 3.7.1, Chart.js 4.4.3 |
| **Backend / Core Java** | Java 21 / 25, Object-Oriented Architecture, Exception Handling, Data Transfer Objects (DTOs), Java Streams API |
| **Enterprise Framework** | Spring Framework (Spring MVC, Spring Context, Spring Validation) |
| **Bootstrapping** | Spring Boot 3.4.3 (Embedded Tomcat, Auto-configuration) |
| **ORM / Persistence** | Hibernate 6.x, Spring Data JPA, HikariCP Connection Pool |
| **Databases** | MySQL 8.0 (Production) & H2 (Development zero-config) |
| **Build & Version Control** | Apache Maven 3.9.9, Git |

---

## 🚀 Getting Started

### 1. Prerequisites
- **Java 17, 21, or 25** installed (`java -version`)
- **Git** installed (`git --version`)
- *(Optional)* **MySQL Server 8.0** if running the MySQL profile

### 2. Run with Zero-Config Fallback (H2 Embedded)
You can launch the application immediately using the included Maven wrapper:

```bash
# On Windows
.\mvnw.cmd spring-boot:run

# Or if you have global maven:
mvn spring-boot:run
```

Once started, open your browser and navigate to:
👉 **http://localhost:8080**

#### Default Demo Credentials:
- **Username**: `demo`
- **Password**: `password123`
*(Or click "Create an account" to register your own personalized user)*

#### H2 Web Console:
- **URL**: `http://localhost:8080/h2-console`
- **JDBC URL**: `jdbc:h2:file:./data/expensedb`
- **User**: `sa` | **Password**: *(leave blank)*

---

### 3. Switch to MySQL 8.0

To connect directly to your local MySQL database:

1. Open `src/main/resources/application.properties` and change the active profile:
   ```properties
   spring.profiles.active=mysql
   ```

2. Open `src/main/resources/application-mysql.properties` and verify your MySQL password:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/expense_tracker_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   spring.datasource.username=root
   spring.datasource.password=YOUR_MYSQL_PASSWORD
   ```

3. Launch the application:
   ```bash
   .\mvnw.cmd spring-boot:run
   ```
   *Hibernate will automatically create all tables, indexes, constraints, and the DataInitializer will seed default categories and demo transactions.*

*(Alternatively, you can manually execute `schema.sql` and `data.sql` in MySQL Workbench).*

---

## 📂 Project Structure

```
Expense Tracker System/
├── .gitignore
├── mvnw.cmd                              # Portable Maven wrapper
├── pom.xml                               # Project dependencies & build config
├── schema.sql                            # Production MySQL 8.0 DDL script
├── data.sql                              # Sample seed data SQL script
├── README.md                             # Documentation
└── src/
    ├── main/
    │   ├── java/com/expensetracker/
    │   │   ├── ExpenseTrackerApplication.java
    │   │   ├── config/                   # Seed data & startup configs
    │   │   ├── controller/               # Spring MVC & REST API endpoints
    │   │   ├── dto/                      # Data Transfer Objects
    │   │   ├── exception/                # Global exception handling
    │   │   ├── model/                    # JPA / Hibernate Entities
    │   │   ├── repository/               # Spring Data JPA Repositories
    │   │   └── service/                  # Business logic services
    │   └── resources/
    │       ├── application.properties    # Base configuration
    │       ├── application-dev.properties# H2 profile
    │       ├── application-mysql.properties # MySQL profile
    │       ├── static/
    │       │   ├── css/styles.css        # Glassmorphism & custom styling
    │       │   └── js/app.js             # jQuery AJAX & Chart.js logic
    │       └── templates/                # Thymeleaf views
    │           ├── auth/                 # Login & Register
    │           ├── budgets/              # Budget goals
    │           ├── categories/           # Category manager
    │           ├── expenses/             # Transaction lists & modals
    │           ├── layout/               # Master template layout
    │           ├── dashboard.html        # Executive dashboard
    │           └── error.html            # Error page
```

---

## 📡 REST API Endpoints (Used by jQuery AJAX)

| Endpoint | Method | Description |
|---|---|---|
| `/api/expenses/filter` | `GET` | Asynchronous transaction filter by type, category, date, keyword |
| `/api/expenses/{id}` | `GET` | Retrieve JSON details of single transaction for edit modal |
| `/api/analytics/monthly-trends` | `GET` | 6-month historical cash flow dataset for Chart.js |
| `/api/analytics/category-breakdown` | `GET` | Current month category breakdown dataset for Chart.js |

---

## 📜 Git Version Control

To commit new changes:
```bash
git add .
git commit -m "feat: implement enterprise expense tracker with spring boot & mysql"
```
