# WealthTrack - Problems & Issues Log

This document tracks all technical issues encountered during the development and testing of the WealthTrack application.

---

## Current Session Issues

### Issue 1: Category Dropdown Not Working in Budget Tracker
**Date**: 2026-04-07
**Severity**: High (Blocks core functionality)
**Status**: Fixed

**Problem:**
The category dropdown in the Add Expense form (BudgetTrackerScreen.kt) was not allowing users to select a category. The dropdown appeared unclickable.

**Root Cause:**
- The `ExposedDropdownMenuBox` `expanded` state was incorrectly checking if `expenseCategory.isNotEmpty()` instead of using a proper boolean state
- The `onExpandedChange` callback was empty, not toggling the dropdown state
- No trailing icon indicated the dropdown was clickable
- Setting `readOnly = true` without proper state management confused user interaction

**Solution:**
- Added `categoryExpanded` state variable using `remember { mutableStateOf(false) }`
- Connected `onExpandedChange` to toggle the state: `onExpandedChange = { categoryExpanded = it }`
- Added trailing icon: `ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)`
- Set dropdown to close when item selected: `categoryExpanded = false`
- Made `onValueChange` empty since category is selected from dropdown only

**Files Modified:**
- `app/src/main/java/com/wealthtrack/presentation/BudgetTrackerScreen.kt` (lines 238-270)

---

### Issue 2: Bottom Navigation Icons and Labels Overlapping
**Date**: 2026-04-07
**Severity**: High (UI/UX issue)
**Status**: Fixed

**Problem:**
The bottom navigation bar showed overlapping icons and text labels. With 5 navigation items, the space was too cramped for long labels like "ARR Calculator" and "Investment Suggestion".

**Root Cause:**
- Too many navigation items (5) for the available bottom navigation space
- Long navigation labels ("ARR Calculator", "Investment Suggestion", "Budget Tracker") caused text overflow
- No style constraints on label text wrapping
- Padding conflict between parent Scaffold and child screen Scaffolds

**Solution Applied (Attempt 1):**
- Shortened navigation labels in `AppNavigation.kt`:
  - "ARR Calculator" → "ARR"
  - "Investment Suggestion" → "Invest"
  - "Budget Tracker" → "Budget"
- Added `alwaysShowLabel = true` to NavigationBarItems
- Set `maxLines = 1` and `softWrap = false` on labels
- Used `labelSmall` typography for smaller text
- Added explicit icon sizing (24.dp)

**Solution Applied (Attempt 2 - Padding Fix):**
- Changed NavHost modifier from full padding to selective padding
- Only applied top, start, and end padding (not bottom) to prevent double-padding

**Issues During Fix:**
- Multiple compilation errors due to import conflicts
- `Modifier` import was accidentally duplicated then removed, causing "Unresolved reference: Modifier" errors across 18 locations
- `calculateStartPadding()` and `calculateEndPadding()` require `LayoutDirection` parameter

**Final Solution:**
- Added `LocalLayoutDirection.current` to get layout direction
- Passed `layoutDirection` to padding calculation methods
- Wrapped padding in `PaddingValues()` constructor
- Added missing `Modifier` import back

**Files Modified:**
- `app/src/main/java/com/wealthtrack/navigation/AppNavigation.kt` (shortened titles)
- `app/src/main/java/com/wealthtrack/presentation/WealthTrackApp.kt` (navigation styling and padding)

---

### Issue 3: Compilation Errors in WealthTrackApp.kt
**Date**: 2026-04-07
**Severity**: High (Build blocking)
**Status**: Fixed

**Errors Encountered:**

1. **Conflicting Modifier imports** (2 errors)
   ```
   e: file:///G:/ClaudeCode/Android/WealthTrack/app/src/main/java/com/wealthtrack/presentation/WealthTrackApp.kt:4:28
   Conflicting import, imported name 'Modifier' is ambiguous
   ```
   **Cause**: `Modifier` was imported twice (lines 4 and 8)
   **Fix**: Removed duplicate import

2. **Unresolved reference: Modifier** (18 errors)
   ```
   e: file:///G:/ClaudeCode/Android/WealthTrack/app/src/main/java/com/wealthtrack/presentation/WealthTrackApp.kt:46:98
   Unresolved reference: Modifier
   ```
   **Cause**: Modifier import was accidentally removed during edit
   **Fix**: Re-added `import androidx.compose.ui.Modifier`

3. **Missing layoutDirection parameter** (2 errors)
   ```
   e: file:///G:/ClaudeCode/Android/WealthTrack/app/src/main/java/com/wealthtrack/presentation/WealthTrackApp.kt:67:63
   No value passed for parameter 'layoutDirection'
   ```
   **Cause**: `calculateStartPadding()` and `calculateEndPadding()` require a `LayoutDirection` parameter in newer Compose versions
   **Fix**: 
   - Added `import androidx.compose.ui.platform.LocalLayoutDirection`
   - Retrieved `val layoutDirection = LocalLayoutDirection.current`
   - Passed to methods: `innerPadding.calculateStartPadding(layoutDirection)`

**Resolution Time**: ~15 minutes of iterative fixes

---

## Previous Session Issues (From Documentation)

### Issue 4: Compose Compiler Version Mismatch
**Date**: Prior session
**Status**: Fixed

**Problem:**
```
Compose Compiler 1.3.2 requires Kotlin 1.7.20 but you're using 1.9.22
```

**Solution:**
Added `composeOptions { kotlinCompilerExtensionVersion = "1.5.8" }` to `app/build.gradle.kts`

**File Modified:**
- `app/build.gradle.kts`

---

### Issue 5: Multiple Compilation Errors (50+ errors)
**Date**: Prior session
**Status**: Fixed

**Problems Fixed:**

1. **ExpenseEntity.kt - Invalid Index annotation**
   - Removed `order = Index.Order.DESC` (invalid parameter)

2. **BackupRestoreUseCase.kt - Missing File import**
   - Changed `File` to `java.io.File`

3. **Theme.kt - Typography definition**
   - Created custom `WealthTrackTypography` object

4. **MainActivity.kt - Missing imports**
   - Added `import androidx.compose.ui.unit.dp`
   - Added `import com.wealthtrack.presentation.WealthTrackApp`

5. **WealthTrackDatabase.kt - Type mismatch**
   - Fixed builder chain to properly call `.build()`

6. **SettingsScreen.kt - collectAsState syntax**
   - Fixed with proper lambda syntax

7. **BudgetTrackerScreen.kt - Missing imports**
   - Added `AlertSeverity` and `BudgetAlert` imports

8. **WealthTrackApp.kt - Missing dp import and duplicate route**
   - Added dp import
   - Removed duplicate BudgetTrackerScreen composable

**Files Modified:**
- 8 files total

---

### Issue 6: Gradle Wrapper Missing
**Date**: Prior session
**Status**: Fixed

**Problem:**
`gradlew` script not found, preventing command-line builds.

**Solution:**
Generated wrapper using system Gradle:
```bash
gradle wrapper
```

---

### Issue 7: Plugin Errors
**Date**: Prior session
**Status**: Fixed

**Problem:**
Various plugin-related build errors during initial setup.

**Solution:**
- Cleaned and synced Gradle files
- Verified plugin versions in `gradle/libs.versions.toml`
- Used Android Studio's "Sync Project with Gradle Files"

---

## Summary of Issues

| Issue | Date | Severity | Status | Resolution Time |
|-------|------|----------|--------|-----------------|
| Category dropdown not working | 2026-04-07 | High | Fixed | ~10 min |
| Bottom navigation overlap | 2026-04-07 | High | Fixed | ~20 min |
| Compilation errors (Modifier, layoutDirection) | 2026-04-07 | High | Fixed | ~15 min |
| Compose compiler mismatch | Prior | Medium | Fixed | ~5 min |
| 50+ compilation errors | Prior | Critical | Fixed | ~30 min |
| Gradle wrapper missing | Prior | Medium | Fixed | ~2 min |
| Plugin errors | Prior | Low | Fixed | ~5 min |

## Lessons Learned

1. **State Management in Compose**: Always use proper state variables (`mutableStateOf`) for UI state, don't derive expanded state from content state.

2. **ExposedDropdownMenuBox Requirements**: Requires explicit state management and trailing icons for optimal UX.

3. **Compose Padding APIs**: `calculateStartPadding()` and `calculateEndPadding()` require `LayoutDirection` parameter in recent versions.

4. **Navigation Label Length**: Keep bottom navigation labels short (1-2 words) to prevent overlap, especially with 4+ items.

5. **Import Management**: Be careful when editing imports to avoid duplicates or accidental removals.

6. **Clean Architecture Benefits**: Isolating issues to specific layers (Presentation vs Data) makes debugging easier.

## Current Build Status

**Status**: Building successfully
**Last Successful Build**: 2026-04-07
**All Features**: Operational

The app is now stable with all requested features implemented and the UI displaying correctly.
