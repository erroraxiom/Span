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

package com.erroraxiom.span.ui.statsScreen.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.util.fastMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.columnModel
import com.patrykandpatrick.vico.compose.common.data.ExtraStore
import com.erroraxiom.span.data.Stat
import com.erroraxiom.span.data.StatRepository
import com.erroraxiom.span.data.StatTime
import com.erroraxiom.span.data.Topic
import com.erroraxiom.span.di.AppInfo
import com.erroraxiom.span.ui.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
class StatsViewModel(
    private val statRepository: StatRepository,
    private val appInfo: AppInfo
) : ViewModel() {
    val backStack = mutableStateListOf<Screen.Stats>(Screen.Stats.Main)

    private val dayFormatter = DateTimeFormatter.ofPattern("d MMM")

    val todayStat = statRepository
        .getTodayStat()
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val allTimeTotalFocus = statRepository
        .getAllTimeTotalFocusTime()
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val lastWeekStatsFlow = statRepository
        .getLastNDaysStats(7)
        .distinctUntilChanged()
        .map { list ->
            val reversed = list.reversed()
            fillMissingDays(reversed, 7)
        }
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val lastWeekMainChartModelProducer = CartesianChartModelProducer()
    private val lastWeekMainChartKeys = ExtraStore.Key<List<String>>()
    val lastWeekMainChartData: StateFlow<Pair<CartesianChartModelProducer, ExtraStore.Key<List<String>>>> =
        lastWeekStatsFlow
            .map { list ->
                if (list.isNotEmpty()) {
                    val keys = list.fastMap {
                        it.date.dayOfWeek.getDisplayName(
                            TextStyle.NARROW,
                            Locale.getDefault()
                        )
                    }
                    val values = list.fastMap { it.totalFocusTime() }
                    lastWeekMainChartModelProducer.runTransaction {
                        columnModel { series(values) }
                        extras { it[lastWeekMainChartKeys] = keys }
                    }
                }
                Pair(lastWeekMainChartModelProducer, lastWeekMainChartKeys)
            }
            .flowOn(Dispatchers.Default)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Pair(lastWeekMainChartModelProducer, lastWeekMainChartKeys)
            )

    val lastWeekFocusHistoryValues: StateFlow<List<Pair<String, List<Long>>>> =
        lastWeekStatsFlow
            .map { list ->
                list.fastMap {
                    Pair(
                        "${
                            it.date.dayOfWeek.getDisplayName(
                                TextStyle.SHORT,
                                Locale.getDefault()
                            )
                        }, ${it.date.format(dayFormatter)}",
                        listOf(
                            it.focusTimeQ1,
                            it.focusTimeQ2,
                            it.focusTimeQ3,
                            it.focusTimeQ4
                        )
                    )
                }
            }
            .flowOn(Dispatchers.Default)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val lastWeekAverageFocusTimes: StateFlow<StatTime?> =
        statRepository
            .getLastNDaysAverageFocusTimes(7)
            .distinctUntilChanged()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )

    private val lastWeekBreakdownChartModelProducer = CartesianChartModelProducer()
    val lastWeekFocusBreakdownValues: StateFlow<Pair<CartesianChartModelProducer, Long>> =
        lastWeekAverageFocusTimes
            .map {
                lastWeekBreakdownChartModelProducer.runTransaction {
                    columnModel {
                        series(
                            it?.focusTimeQ1 ?: 0L,
                            it?.focusTimeQ2 ?: 0L,
                            it?.focusTimeQ3 ?: 0L,
                            it?.focusTimeQ4 ?: 0L
                        )
                    }
                }
                Pair(lastWeekBreakdownChartModelProducer, it?.breakTime ?: 0L)
            }
            .flowOn(Dispatchers.Default)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Pair(lastWeekBreakdownChartModelProducer, 0L)
            )

    private val lastMonthStatsFlow = statRepository
        .getLastNDaysStats(30)
        .distinctUntilChanged()
        .map { list ->
            val reversed = list.reversed()
            fillMissingDays(reversed, 30)
        }
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val lastMonthMainChartModelProducer = CartesianChartModelProducer()
    private val lastMonthMainChartKeys = ExtraStore.Key<List<String>>()
    val lastMonthMainChartData: StateFlow<Pair<CartesianChartModelProducer, ExtraStore.Key<List<String>>>> =
        lastMonthStatsFlow
            .map { list ->
                if (list.isNotEmpty()) {
                    val keys = list.fastMap { it.date.dayOfMonth.toString() }
                    val values = list.fastMap { it.totalFocusTime() }
                    lastMonthMainChartModelProducer.runTransaction {
                        columnModel { series(values) }
                        extras { it[lastMonthMainChartKeys] = keys }
                    }
                }
                Pair(lastMonthMainChartModelProducer, lastMonthMainChartKeys)
            }
            .flowOn(Dispatchers.Default)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Pair(lastMonthMainChartModelProducer, lastMonthMainChartKeys)
            )

    val lastMonthCalendarData: StateFlow<List<Stat?>> =
        lastMonthStatsFlow
            .map { list ->
                if (list.isEmpty()) emptyList()
                else List(list.first().date.dayOfWeek.ordinal) { null } + list
            }
            .flowOn(Dispatchers.Default)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val lastMonthAverageFocusTimes: StateFlow<StatTime?> =
        statRepository
            .getLastNDaysAverageFocusTimes(30)
            .distinctUntilChanged()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )

    private val lastMonthBreakdownChartModelProducer = CartesianChartModelProducer()
    val lastMonthFocusBreakdownValues: StateFlow<Pair<CartesianChartModelProducer, Long>> =
        lastMonthAverageFocusTimes
            .map {
                lastMonthBreakdownChartModelProducer.runTransaction {
                    columnModel {
                        series(
                            it?.focusTimeQ1 ?: 0L,
                            it?.focusTimeQ2 ?: 0L,
                            it?.focusTimeQ3 ?: 0L,
                            it?.focusTimeQ4 ?: 0L
                        )
                    }
                }
                Pair(lastMonthBreakdownChartModelProducer, it?.breakTime ?: 0L)
            }
            .flowOn(Dispatchers.Default)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Pair(lastMonthBreakdownChartModelProducer, 0L)
            )

    private fun fillMissingDays(list: List<Stat>, days: Int): List<Stat> {
        val today = LocalDate.now()
        val startDate = today.minusDays((days - 1).toLong())
        val map = list.associateBy { it.date }
        return (0 until days).map { offset ->
            val d = startDate.plusDays(offset.toLong())
            map[d] ?: Stat(d, Topic.DEFAULT_TOPIC_ID, 0, 0, 0, 0, 0)
        }
    }

    fun generateSampleData() {
        if (appInfo.debug) {
            viewModelScope.launch {
                val today = LocalDate.now()
                var it = today.minusDays(90)

                while (it.isBefore(today) || it.isEqual(today)) {
                    val isWeekend =
                        it.dayOfWeek == DayOfWeek.SATURDAY || it.dayOfWeek == DayOfWeek.SUNDAY
                    val baseProb = if (isWeekend) 0.4 else 0.85

                    if (Random.nextDouble() < baseProb) {
                        val maxTotalFocus = if (isWeekend) {
                            Random.nextLong(30 * 60 * 1000L, 3 * 60 * 60 * 1000L)
                        } else {
                            Random.nextLong(1 * 60 * 60 * 1000L, 5 * 60 * 60 * 1000L)
                        }

                        val q1 = (maxTotalFocus * 0.1).toLong()
                        val q2 = (maxTotalFocus * 0.45).toLong()
                        val q3 = (maxTotalFocus * 0.35).toLong()
                        val q4 = (maxTotalFocus * 0.1).toLong()
                        val breakTime = (maxTotalFocus * 0.2).toLong()

                        statRepository.replaceStat(
                            Stat(it, Topic.DEFAULT_TOPIC_ID, q1, q2, q3, q4, breakTime)
                        )
                    } else {
                        statRepository.replaceStat(
                            Stat(it, Topic.DEFAULT_TOPIC_ID, 0, 0, 0, 0, 0)
                        )
                    }

                    it = it.plus(1, ChronoUnit.DAYS)
                }
            }
        }
    }
}