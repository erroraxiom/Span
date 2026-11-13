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

package com.erroraxiom.span.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.materialkolor.rememberDynamicColorScheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
actual fun SpanTheme(
    darkTheme: Boolean,
    seedColor: Color,
    dynamicColor: Boolean,
    blackTheme: Boolean,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val isAmoled = blackTheme && darkTheme

    val baseColorScheme = when {
        seedColor != Color.White -> {
            rememberDynamicColorScheme(
                seedColor = seedColor,
                isDark = darkTheme,
                isAmoled = isAmoled
            )
        }
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            remember(context, darkTheme, isAmoled) {
                val dynamicScheme = if (darkTheme) {
                    dynamicDarkColorScheme(context)
                } else {
                    dynamicLightColorScheme(context)
                }
                if (isAmoled) {
                    dynamicScheme.copy(
                        background = Color.Black,
                        surface = Color.Black,
                        surfaceDim = Color.Black,
                        surfaceContainerLowest = Color.Black,
                        surfaceContainerLow = Color(0xFF0E0E0E),
                        surfaceContainer = Color(0xFF161616),
                        surfaceContainerHigh = Color(0xFF1F1F1F),
                        surfaceContainerHighest = Color(0xFF282828)
                    )
                } else {
                    dynamicScheme
                }
            }
        }
        else -> {
            rememberDynamicColorScheme(
                seedColor = Color(0xFFFFA79B),
                isDark = darkTheme,
                isAmoled = isAmoled
            )
        }
    }

    val appFonts = rememberAppFonts()
    val appTypography = typography(appFonts)
    val expressiveMotionScheme = remember { MotionScheme.expressive() }

    val view = LocalView.current
    if (!view.isInEditMode) {
        LaunchedEffect(darkTheme) {
            val window = (view.context as? Activity)?.window ?: return@LaunchedEffect
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(
        LocalDarkTheme provides darkTheme,
        LocalBlackTheme provides isAmoled,
        LocalSeedColor provides seedColor,
        LocalAppFonts provides appFonts
    ) {
        MaterialExpressiveTheme(
            colorScheme = baseColorScheme,
            shapes = SpanShapes,
            typography = appTypography,
            motionScheme = expressiveMotionScheme,
            content = content
        )
    }
}