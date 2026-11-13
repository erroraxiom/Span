/*
 * Copyright (c) 2026 Erroraxiom
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

package com.erroraxiom.span.ui.settingsScreen

import androidx.compose.runtime.Composable

const val SYSTEM_DEFAULT_AMPLITUDE = -1

interface PlatformVibrator {
    val hasVibrator: Boolean
    val hasAmplitudeControl: Boolean

    fun playWaveform(onDuration: Long, offDuration: Long, amplitude: Int)
    fun cancel()
}

@Composable
expect fun rememberPlatformVibrator(): PlatformVibrator