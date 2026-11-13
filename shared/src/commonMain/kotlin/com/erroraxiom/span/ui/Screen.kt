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

package com.erroraxiom.span.ui

import androidx.compose.runtime.Immutable
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

sealed interface Screen : NavKey {
    @Serializable
    data object Timer : Screen

    @Serializable
    data object AOD : Screen

    @Serializable
    sealed class Settings : Screen {
        @Serializable
        data object Main : Settings()

        @Serializable
        data object Alarm : Settings()

        @Serializable
        data object Appearance : Settings()

        @Serializable
        data object Timer : Settings()

        @Serializable
        data object Topics : Settings()

        @Serializable
        data object About : Settings()
    }

    @Serializable
    sealed class Stats : Screen {
        @Serializable
        data object Main : Stats()

        @Serializable
        data object LastWeek : Stats()

        @Serializable
        data object LastMonth : Stats()
    }
}

@Immutable
data class NavItem<T : Screen>(
    val route: T,
    val unselectedIcon: DrawableResource,
    val selectedIcon: DrawableResource,
    val label: StringResource,
    val onNavigateHome: () -> Unit
)

@Immutable
data class SettingsNavItem(
    val route: Screen.Settings,
    val icon: DrawableResource,
    val label: StringResource,
    val innerSettings: List<StringResource>? = null
)
