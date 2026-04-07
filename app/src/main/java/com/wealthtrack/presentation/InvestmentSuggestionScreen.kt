package com.wealthtrack.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wealthtrack.domain.CalculateInvestmentSuggestionUseCase

/**
 * Jetpack Compose screen for Investment Suggestion using 50/30/20 rule
 * Material 3 design with income input and budget breakdown display
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestmentSuggestionScreen(
    viewModel: InvestmentSuggestionViewModel = hiltViewModel()
) {
    val state = viewModel.state

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Investment Suggestion") },
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
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title and icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Savings,
                    contentDescription = null
                )
                Text(
                    text = "50/30/20 Rule",
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            // Description
            Text(
                text = "Enter your monthly income to see the recommended budget breakdown based on the 50/30/20 rule.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Monthly income input
            OutlinedTextField(
                value = state.monthlyIncome,
                onValueChange = viewModel::onMonthlyIncomeChanged,
                label = { Text("Monthly Income ($)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Savings, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Calculate button
            Button(
                onClick = viewModel::calculateBudget,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Calculate Budget", style = MaterialTheme.typography.titleMedium)
            }

            // Display error message if present
            state.errorMessage?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            // Display budget breakdown
            state.budgetBreakdown?.let { breakdown ->
                BudgetBreakdownCard(breakdown)

                Spacer(modifier = Modifier.height(8.dp))

                // Highlight the 20% savings/investments
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "💰 Recommended Investment/Savings",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            text = String.format("$%.2f / month", breakdown.savingsInvestmentsAmount),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            text = "This 20% should go towards investments, retirement, emergency fund, and debt repayment.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }

                // Clear button
                OutlinedButton(
                    onClick = viewModel::clear,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Clear")
                }
            }
        }
    }
}

/**
 * Card displaying the full 50/30/20 budget breakdown
 */
@Composable
fun BudgetBreakdownCard(breakdown: CalculateInvestmentSuggestionUseCase.BudgetBreakdown) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Budget Breakdown",
                style = MaterialTheme.typography.titleLarge
            )

            Divider()

            // 50% - Needs
            BudgetRow(
                percentage = "50%",
                category = "Needs",
                description = "Housing, groceries, utilities, insurance",
                amount = breakdown.needsAmount,
                color = MaterialTheme.colorScheme.primary
            )

            // 30% - Wants
            BudgetRow(
                percentage = "30%",
                category = "Wants",
                description = "Dining out, entertainment, hobbies",
                amount = breakdown.wantsAmount,
                color = MaterialTheme.colorScheme.secondary
            )

            // 20% - Savings/Investments
            BudgetRow(
                percentage = "20%",
                category = "Savings & Investments",
                description = "Retirement, emergency fund, investments",
                amount = breakdown.savingsInvestmentsAmount,
                color = MaterialTheme.colorScheme.tertiary
            )

            Divider()

            // Total income
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total Monthly Income",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = String.format("$%.2f", breakdown.monthlyIncome),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

/**
 * Single row in the budget breakdown showing percentage, category, and amount
 */
@Composable
fun BudgetRow(
    percentage: String,
    category: String,
    description: String,
    amount: Double,
    color: androidx.compose.ui.graphics.Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = percentage,
                    style = MaterialTheme.typography.titleMedium,
                    color = color
                )
                Text(
                    text = category,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = String.format("$%.2f", amount),
            style = MaterialTheme.typography.titleMedium
        )
    }
}
