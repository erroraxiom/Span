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

package com.erroraxiom.span.ui.settingsScreen.viewModel

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.erroraxiom.span.utils.OS
import com.erroraxiom.span.utils.currentOS
import com.erroraxiom.span.utils.getDefaultAlarmTone

@Immutable
data class SettingsState(
    val theme: String = "auto",
    val colorScheme: Color = Color.White,
    val blackTheme: Boolean = false,
    val aodEnabled: Boolean = false,
    val alarmEnabled: Boolean = true,
    val vibrateEnabled: Boolean = true,
    val mediaVolumeForAlarm: Boolean = false,
    val singleProgressBar: Boolean = false,
    val secureAod: Boolean = true,
    val isShowingEraseDataDialog: Boolean = false,

    val vibrationOnDuration: Long = 1000L,
    val vibrationOffDuration: Long = 1000L,
    val vibrationAmplitude: Int = -1,
    val focusGoal: Long = 0L,

    val alarmSoundUri: String? = getDefaultAlarmTone(),

    val customWindowDecor: Boolean = currentOS != OS.WINDOWS,
)
