package com.wealthtrack.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wealthtrack.data.local.ExpenseEntity
import com.wealthtrack.domain.AlertSeverity
import com.wealthtrack.domain.BudgetAlert
import com.wealthtrack.domain.GetBudgetStatusUseCase
import java.text.SimpleDateFormat
import java.util.*

/**
 * Budget Tracker Screen with expense tracking and spending alerts
 * Material 3 design with visual budget progress indicators
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetTrackerScreen(
    viewModel: BudgetTrackerViewModel = hiltViewModel()
) {
    val state = viewModel.state

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Budget Tracker") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Display alerts
            if (state.alerts.isNotEmpty()) {
                AlertSection(alerts = state.alerts, onDismiss = viewModel::dismissAlert)
            }

            // Budget status cards
            if (state.budgetStatuses.isNotEmpty()) {
                BudgetStatusSection(statuses = state.budgetStatuses)
            }

            // Add expense form
            AddExpenseForm(state = state, viewModel = viewModel)

            // Recent expenses list
            if (state.expenses.isNotEmpty()) {
                RecentExpensesSection(
                    expenses = state.expenses.take(10),
                    onDelete = viewModel::deleteExpense
                )
            }
        }
    }
}

@Composable
fun AlertSection(alerts: List<BudgetAlert>, onDismiss: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        alerts.forEach { alert ->
            val (color, icon) = when (alert.severity) {
                AlertSeverity.WARNING -> MaterialTheme.colorScheme.tertiaryContainer to Icons.Default.Warning
                AlertSeverity.CRITICAL -> MaterialTheme.colorScheme.errorContainer to Icons.Default.Error
                AlertSeverity.EXCEEDED -> MaterialTheme.colorScheme.error to Icons.Default.Block
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = color)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer)
                    Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
                        Text(
                            text = alert.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                    IconButton(onClick = { onDismiss(alert.category) }) {
                        Icon(Icons.Default.Close, contentDescription = "Dismiss")
                    }
                }
            }
        }
    }
}

@Composable
fun BudgetStatusSection(statuses: List<GetBudgetStatusUseCase.BudgetStatus>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "This Month's Budget",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        statuses.forEach { status ->
            BudgetStatusCard(status = status)
        }
    }
}

@Composable
fun BudgetStatusCard(status: GetBudgetStatusUseCase.BudgetStatus) {
    val progressColor = when (status.status) {
        GetBudgetStatusUseCase.BudgetStatusType.ON_TRACK -> MaterialTheme.colorScheme.primary
        GetBudgetStatusUseCase.BudgetStatusType.WARNING -> MaterialTheme.colorScheme.tertiary
        GetBudgetStatusUseCase.BudgetStatusType.CRITICAL -> MaterialTheme.colorScheme.error
        GetBudgetStatusUseCase.BudgetStatusType.EXCEEDED -> MaterialTheme.colorScheme.error
    }

    val statusIcon = when (status.status) {
        GetBudgetStatusUseCase.BudgetStatusType.ON_TRACK -> Icons.Default.CheckCircle
        GetBudgetStatusUseCase.BudgetStatusType.WARNING -> Icons.Default.Warning
        GetBudgetStatusUseCase.BudgetStatusType.CRITICAL -> Icons.Default.Error
        GetBudgetStatusUseCase.BudgetStatusType.EXCEEDED -> Icons.Default.Block
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(statusIcon, contentDescription = null, tint = progressColor)
                    Text(
                        text = status.budgetCategory,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "${String.format("%.0f", status.percentageUsed)}%",
                    style = MaterialTheme.typography.titleMedium,
                    color = progressColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = (status.percentageUsed / 100f).toFloat().coerceIn(0f, 1f),
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = progressColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Spent: $${String.format("%.2f", status.spentAmount)}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Budget: $${String.format("%.2f", status.budgetedAmount)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Remaining: $${String.format("%.2f", status.remainingAmount)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (status.remainingAmount < 0) MaterialTheme.colorScheme.error else Color.Unspecified
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseForm(state: BudgetTrackerState, viewModel: BudgetTrackerViewModel) {
    val categories = listOf("Groceries", "Rent", "Utilities", "Dining Out", "Entertainment", "Transportation", "Shopping", "Healthcare", "Investment", "Savings", "Other")

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Add Expense",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Amount input
            OutlinedTextField(
                value = state.expenseAmount,
                onValueChange = viewModel::onExpenseAmountChanged,
                label = { Text("Amount ($)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Category dropdown
            var categoryExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = it }
            ) {
                OutlinedTextField(
                    value = state.expenseCategory,
                    onValueChange = { },
                    label = { Text("Category") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Category, contentDescription = null) },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) }
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = {
                                viewModel.onExpenseCategoryChanged(category)
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Budget Category selector
            var budgetCategoryExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = budgetCategoryExpanded,
                onExpandedChange = { budgetCategoryExpanded = it }
            ) {
                OutlinedTextField(
                    value = state.expenseBudgetCategory,
                    onValueChange = { },
                    label = { Text("Budget Category") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    singleLine = true,
                    readOnly = true
                )
                ExposedDropdownMenu(
                    expanded = budgetCategoryExpanded,
                    onDismissRequest = { budgetCategoryExpanded = false }
                ) {
                    listOf("Needs", "Wants", "Savings").forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = {
                                viewModel.onExpenseBudgetCategoryChanged(category)
                                budgetCategoryExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            OutlinedTextField(
                value = state.expenseDescription,
                onValueChange = viewModel::onExpenseDescriptionChanged,
                label = { Text("Description (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Date picker would go here (simplified for now)
            Text(
                text = "Date: ${formatDate(state.expenseDate)}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Error message
            state.errorMessage?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Success message
            state.successMessage?.let { success ->
                Text(
                    text = success,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = viewModel::saveExpense,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Save Expense")
                }
                OutlinedButton(
                    onClick = viewModel::clearForm,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Clear")
                }
            }
        }
    }
}

@Composable
fun RecentExpensesSection(expenses: List<ExpenseEntity>, onDelete: (ExpenseEntity) -> Unit) {
    Column {
        Text(
            text = "Recent Expenses",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            expenses.forEach { expense ->
                ExpenseCard(expense = expense, onDelete = onDelete)
            }
        }
    }
}

@Composable
fun ExpenseCard(expense: ExpenseEntity, onDelete: (ExpenseEntity) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = expense.category,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "• ${expense.budgetCategory}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (expense.description.isNotBlank()) {
                    Text(
                        text = expense.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = formatDate(expense.date),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$${String.format("%.2f", expense.amount)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                IconButton(onClick = { onDelete(expense) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
