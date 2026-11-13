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

package com.erroraxiom.span.ui.statsScreen.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.common.data.ExtraStore
import com.erroraxiom.span.data.StatTime
import com.erroraxiom.span.ui.mergePaddingValues
import com.erroraxiom.span.ui.statsScreen.components.FocusBreakRatioVisualization
import com.erroraxiom.span.ui.statsScreen.components.FocusBreakdownChart
import com.erroraxiom.span.ui.statsScreen.components.HorizontalStackedBar
import com.erroraxiom.span.ui.statsScreen.components.TimeColumnChart
import com.erroraxiom.span.ui.theme.CustomColors.detailPaneTopBarColors
import com.erroraxiom.span.ui.theme.CustomColors.listItemColors
import com.erroraxiom.span.ui.theme.CustomColors.topBarColors
import com.erroraxiom.span.ui.theme.LocalAppFonts
import com.erroraxiom.span.ui.theme.SpanShapeDefaults.PANE_MAX_WIDTH
import com.erroraxiom.span.ui.theme.SpanShapeDefaults.bottomListItemShape
import com.erroraxiom.span.ui.theme.SpanShapeDefaults.cardShape
import com.erroraxiom.span.ui.theme.SpanShapeDefaults.middleListItemShape
import com.erroraxiom.span.ui.theme.SpanShapeDefaults.topListItemShape
import com.erroraxiom.span.ui.topBarWindowInsets
import com.erroraxiom.span.utils.millisecondsToHoursMinutes
import com.erroraxiom.span.utils.millisecondsToMinutes
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import span.shared.generated.resources.Res
import span.shared.generated.resources.arrow_back
import span.shared.generated.resources.back
import span.shared.generated.resources.focus_break_ratio
import span.shared.generated.resources.focus_breakdown
import span.shared.generated.resources.focus_breakdown_desc
import span.shared.generated.resources.focus_history
import span.shared.generated.resources.focus_history_desc
import span.shared.generated.resources.focus_per_day_avg
import span.shared.generated.resources.last_week
import span.shared.generated.resources.stats

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun LastWeekScreen(
    contentPadding: PaddingValues,
    focusBreakdownValues: Pair<CartesianChartModelProducer, Long>,
    focusHistoryValues: List<Pair<String, List<Long>>>,
    mainChartData: Pair<CartesianChartModelProducer, ExtraStore.Key<List<String>>>,
    averageFocusTimes: StatTime?,
    goal: Long,
    hoursFormat: String,
    hoursMinutesFormat: String,
    minutesFormat: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val fonts = LocalAppFonts.current

    val widthExpanded = currentWindowAdaptiveInfoV2()
        .windowSizeClass
        .isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND)

    val avgFocusList = remember(averageFocusTimes) {
        if (averageFocusTimes != null) {
            listOf(
                averageFocusTimes.focusTimeQ1,
                averageFocusTimes.focusTimeQ2,
                averageFocusTimes.focusTimeQ3,
                averageFocusTimes.focusTimeQ4
            )
        } else listOf(0L, 0L, 0L, 0L)
    }

    val rankList = remember(avgFocusList) {
        val sortedIndices = avgFocusList.indices.sortedByDescending { avgFocusList[it] }
        val ranks = MutableList(avgFocusList.size) { 0 }
        sortedIndices.forEachIndexed { rank, originalIndex ->
            ranks[originalIndex] = rank
        }
        ranks
    }

    fun formatDuration(millis: Long): String {
        return if (millis >= 60 * 60 * 1000L) {
            millisecondsToHoursMinutes(millis, hoursMinutesFormat)
        } else {
            millisecondsToMinutes(millis, minutesFormat)
        }
    }

    val barColors = if (widthExpanded) detailPaneTopBarColors else topBarColors

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(barColors.containerColor)
    ) {
        Scaffold(
            topBar = {
                LargeFlexibleTopAppBar(
                    title = {
                        Text(
                            stringResource(Res.string.last_week),
                            fontFamily = fonts.topBarTitle
                        )
                    },
                    subtitle = {
                        Text(stringResource(Res.string.stats))
                    },
                    navigationIcon = {
                        if (!widthExpanded)
                            FilledTonalIconButton(
                                onClick = onBack,
                                shapes = IconButtonDefaults.shapes(),
                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                    containerColor = listItemColors.containerColor
                                )
                            ) {
                                Icon(
                                    painterResource(Res.drawable.arrow_back),
                                    stringResource(Res.string.back)
                                )
                            }
                    },
                    colors = barColors,
                    scrollBehavior = scrollBehavior,
                    windowInsets = if (!widthExpanded) topBarWindowInsets() else WindowInsets()
                )
            },
            containerColor = barColors.containerColor,
            modifier = Modifier
                .widthIn(max = PANE_MAX_WIDTH)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) { innerPadding ->
            val insets = mergePaddingValues(innerPadding, contentPadding)
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                contentPadding = insets,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                item(key = "header") {
                    Spacer(Modifier.height(16.dp))
                    Column(Modifier.padding(start = 16.dp)) {
                        Text(
                            text = remember(avgFocusList, hoursMinutesFormat, minutesFormat) {
                                formatDuration(avgFocusList.sum())
                            },
                            style = typography.displaySmall
                        )
                        Text(
                            text = stringResource(Res.string.focus_per_day_avg),
                            style = typography.titleSmall
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                }

                item(key = "chart") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(cardShape)
                            .background(listItemColors.containerColor)
                            .padding(vertical = 16.dp)
                    ) {
                        TimeColumnChart(
                            modelProducer = mainChartData.first,
                            goal = goal,
                            hoursFormat = hoursFormat,
                            hoursMinutesFormat = hoursMinutesFormat,
                            minutesFormat = minutesFormat,
                            xValueFormatter = remember(mainChartData.second) {
                                CartesianValueFormatter { context, x, _ ->
                                    context.model.extraStore.getOrNull(mainChartData.second)
                                        ?.getOrNull(x.toInt()) ?: ""
                                }
                            },
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }
                }

                item(key = "spacer_1") { Spacer(Modifier.height(12.dp)) }

                item(key = "history_header") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(topListItemShape)
                            .background(listItemColors.containerColor)
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                    ) {
                        Text(
                            stringResource(Res.string.focus_history),
                            style = typography.headlineSmall.copy(fontFamily = fonts.topBarTitle)
                        )
                        Text(
                            stringResource(Res.string.focus_history_desc),
                            style = typography.bodySmall,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                }
                itemsIndexed(
                    focusHistoryValues,
                    key = { _, item -> "history_${item.first}" }
                ) { index, item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(
                                if (index != focusHistoryValues.lastIndex) middleListItemShape
                                else bottomListItemShape
                            )
                            .background(listItemColors.containerColor)
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        val sum = remember(item.second) { item.second.sum() }
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = item.first,
                                style = typography.labelMedium,
                                color = colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(4.dp))
                            HorizontalStackedBar(
                                values = item.second,
                                minutesFormat = minutesFormat,
                                hoursMinutesFormat = hoursMinutesFormat,
                                rankList = rankList
                            )
                        }
                        Text(
                            text = remember(sum, hoursMinutesFormat, minutesFormat) {
                                formatDuration(sum)
                            },
                            style = typography.labelLarge,
                            color = colorScheme.onSurface,
                            textAlign = TextAlign.End,
                            modifier = Modifier.width(64.dp)
                        )
                    }
                }

                item(key = "spacer_2") { Spacer(Modifier.height(12.dp)) }

                item(key = "breakdown_header") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(topListItemShape)
                            .background(listItemColors.containerColor)
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                    ) {
                        Text(
                            stringResource(Res.string.focus_breakdown),
                            style = typography.headlineSmall.copy(fontFamily = fonts.topBarTitle)
                        )
                        Text(
                            stringResource(Res.string.focus_breakdown_desc),
                            style = typography.bodySmall,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                }
                item(key = "breakdown_chart") {
                    FocusBreakdownChart(
                        modelProducer = focusBreakdownValues.first,
                        hoursFormat = hoursFormat,
                        hoursMinutesFormat = hoursMinutesFormat,
                        minutesFormat = minutesFormat,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(middleListItemShape)
                            .background(listItemColors.containerColor)
                            .padding(start = 20.dp, end = 20.dp, bottom = 16.dp)
                    )
                }
                item(key = "ratio_card") {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(bottomListItemShape)
                            .background(listItemColors.containerColor)
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                    ) {
                        Text(
                            stringResource(Res.string.focus_break_ratio),
                            style = typography.titleMedium
                        )
                        FocusBreakRatioVisualization(
                            focusDuration = avgFocusList.sum(),
                            breakDuration = focusBreakdownValues.second,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                item(key = "bottom_spacer") { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}