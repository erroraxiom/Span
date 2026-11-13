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

import com.erroraxiom.span.ui.Screen
import com.erroraxiom.span.ui.SettingsNavItem
import span.shared.generated.resources.Res
import span.shared.generated.resources.alarm
import span.shared.generated.resources.alarm_sound
import span.shared.generated.resources.always_on_display
import span.shared.generated.resources.appearance
import span.shared.generated.resources.black_theme
import span.shared.generated.resources.color_scheme
import span.shared.generated.resources.dnd
import span.shared.generated.resources.durations
import span.shared.generated.resources.label
import span.shared.generated.resources.media_volume_for_alarm
import span.shared.generated.resources.palette
import span.shared.generated.resources.sound
import span.shared.generated.resources.theme
import span.shared.generated.resources.timer
import span.shared.generated.resources.timer_filled
import span.shared.generated.resources.topics
import span.shared.generated.resources.vibrate

val settingsScreens = listOf(
    SettingsNavItem(
        Screen.Settings.Timer,
        Res.drawable.timer_filled,
        Res.string.timer,
        listOf(Res.string.durations, Res.string.dnd, Res.string.always_on_display)
    ),
    SettingsNavItem(
        Screen.Settings.Topics,
        Res.drawable.label,
        Res.string.topics,
        null
    ),
    SettingsNavItem(
        Screen.Settings.Alarm,
        Res.drawable.alarm,
        Res.string.alarm,
        listOf(
            Res.string.alarm_sound,
            Res.string.sound,
            Res.string.vibrate,
            Res.string.media_volume_for_alarm
        )
    ),
    SettingsNavItem(
        Screen.Settings.Appearance,
        Res.drawable.palette,
        Res.string.appearance,
        listOf(Res.string.theme, Res.string.color_scheme, Res.string.black_theme)
    )
)
