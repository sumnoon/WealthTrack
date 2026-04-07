package com.wealthtrack.navigation

import androidx.annotation.DrawableRes
import com.wealthtrack.R

/**
 * Navigation routes for the app
 */
sealed class Screen(val route: String, val title: String, @DrawableRes val iconRes: Int) {
    object Home : Screen("home", "Home", R.drawable.ic_nav_home)
    object ARRCalculator : Screen("arr_calculator", "ARR", R.drawable.ic_nav_arr)
    object InvestmentSuggestion : Screen("investment_suggestion", "Invest", R.drawable.ic_nav_invest)
    object BudgetTracker : Screen("budget_tracker", "Budget", R.drawable.ic_nav_budget)
    object Settings : Screen("settings", "Settings", R.drawable.ic_nav_settings)
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
