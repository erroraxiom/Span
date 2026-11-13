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

package com.erroraxiom.span.ui.statsScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme.motionScheme
import androidx.compose.material3.VerticalDragHandle
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldDestinationItem
import androidx.compose.material3.adaptive.layout.rememberPaneExpansionState
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.common.ProvideVicoTheme
import com.patrykandpatrick.vico.compose.common.data.ExtraStore
import com.patrykandpatrick.vico.compose.m3.common.rememberM3VicoTheme
import com.erroraxiom.span.data.Stat
import com.erroraxiom.span.data.StatTime
import com.erroraxiom.span.ui.Screen
import com.erroraxiom.span.ui.androidSystemGestureExclusion
import com.erroraxiom.span.ui.calculatePaneScaffoldDirective
import com.erroraxiom.span.ui.settingsScreen.DetailPlaceholder
import com.erroraxiom.span.ui.statsScreen.screens.LastMonthScreen
import com.erroraxiom.span.ui.statsScreen.screens.LastWeekScreen
import com.erroraxiom.span.ui.statsScreen.screens.StatsMainScreen
import com.erroraxiom.span.ui.statsScreen.viewModel.StatsViewModel
import com.erroraxiom.span.ui.theme.CustomColors.detailPaneTopBarColors
import com.erroraxiom.span.utils.onBack
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import span.shared.generated.resources.Res
import span.shared.generated.resources.hours_and_minutes_format
import span.shared.generated.resources.hours_format
import span.shared.generated.resources.minutes_format
import span.shared.generated.resources.query_stats

@Composable
fun StatsScreenRoot(
    contentPadding: PaddingValues,
    focusGoal: Long,
    modifier: Modifier = Modifier,
    viewModel: StatsViewModel = koinViewModel()
) {
    val backStack = viewModel.backStack

    val todayStat by viewModel.todayStat.collectAsStateWithLifecycle(null)
    val allTimeTotalFocus by viewModel.allTimeTotalFocus.collectAsStateWithLifecycle(null)

    val lastWeekMainChartData by viewModel.lastWeekMainChartData.collectAsStateWithLifecycle()
    val lastWeekAverageFocusTimes by viewModel.lastWeekAverageFocusTimes.collectAsStateWithLifecycle()

    val lastMonthMainChartData by viewModel.lastMonthMainChartData.collectAsStateWithLifecycle()
    val lastMonthAverageFocusTimes by viewModel.lastMonthAverageFocusTimes.collectAsStateWithLifecycle()

    ProvideVicoTheme(rememberM3VicoTheme()) {
        StatsScreen(
            contentPadding = contentPadding,
            focusGoal = focusGoal,
            backStack = backStack,
            viewModel = viewModel,
            todayStat = todayStat,
            allTimeTotalFocus = allTimeTotalFocus,
            lastWeekMainChartData = lastWeekMainChartData,
            lastWeekAverageFocusTimes = lastWeekAverageFocusTimes,
            lastMonthMainChartData = lastMonthMainChartData,
            lastMonthAverageFocusTimes = lastMonthAverageFocusTimes,
            generateSampleData = viewModel::generateSampleData,
            hoursFormat = stringResource(Res.string.hours_format),
            hoursMinutesFormat = stringResource(Res.string.hours_and_minutes_format),
            minutesFormat = stringResource(Res.string.minutes_format),
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun StatsScreen(
    contentPadding: PaddingValues,
    focusGoal: Long,
    backStack: SnapshotStateList<Screen.Stats>,
    viewModel: StatsViewModel,
    todayStat: Stat?,
    allTimeTotalFocus: Long?,
    lastWeekMainChartData: Pair<CartesianChartModelProducer, ExtraStore.Key<List<String>>>,
    lastWeekAverageFocusTimes: StatTime?,
    lastMonthMainChartData: Pair<CartesianChartModelProducer, ExtraStore.Key<List<String>>>,
    lastMonthAverageFocusTimes: StatTime?,
    generateSampleData: () -> Unit,
    hoursFormat: String,
    hoursMinutesFormat: String,
    minutesFormat: String,
    modifier: Modifier = Modifier
) {
    val motionScheme = motionScheme
    val adaptiveInfo = currentWindowAdaptiveInfoV2()
    val widthExpanded = adaptiveInfo.windowSizeClass
        .isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND)

    val navigateToSubScreen: (Screen.Stats) -> Unit = { route ->
        if (backStack.size < 2) backStack.add(route)
        else backStack[backStack.lastIndex] = route
    }

    @Composable
    fun StatsNavDisplay(isTwoPane: Boolean) {
        NavDisplay(
            backStack = backStack,
            onBack = backStack::onBack,
            transitionSpec = {
                fadeIn(motionScheme.defaultEffectsSpec())
                    .togetherWith(fadeOut(motionScheme.defaultEffectsSpec()))
            },
            popTransitionSpec = {
                fadeIn(motionScheme.defaultEffectsSpec())
                    .togetherWith(fadeOut(motionScheme.defaultEffectsSpec()))
            },
            predictivePopTransitionSpec = {
                fadeIn(motionScheme.defaultEffectsSpec())
                    .togetherWith(fadeOut(motionScheme.defaultEffectsSpec()))
            },
            entryProvider = entryProvider {
                entry<Screen.Stats.Main> {
                    if (isTwoPane) {
                        DetailPlaceholder(
                            icon = Res.drawable.query_stats,
                            background = detailPaneTopBarColors.containerColor
                        )
                    } else {
                        StatsMainScreen(
                            goal = focusGoal,
                            contentPadding = contentPadding,
                            lastWeekSummaryChartData = lastWeekMainChartData,
                            lastMonthSummaryChartData = lastMonthMainChartData,
                            todayStat = todayStat,
                            allTimeTotalFocus = allTimeTotalFocus,
                            lastWeekAverageFocusTimes = lastWeekAverageFocusTimes,
                            lastMonthAverageFocusTimes = lastMonthAverageFocusTimes,
                            generateSampleData = generateSampleData,
                            hoursFormat = hoursFormat,
                            hoursMinutesFormat = hoursMinutesFormat,
                            minutesFormat = minutesFormat,
                            onNavigate = navigateToSubScreen
                        )
                    }
                }
                entry<Screen.Stats.LastWeek> {
                    val lastWeekFocusHistoryValues by viewModel.lastWeekFocusHistoryValues.collectAsStateWithLifecycle()
                    val lastWeekFocusBreakdownValues by viewModel.lastWeekFocusBreakdownValues.collectAsStateWithLifecycle()
                    LastWeekScreen(
                        contentPadding = contentPadding,
                        focusBreakdownValues = lastWeekFocusBreakdownValues,
                        focusHistoryValues = lastWeekFocusHistoryValues,
                        mainChartData = lastWeekMainChartData,
                        averageFocusTimes = lastWeekAverageFocusTimes,
                        goal = focusGoal,
                        hoursFormat = hoursFormat,
                        hoursMinutesFormat = hoursMinutesFormat,
                        minutesFormat = minutesFormat,
                        onBack = backStack::onBack
                    )
                }
                entry<Screen.Stats.LastMonth> {
                    val lastMonthCalendarData by viewModel.lastMonthCalendarData.collectAsStateWithLifecycle()
                    val lastMonthFocusBreakdownValues by viewModel.lastMonthFocusBreakdownValues.collectAsStateWithLifecycle()
                    LastMonthScreen(
                        contentPadding = contentPadding,
                        focusBreakdownValues = lastMonthFocusBreakdownValues,
                        calendarData = lastMonthCalendarData,
                        mainChartData = lastMonthMainChartData,
                        averageFocusTimes = lastMonthAverageFocusTimes,
                        goal = focusGoal,
                        hoursFormat = hoursFormat,
                        hoursMinutesFormat = hoursMinutesFormat,
                        minutesFormat = minutesFormat,
                        onBack = backStack::onBack
                    )
                }
            }
        )
    }

    if (!widthExpanded) {
        StatsNavDisplay(isTwoPane = false)
    } else {
        val navigator = rememberListDetailPaneScaffoldNavigator(
            scaffoldDirective = calculatePaneScaffoldDirective(adaptiveInfo),
            initialDestinationHistory = listOf(
                ThreePaneScaffoldDestinationItem(ListDetailPaneScaffoldRole.List),
                ThreePaneScaffoldDestinationItem<Nothing>(ListDetailPaneScaffoldRole.Detail)
            )
        )
        val paneExpansionState = rememberPaneExpansionState()

        ListDetailPaneScaffold(
            directive = navigator.scaffoldDirective,
            scaffoldState = navigator.scaffoldState,
            listPane = {
                AnimatedPane {
                    StatsMainScreen(
                        goal = focusGoal,
                        contentPadding = contentPadding,
                        lastWeekSummaryChartData = lastWeekMainChartData,
                        lastMonthSummaryChartData = lastMonthMainChartData,
                        todayStat = todayStat,
                        allTimeTotalFocus = allTimeTotalFocus,
                        lastWeekAverageFocusTimes = lastWeekAverageFocusTimes,
                        lastMonthAverageFocusTimes = lastMonthAverageFocusTimes,
                        generateSampleData = generateSampleData,
                        hoursFormat = hoursFormat,
                        hoursMinutesFormat = hoursMinutesFormat,
                        minutesFormat = minutesFormat,
                        onNavigate = navigateToSubScreen
                    )
                }
            },
            detailPane = {
                AnimatedPane {
                    StatsNavDisplay(isTwoPane = true)
                }
            },
            paneExpansionState = paneExpansionState,
            paneExpansionDragHandle = {
                val interactionSource = remember { MutableInteractionSource() }
                AnimatedVisibility(
                    visible = navigator.scaffoldValue[ListDetailPaneScaffoldRole.Detail] != PaneAdaptedValue.Hidden,
                    enter = fadeIn(motionScheme.defaultEffectsSpec()) + slideInVertically(
                        motionScheme.defaultSpatialSpec()
                    ),
                    exit = fadeOut(motionScheme.defaultEffectsSpec()) + slideOutVertically(
                        motionScheme.defaultSpatialSpec()
                    )
                ) {
                    VerticalDragHandle(
                        modifier = Modifier
                            .paneExpansionDraggable(
                                paneExpansionState,
                                LocalMinimumInteractiveComponentSize.current,
                                interactionSource
                            )
                            .androidSystemGestureExclusion()
                    )
                }
            },
            modifier = modifier
        )
    }
}
