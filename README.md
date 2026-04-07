# WealthTrack - Secure Offline Personal Finance Android App

[![Android](https://img.shields.io/badge/Android-13%2B-brightgreen)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.20-purple)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/Compose-Material%203-blue)](https://developer.android.com/jetpack/compose)

## Overview

**WealthTrack** is a secure, offline-first personal finance Android application that helps you track investments, calculate returns, manage budgets, and monitor expenses - all while keeping your financial data completely private and encrypted on your device.

**Key Promise**: Your data NEVER leaves your device. No internet permission, no tracking, no data collection.

## Implemented Features

### Budget Tracking with Spending Alerts
- Manual expense entry with categories
- Real-time 50/30/20 budget tracking
- Visual progress bars showing budget utilization
- Smart alerts at 80%/95%/100%+ spending thresholds
- Monthly auto-reset for new budget cycles
- Expense deletion and history tracking

### Dark Mode Toggle
- Three modes: Light, Dark, and Auto (system-based)
- DataStore persistence for user preference
- Material 3 dynamic color theming
- Integrated into Settings screen

### ARR Calculator
- Calculate Annualized Rate of Return
- Formula: `ARR = ((Ending Value / Beginning Value) ^ (1 / Years)) - 1`
- Save results to encrypted database
- Track investment performance over time

### Investment Suggestions (50/30/20 Rule)
- Input monthly income
- Get personalized budget breakdown:
  - **50%** Needs (housing, groceries, utilities)
  - **30%** Wants (entertainment, dining)
  - **20%** Savings & Investments (highlighted)

### Security Features
- **No Internet Permission**: Completely air-gapped
- **Screenshot Prevention**: FLAG_SECURE blocks screenshots and screen recording
- **Biometric Authentication**: Fingerprint/Face unlock required on launch
- **Database Encryption**: SQLCipher 256-bit AES encryption

### Settings
- Theme selection (Light/Dark/Auto)
- App preferences management

## App Navigation (5 Tabs)
1. **Home** - Dashboard with feature cards
2. **ARR Calculator** - Investment return calculator
3. **Investment Suggestion** - 50/30/20 budget planner
4. **Budget Tracker** - Expense tracking with alerts
5. **Settings** - Theme and app preferences

## Tech Stack

| Technology | Purpose |
|------------|---------|
| **Kotlin** | Primary language |
| **Jetpack Compose** | Modern UI framework |
| **Material 3** | Design system |
| **MVVM + Clean Architecture** | Architecture pattern |
| **Dagger Hilt** | Dependency injection |
| **Room + SQLCipher** | Encrypted local database |
| **Biometric API** | Authentication |
| **DataStore** | Preferences storage |
| **Coroutines + Flow** | Async operations |

## Quick Start

### Prerequisites
- Android Studio Koala (2024.4.1) or newer
- JDK 17
- Android device with API 33+ (with biometric hardware)

### Setup
1. Open the project in Android Studio
2. Wait for Gradle sync to complete
3. Click **Run** button (▶️)
4. Authenticate with biometric on first launch
5. Start tracking your finances!

### Build from Command Line
```bash
./gradlew build          # Build the project
./gradlew installDebug   # Install on connected device
```

## Architecture

Built with **Clean Architecture** and **MVVM** pattern:

```
Presentation (Compose UI + ViewModels)
         ↓
Domain (Use Cases - Business Logic)
         ↓
Data (Room Database + SQLCipher)
```

## Project Structure

```
WealthTrack/
├── app/src/main/java/com/wealthtrack/
│   ├── data/              # Data layer (Room + SQLCipher)
│   ├── domain/            # Business logic (Use Cases)
│   ├── presentation/      # UI layer (Compose + ViewModels)
│   ├── navigation/        # Navigation setup
│   ├── MainActivity.kt    # Main entry point
│   └── WealthTrackApplication.kt
├── gradle/libs.versions.toml  # Dependencies
└── build.gradle.kts
```

## Key Benefits

1. **Privacy First**: No data collection, no tracking
2. **Secure**: Biometric lock + encrypted database
3. **Offline**: Works without internet connection
4. **Modern**: Jetpack Compose + Material 3
5. **Clean**: Well-architected codebase
6. **Educational**: Learn modern Android development

## Support

For issues or questions:
- Android Developer Documentation: https://developer.android.com/
- Jetpack Compose: https://developer.android.com/jetpack/compose
- Room Database: https://developer.android.com/training/data-storage/room
- SQLCipher: https://www.zetetic.net/sqlcipher/

---

**Built with modern Android development practices**
