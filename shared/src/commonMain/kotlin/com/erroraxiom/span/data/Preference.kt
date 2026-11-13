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

package com.erroraxiom.span.data

import androidx.room3.Entity
import androidx.room3.PrimaryKey

/**
 * Class for storing boolean preferences in the app's database
 */
@Entity(tableName = "boolean_preference")
data class BooleanPreference(
    @PrimaryKey
    val key: String,
    val value: Boolean
)

/**
 * Class for storing integer preferences in the app's database
 */
@Entity(tableName = "int_preference")
data class IntPreference(
    @PrimaryKey
    val key: String,
    val value: Long
)

/**
 * Class for storing string preferences in the app's database
 */
@Entity(tableName = "string_preference")
data class StringPreference(
    @PrimaryKey
    val key: String,
    val value: String
)