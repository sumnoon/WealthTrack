package com.wealthtrack

import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.wealthtrack.presentation.ThemeViewModel
import com.wealthtrack.presentation.WealthTrackApp
import com.wealthtrack.ui.theme.WealthTrackTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Activity with security features:
 * 1. FLAG_SECURE to prevent screenshots and screen recording
 * 2. Biometric authentication enforcement before accessing any data
 */
@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    private val themeViewModel: ThemeViewModel by viewModels()
    private var isBiometricAuthenticated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // SECURITY: Prevent screenshots and screen recording
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        // Check biometric availability
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                // Biometric is available, show authentication screen
                setContent {
                    val currentTheme by themeViewModel.themeMode.collectAsState()
                    val darkTheme = when (currentTheme) {
                        "LIGHT" -> false
                        "DARK" -> true
                        "AUTO" -> isSystemInDarkTheme()
                        else -> isSystemInDarkTheme()
                    }

                    WealthTrackTheme(darkTheme = darkTheme) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            BiometricAuthScreen(
                                onAuthenticate = { showBiometricPrompt() }
                            )
                        }
                    }
                }
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Toast.makeText(
                    this,
                    "No biometric hardware available. App requires biometric authentication.",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Toast.makeText(
                    this,
                    "Biometric hardware unavailable.",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Toast.makeText(
                    this,
                    "Please enroll a fingerprint/face to use this app.",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
            else -> {
                Toast.makeText(
                    this,
                    "Biometric authentication not available.",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }

    /**
     * Displays the biometric authentication prompt
     */
    private fun showBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    isBiometricAuthenticated = true
                    // Navigate to main app content
                    setContent {
                        val currentTheme by themeViewModel.themeMode.collectAsState()
                        val darkTheme = when (currentTheme) {
                            "LIGHT" -> false
                            "DARK" -> true
                            "AUTO" -> isSystemInDarkTheme()
                            else -> isSystemInDarkTheme()
                        }

                        WealthTrackTheme(darkTheme = darkTheme) {
                            WealthTrackApp()
                        }
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        this@MainActivity,
                        "Authentication failed. Try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(
                        this@MainActivity,
                        "Authentication error: $errString",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Authenticate to Access WealthTrack")
            .setSubtitle("Use your fingerprint or face to unlock")
            .setNegativeButtonText("Cancel")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}

/**
 * Biometric authentication screen shown before user verifies identity
 */
@Composable
fun BiometricAuthScreen(onAuthenticate: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "WealthTrack",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Secure Personal Finance",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onAuthenticate,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Authenticate")
        }
    }
}
