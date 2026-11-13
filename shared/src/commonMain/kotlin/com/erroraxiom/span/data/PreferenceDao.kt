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

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy.Companion.REPLACE
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PreferenceDao {
    @Insert(onConflict = REPLACE)
    suspend fun insertIntPreference(preference: IntPreference)

    @Insert(onConflict = REPLACE)
    suspend fun insertBooleanPreference(preference: BooleanPreference)

    @Insert(onConflict = REPLACE)
    suspend fun insertStringPreference(preference: StringPreference)

    @Query("DELETE FROM int_preference")
    suspend fun resetIntPreferences()

    @Query("DELETE FROM boolean_preference")
    suspend fun resetBooleanPreferences()

    @Query("DELETE FROM string_preference")
    suspend fun resetStringPreferences()

    @Query("SELECT value FROM int_preference WHERE `key` = :key")
    suspend fun getIntPreference(key: String): Long?

    @Query("SELECT value FROM boolean_preference WHERE `key` = :key")
    suspend fun getBooleanPreference(key: String): Boolean?

    @Query("SELECT value FROM boolean_preference WHERE `key` = :key")
    fun getBooleanPreferenceFlow(key: String): Flow<Boolean>

    @Query("SELECT value FROM string_preference WHERE `key` = :key")
    suspend fun getStringPreference(key: String): String?

    @Query("SELECT value FROM string_preference WHERE `key` = :key")
    fun getStringPreferenceFlow(key: String): Flow<String>
}