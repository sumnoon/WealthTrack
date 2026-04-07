package com.wealthtrack

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for WealthTrack
 * Hilt requires this for dependency injection initialization
 */
@HiltAndroidApp
class WealthTrackApplication : Application()
