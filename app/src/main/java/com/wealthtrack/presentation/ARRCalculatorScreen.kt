package com.wealthtrack.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Jetpack Compose screen for the ARR Calculator
 * Material 3 design with form inputs and result display
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ARRCalculatorScreen(
    viewModel: ARRViewModel = hiltViewModel()
) {
    val state = viewModel.state

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ARR Calculator") },
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
                    imageVector = Icons.Default.Calculate,
                    contentDescription = null
                )
                Text(
                    text = "Annualized Rate of Return",
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            // Investment name input
            OutlinedTextField(
                value = state.investmentName,
                onValueChange = viewModel::onNameChanged,
                label = { Text("Investment Name (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Beginning value input
            OutlinedTextField(
                value = state.beginningValue,
                onValueChange = viewModel::onBeginningValueChanged,
                label = { Text("Beginning Value ($)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )

            // Ending value input
            OutlinedTextField(
                value = state.endingValue,
                onValueChange = viewModel::onEndingValueChanged,
                label = { Text("Ending Value ($)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )

            // Number of years input
            OutlinedTextField(
                value = state.numberOfYears,
                onValueChange = viewModel::onNumberOfYearsChanged,
                label = { Text("Number of Years") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Calculate button
            Button(
                onClick = viewModel::calculateARR,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Calculate ARR", style = MaterialTheme.typography.titleMedium)
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

            // Display success message if present
            state.successMessage?.let { message ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = message,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Display result
            state.formattedARR?.let { arr ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Your ARR",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = arr,
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action buttons row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Save button
                Button(
                    onClick = viewModel::saveInvestment,
                    modifier = Modifier.weight(1f),
                    enabled = state.calculatedARR != null
                ) {
                    Text("Save")
                }

                // Clear button
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
