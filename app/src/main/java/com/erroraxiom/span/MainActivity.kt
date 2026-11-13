/*
 * Copyright (c) 2025-2026 Erroraxiom
 *
 * This file is part of Span - a minimalist pomodoro timer for Android.
 *
 * Span is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Span is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Span.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package com.erroraxiom.span

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.erroraxiom.span.data.StateRepository
import com.erroraxiom.span.di.ActivityCallbacks
import com.erroraxiom.span.ui.AppScreen
import com.erroraxiom.span.ui.settingsScreen.viewModel.SettingsViewModel
import com.erroraxiom.span.ui.theme.SpanTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val settingsViewModel: SettingsViewModel by inject()
    private val stateRepository: StateRepository by inject()
    private val activityCallbacks: ActivityCallbacks by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        activityCallbacks.activityTurnScreenOn = { value ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                setTurnScreenOn(value)
            } else {
                if (value) window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
                else window.clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
            }
        }

        setContent {
            val settingsState by settingsViewModel.settingsState.collectAsStateWithLifecycle()

            val darkTheme = when (settingsState.theme) {
                "dark" -> true
                "light" -> false
                else -> isSystemInDarkTheme()
            }

            val seed = settingsState.colorScheme

            val isAlwaysOnDisplayEnabled = settingsState.aodEnabled

            DisposableEffect(Unit) {
                onDispose {
                    activityCallbacks.activityTurnScreenOn = {}
                }
            }

            SpanTheme(
                darkTheme = darkTheme,
                seedColor = seed,
                dynamicColor = seed == Color.White,
                blackTheme = settingsState.blackTheme
            ) {
                AppScreen(
                    isAODEnabled = isAlwaysOnDisplayEnabled,
                    setTimerFrequency = { freq ->
                        stateRepository.screenTimerFrequency = freq
                    }
                )
            }
        }
    }
}