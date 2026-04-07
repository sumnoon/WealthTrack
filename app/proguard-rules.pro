# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in the Android SDK.

# Keep Hilt classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Keep Room entities
-keep class com.wealthtrack.data.local.** { *; }

# Keep SQLCipher classes
-keep class net.sqlcipher.** { *; }
-keep class com.wealthtrack.data.local.WealthTrackDatabase
