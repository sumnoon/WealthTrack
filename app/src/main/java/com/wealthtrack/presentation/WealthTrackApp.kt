package com.wealthtrack.presentation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.wealthtrack.navigation.Screen
import com.wealthtrack.navigation.bottomNavItems

/**
 * Main app composable with bottom navigation
 * Shown after successful biometric authentication
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WealthTrackApp() {
    val navController = rememberNavController()
    val layoutDirection = LocalLayoutDirection.current

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(Screen.Home.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(screen.icon, contentDescription = screen.title, modifier = Modifier.size(24.dp)) },
                        label = {
                            Text(
                                text = screen.title,
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1,
                                softWrap = false
                            )
                        },
                        alwaysShowLabel = true
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(
                PaddingValues(
                    start = innerPadding.calculateStartPadding(layoutDirection),
                    end = innerPadding.calculateEndPadding(layoutDirection),
                    top = innerPadding.calculateTopPadding()
                )
            )
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToARR = { navController.navigate(Screen.ARRCalculator.route) },
                    onNavigateToSuggestion = { navController.navigate(Screen.InvestmentSuggestion.route) },
                    onNavigateToBudgetTracker = { navController.navigate(Screen.BudgetTracker.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                )
            }
            composable(Screen.ARRCalculator.route) {
                ARRCalculatorScreen()
            }
            composable(Screen.InvestmentSuggestion.route) {
                InvestmentSuggestionScreen()
            }
            composable(Screen.BudgetTracker.route) {
                BudgetTrackerScreen()
            }
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
        }
    }
}

/**
 * Home screen with navigation to features
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToARR: () -> Unit,
    onNavigateToSuggestion: () -> Unit,
    onNavigateToBudgetTracker: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "WealthTrack",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Your secure, offline personal finance tracker",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = onNavigateToARR
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "📈 ARR Calculator",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Calculate your Annualized Rate of Return",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = onNavigateToSuggestion
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "💰 Investment Suggestion",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Get personalized budget recommendations (50/30/20)",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = onNavigateToBudgetTracker
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "📊 Budget Tracker",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Track expenses and monitor your budget",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = onNavigateToSettings
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "⚙️ Settings",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Customize theme and app preferences",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
