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

package com.erroraxiom.span.ui.timerScreen.viewModel

import androidx.compose.runtime.Immutable
import com.erroraxiom.span.utils.millisecondsToStr

@Immutable
data class TimerState(
    val timerMode: TimerMode = TimerMode.FOCUS,
    val timeStr: String = "25:00",
    val totalTime: Long = 25 * 60 * 1000,
    /** Whether the interval is counting down. Independent of [sessionActive]. */
    val timerRunning: Boolean = false,
    val nextTimerMode: TimerMode = TimerMode.SHORT_BREAK,
    val nextTimeStr: String = "5:00",
    val showBrandTitle: Boolean = true,
    val currentFocusCount: Int = 1,
    val totalFocusCount: Int = 4,
    val alarmRinging: Boolean = false,
    val infiniteFocus: Boolean = false
) {
    /**
     * Whether the timer has been moved off its reset state, which is when the topic must not be
     * switched or edited
     */
    val sessionActive: Boolean
        get() = timerRunning ||
                timerMode != TimerMode.FOCUS ||
                currentFocusCount != 1 ||
                timeStr != millisecondsToStr(if (infiniteFocus) 0 else totalTime)
}

enum class TimerMode {
    FOCUS, SHORT_BREAK, LONG_BREAK, BRAND
}