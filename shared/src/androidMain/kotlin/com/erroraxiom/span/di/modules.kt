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

package com.erroraxiom.span.di

import android.content.Context
import androidx.room3.Room
import com.erroraxiom.span.BuildKonfig
import com.erroraxiom.span.data.AppDatabase
import com.erroraxiom.span.data.AppTopicRepository
import com.erroraxiom.span.data.MIGRATION_2_3
import com.erroraxiom.span.data.SeedDefaultTopicCallback
import com.erroraxiom.span.data.TopicRepository
import com.erroraxiom.span.ui.settingsScreen.viewModel.SettingsViewModel
import com.erroraxiom.span.ui.statsScreen.viewModel.StatsViewModel
import com.erroraxiom.span.ui.timerScreen.viewModel.TimerViewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.create
import org.koin.plugin.module.dsl.single
import org.koin.plugin.module.dsl.viewModel

val dbModule = module {
    single<AppDatabase> { create(::createDatabase) }
    single { get<AppDatabase>().preferenceDao() }
    single { get<AppDatabase>().statDao() }
    single { get<AppDatabase>().topicDao() }
    single { get<AppDatabase>().systemDao() }
}

val viewModels = module {
    viewModel<TimerViewModel>()
    viewModel<SettingsViewModel>()
    viewModel<StatsViewModel>()
}

val androidModule = module {
    single<AppTopicRepository>() bind TopicRepository::class
}

private fun createDatabase(context: Context): AppDatabase {
    return Room
        .databaseBuilder(
            context,
            AppDatabase::class.java,
            BuildKonfig.DATABASE_NAME
        )
        .addMigrations(MIGRATION_2_3)
        .addCallback(SeedDefaultTopicCallback)
        .build()
}
