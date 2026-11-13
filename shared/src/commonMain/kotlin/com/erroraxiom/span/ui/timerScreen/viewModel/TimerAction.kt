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

package com.erroraxiom.span.ui.timerScreen.viewModel

import com.erroraxiom.span.data.Topic

sealed interface TimerAction {
    data class SkipTimer(val fromButton: Boolean) : TimerAction
    data class SetInfiniteFocus(val value: Boolean) : TimerAction
    data class SetTopic(val topic: Topic) : TimerAction

    data object ResetTimer : TimerAction
    data object UndoReset : TimerAction
    data object StopAlarm : TimerAction
    data object ToggleTimer : TimerAction
}