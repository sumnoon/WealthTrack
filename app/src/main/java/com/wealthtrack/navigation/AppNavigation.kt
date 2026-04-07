package com.wealthtrack.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Navigation routes for the app
 */
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object ARRCalculator : Screen("arr_calculator", "ARR", Icons.Default.Calculate)
    object InvestmentSuggestion : Screen("investment_suggestion", "Invest", Icons.Default.Savings)
    object BudgetTracker : Screen("budget_tracker", "Budget", Icons.Default.AccountBalanceWallet)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

/**
 * Bottom navigation bar items
 */
val bottomNavItems = listOf(
    Screen.Home,
    Screen.ARRCalculator,
    Screen.InvestmentSuggestion,
    Screen.BudgetTracker,
    Screen.Settings
)
