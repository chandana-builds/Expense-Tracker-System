/**
 * Expense Tracker System - Main Interactive JavaScript & jQuery Script
 */

$(document).ready(function () {
    // ------------------------------------------------------------------
    // 1. Theme Toggle (Dark / Light Mode)
    // ------------------------------------------------------------------
    const storedTheme = localStorage.getItem('expense_tracker_theme') || 'light';
    applyTheme(storedTheme);

    $('#themeToggleBtn').on('click', function () {
        const currentTheme = $('html').attr('data-bs-theme');
        const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
        applyTheme(newTheme);
        localStorage.setItem('expense_tracker_theme', newTheme);
        // Refresh charts with updated theme colors
        if (window.trendsChart) window.trendsChart.update();
        if (window.categoryChart) window.categoryChart.update();
    });

    function applyTheme(theme) {
        $('html').attr('data-bs-theme', theme);
        if (theme === 'dark') {
            $('#themeToggleBtn i').removeClass('bi-moon-stars-fill').addClass('bi-sun-fill');
            $('#themeToggleBtn').attr('title', 'Switch to Light Mode');
        } else {
            $('#themeToggleBtn i').removeClass('bi-sun-fill').addClass('bi-moon-stars-fill');
            $('#themeToggleBtn').attr('title', 'Switch to Dark Mode');
        }
    }

    // ------------------------------------------------------------------
    // 2. Initialize Charts (Chart.js)
    // ------------------------------------------------------------------
    initMonthlyTrendsChart();
    initCategoryDoughnutChart();

    function initMonthlyTrendsChart() {
        const ctx = document.getElementById('monthlyTrendsChart');
        if (!ctx) return;

        // Fetch analytical data via jQuery AJAX
        $.ajax({
            url: '/api/analytics/monthly-trends',
            method: 'GET',
            dataType: 'json',
            success: function (data) {
                const labels = data.map(item => item.monthLabel);
                const incomes = data.map(item => item.income);
                const expenses = data.map(item => item.expense);
                const savings = data.map(item => item.netSavings);

                window.trendsChart = new Chart(ctx, {
                    type: 'bar',
                    data: {
                        labels: labels,
                        datasets: [
                            {
                                label: 'Income',
                                data: incomes,
                                backgroundColor: 'rgba(16, 185, 129, 0.75)',
                                borderColor: '#10b981',
                                borderWidth: 1.5,
                                borderRadius: 6
                            },
                            {
                                label: 'Expense',
                                data: expenses,
                                backgroundColor: 'rgba(239, 68, 68, 0.75)',
                                borderColor: '#ef4444',
                                borderWidth: 1.5,
                                borderRadius: 6
                            },
                            {
                                label: 'Net Savings',
                                data: savings,
                                type: 'line',
                                borderColor: '#4f46e5',
                                backgroundColor: 'rgba(79, 70, 229, 0.1)',
                                tension: 0.35,
                                fill: true,
                                borderWidth: 2.5,
                                pointBackgroundColor: '#4f46e5',
                                pointRadius: 4
                            }
                        ]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        interaction: {
                            mode: 'index',
                            intersect: false,
                        },
                        plugins: {
                            legend: {
                                position: 'top',
                                labels: {
                                    font: { family: 'Plus Jakarta Sans', weight: '600' }
                                }
                            },
                            tooltip: {
                                callbacks: {
                                    label: function (context) {
                                        return context.dataset.label + ': $' + Number(context.raw).toLocaleString('en-US', { minimumFractionDigits: 2 });
                                    }
                                }
                            }
                        },
                        scales: {
                            y: {
                                beginAtZero: true,
                                grid: { color: 'rgba(148, 163, 184, 0.15)' },
                                ticks: {
                                    callback: function (val) { return '$' + val; },
                                    font: { family: 'Plus Jakarta Sans' }
                                }
                            },
                            x: {
                                grid: { display: false },
                                ticks: { font: { family: 'Plus Jakarta Sans' } }
                            }
                        }
                    }
                });
            },
            error: function (err) {
                console.warn('Could not load monthly trends chart data', err);
            }
        });
    }

    function initCategoryDoughnutChart() {
        const ctx = document.getElementById('categoryBreakdownChart');
        if (!ctx) return;

        $.ajax({
            url: '/api/analytics/category-breakdown?type=EXPENSE',
            method: 'GET',
            dataType: 'json',
            success: function (data) {
                if (!data || data.length === 0) {
                    $('#categoryBreakdownChartPlaceholder').removeClass('d-none');
                    return;
                }

                const labels = data.map(item => item.categoryName);
                const amounts = data.map(item => item.totalAmount);
                const colors = data.map(item => item.color || '#6366f1');

                window.categoryChart = new Chart(ctx, {
                    type: 'doughnut',
                    data: {
                        labels: labels,
                        datasets: [{
                            data: amounts,
                            backgroundColor: colors,
                            borderWidth: 2,
                            borderColor: $('html').attr('data-bs-theme') === 'dark' ? '#121826' : '#ffffff',
                            hoverOffset: 6
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                            legend: {
                                position: 'bottom',
                                labels: {
                                    font: { family: 'Plus Jakarta Sans', weight: '500' },
                                    boxWidth: 12
                                }
                            },
                            tooltip: {
                                callbacks: {
                                    label: function (context) {
                                        const val = context.raw || 0;
                                        return ' ' + context.label + ': $' + Number(val).toLocaleString('en-US', { minimumFractionDigits: 2 });
                                    }
                                }
                            }
                        },
                        cutout: '70%'
                    }
                });
            }
        });
    }

    // ------------------------------------------------------------------
    // 3. jQuery AJAX Dynamic Filter for Transactions List
    // ------------------------------------------------------------------
    $('#ajaxFilterBtn').on('click', function (e) {
        e.preventDefault();
        runAjaxFilter();
    });

    $('#ajaxSearchInput').on('keyup', function (e) {
        if (e.key === 'Enter') {
            runAjaxFilter();
        }
    });

    function runAjaxFilter() {
        const type = $('#filterType').val();
        const categoryId = $('#filterCategory').val();
        const startDate = $('#filterStartDate').val();
        const endDate = $('#filterEndDate').val();
        const keyword = $('#ajaxSearchInput').val();

        $('#filterLoadingSpinner').removeClass('d-none');

        $.ajax({
            url: '/api/expenses/filter',
            type: 'GET',
            data: {
                type: type,
                categoryId: categoryId,
                startDate: startDate,
                endDate: endDate,
                keyword: keyword
            },
            success: function (res) {
                $('#filterLoadingSpinner').addClass('d-none');
                renderTransactionRows(res.expenses);
                $('#resultsCountBadge').text(res.count + ' transactions found');
                $('#filteredTotalSum').text('$' + Number(res.totalAmount).toLocaleString('en-US', { minimumFractionDigits: 2 }));
            },
            error: function () {
                $('#filterLoadingSpinner').addClass('d-none');
                alert('Failed to retrieve filtered records. Please try again.');
            }
        });
    }

    function renderTransactionRows(list) {
        const tbody = $('#expensesTableBody');
        tbody.empty();

        if (!list || list.length === 0) {
            tbody.append(`
                <tr>
                    <td colspan="7" class="text-center py-5 text-muted">
                        <i class="bi bi-inbox fs-1 d-block mb-2"></i>
                        No matching transactions found.
                    </td>
                </tr>
            `);
            return;
        }

        list.forEach(item => {
            const isExpense = item.type === 'EXPENSE';
            const badgeClass = isExpense ? 'badge-expense' : 'badge-income';
            const amountClass = isExpense ? 'text-danger' : 'text-success';
            const prefix = isExpense ? '-' : '+';
            const catName = item.category ? item.category.name : 'General';
            const catColor = item.category ? item.category.color : '#64748b';
            const catIcon = item.category ? item.category.icon : 'bi-tag';

            const row = `
                <tr id="row-expense-${item.id}">
                    <td><strong>#${item.id}</strong></td>
                    <td>${item.transactionDate}</td>
                    <td>
                        <div class="fw-semibold">${escapeHtml(item.title)}</div>
                        ${item.notes ? `<small class="text-muted text-truncate d-block" style="max-width: 250px;">${escapeHtml(item.notes)}</small>` : ''}
                    </td>
                    <td>
                        <span class="cat-pill">
                            <span class="cat-dot" style="background-color: ${catColor};"></span>
                            <i class="bi ${catIcon} me-1"></i> ${escapeHtml(catName)}
                        </span>
                    </td>
                    <td><span class="${badgeClass}">${item.type}</span></td>
                    <td class="text-end fw-bold ${amountClass}">
                        ${prefix}$${Number(item.amount).toLocaleString('en-US', { minimumFractionDigits: 2 })}
                    </td>
                    <td class="text-end">
                        <button class="btn btn-sm btn-outline-primary me-1 edit-expense-btn" data-id="${item.id}" title="Edit">
                            <i class="bi bi-pencil-square"></i>
                        </button>
                        <button class="btn btn-sm btn-outline-danger delete-expense-btn" data-id="${item.id}" data-title="${escapeHtml(item.title)}" title="Delete">
                            <i class="bi bi-trash3"></i>
                        </button>
                    </td>
                </tr>
            `;
            tbody.append(row);
        });
    }

    // ------------------------------------------------------------------
    // 4. Dynamic Edit Transaction Modal with jQuery AJAX
    // ------------------------------------------------------------------
    $(document).on('click', '.edit-expense-btn', function () {
        const id = $(this).data('id');
        
        $.ajax({
            url: '/api/expenses/' + id,
            method: 'GET',
            success: function (data) {
                $('#editExpenseForm').attr('action', '/expenses/edit/' + data.id);
                $('#editTitle').val(data.title);
                $('#editAmount').val(data.amount);
                $('#editType').val(data.type);
                $('#editCategory').val(data.category ? data.category.id : '');
                $('#editDate').val(data.transactionDate);
                $('#editPaymentMethod').val(data.paymentMethod);
                $('#editNotes').val(data.notes || '');

                const editModal = new bootstrap.Modal(document.getElementById('editExpenseModal'));
                editModal.show();
            },
            error: function () {
                alert('Could not retrieve transaction details.');
            }
        });
    });

    // ------------------------------------------------------------------
    // 5. Delete Confirmation Modal
    // ------------------------------------------------------------------
    $(document).on('click', '.delete-expense-btn', function () {
        const id = $(this).data('id');
        const title = $(this).data('title');

        $('#deleteItemTitle').text(title);
        $('#deleteExpenseForm').attr('action', '/expenses/delete/' + id);

        const deleteModal = new bootstrap.Modal(document.getElementById('deleteConfirmModal'));
        deleteModal.show();
    });

    // ------------------------------------------------------------------
    // 6. Utility Helper
    // ------------------------------------------------------------------
    function escapeHtml(string) {
        if (!string) return '';
        return String(string).replace(/[&<>"']/g, function (s) {
            return {
                '&': '&amp;',
                '<': '&lt;',
                '>': '&gt;',
                '"': '&quot;',
                "'": '&#39;'
            }[s];
        });
    }

    // Auto-dismiss alerts after 4 seconds
    setTimeout(function () {
        $('.alert-dismissible').fadeOut('slow');
    }, 4000);
});
