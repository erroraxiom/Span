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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.common.data.ExtraStore
import com.erroraxiom.span.data.Stat
import com.erroraxiom.span.data.StatTime
import com.erroraxiom.span.ui.Screen
import com.erroraxiom.span.ui.mergePaddingValues
import com.erroraxiom.span.ui.statsScreen.components.TimeColumnChart
import com.erroraxiom.span.ui.theme.CustomColors.listItemColors
import com.erroraxiom.span.ui.theme.CustomColors.topBarColors
import com.erroraxiom.span.ui.theme.LocalAppFonts
import com.erroraxiom.span.ui.theme.SpanShapeDefaults.PANE_MAX_WIDTH
import com.erroraxiom.span.ui.theme.SpanShapeDefaults.bottomListItemShape
import com.erroraxiom.span.ui.theme.SpanShapeDefaults.cardShape
import com.erroraxiom.span.ui.theme.SpanShapeDefaults.topListItemShape
import com.erroraxiom.span.ui.topBarWindowInsets
import com.erroraxiom.span.utils.millisecondsToHoursMinutes
import com.erroraxiom.span.utils.millisecondsToMinutes
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import span.shared.generated.resources.Res
import span.shared.generated.resources.break_
import span.shared.generated.resources.focus
import span.shared.generated.resources.focus_per_day_avg
import span.shared.generated.resources.last_month
import span.shared.generated.resources.last_week
import span.shared.generated.resources.lifetime
import span.shared.generated.resources.monitoring
import span.shared.generated.resources.stats
import span.shared.generated.resources.today
import span.shared.generated.resources.total

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun StatsMainScreen(
    contentPadding: PaddingValues,
    goal: Long,
    lastWeekSummaryChartData: Pair<CartesianChartModelProducer, ExtraStore.Key<List<String>>>,
    lastMonthSummaryChartData: Pair<CartesianChartModelProducer, ExtraStore.Key<List<String>>>,
    todayStat: Stat?,
    allTimeTotalFocus: Long?,
    lastWeekAverageFocusTimes: StatTime?,
    lastMonthAverageFocusTimes: StatTime?,
    generateSampleData: () -> Unit,
    hoursFormat: String,
    hoursMinutesFormat: String,
    minutesFormat: String,
    onNavigate: (Screen.Stats) -> Unit,
    modifier: Modifier = Modifier
) {
    val fonts = LocalAppFonts.current
    val density = LocalDensity.current
    val wavyStroke = remember(density) {
        Stroke(width = with(density) { 8.dp.toPx() }, cap = StrokeCap.Round)
    }
    val cookieShape = MaterialShapes.Cookie9Sided.toShape()

    val lastWeekAverageFocusTime = remember(lastWeekAverageFocusTimes) {
        if (lastWeekAverageFocusTimes != null) {
            lastWeekAverageFocusTimes.focusTimeQ1 +
                lastWeekAverageFocusTimes.focusTimeQ2 +
                lastWeekAverageFocusTimes.focusTimeQ3 +
                lastWeekAverageFocusTimes.focusTimeQ4
        } else 0L
    }
    val lastMonthAverageFocusTime = remember(lastMonthAverageFocusTimes) {
        if (lastMonthAverageFocusTimes != null) {
            lastMonthAverageFocusTimes.focusTimeQ1 +
                lastMonthAverageFocusTimes.focusTimeQ2 +
                lastMonthAverageFocusTimes.focusTimeQ3 +
                lastMonthAverageFocusTimes.focusTimeQ4
        } else 0L
    }

    fun formatDuration(millis: Long): String {
        return if (millis >= 60 * 60 * 1000L) {
            millisecondsToHoursMinutes(millis, hoursMinutesFormat)
        } else {
            millisecondsToMinutes(millis, minutesFormat)
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(topBarColors.containerColor)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(Res.string.stats),
                            style = TextStyle(
                                fontFamily = fonts.topBarTitle,
                                fontSize = 32.sp,
                                lineHeight = 32.sp
                            )
                        )
                    },
                    windowInsets = topBarWindowInsets(),
                    colors = topBarColors
                )
            },
            containerColor = topBarColors.containerColor,
            modifier = Modifier
                .widthIn(max = PANE_MAX_WIDTH)
                .fillMaxSize()
        ) { innerPadding ->
            val insets = mergePaddingValues(innerPadding, contentPadding)
            LazyColumn(
                contentPadding = insets,
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                item(key = "top_spacer") { Spacer(Modifier.height(8.dp)) }

                // 1. TODAY SECTION (Material 3 Expressive Tonal Cards)
                item(key = "today_card") {
                    Card(
                        shape = cardShape,
                        colors = CardDefaults.cardColors(containerColor = listItemColors.containerColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                text = stringResource(Res.string.today),
                                style = typography.headlineSmall.copy(fontFamily = fonts.topBarTitle),
                                color = colorScheme.onSurface
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Surface(
                                    shape = cardShape,
                                    color = colorScheme.primaryContainer,
                                    contentColor = colorScheme.onPrimaryContainer,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Text(
                                            text = remember(todayStat, hoursMinutesFormat, minutesFormat) {
                                                formatDuration(todayStat?.totalFocusTime() ?: 0L)
                                            },
                                            style = typography.headlineMedium.copy(
                                                fontFamily = fonts.topBarTitle,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            maxLines = 1
                                        )
                                        Text(
                                            text = stringResource(Res.string.focus),
                                            style = typography.labelLarge,
                                            color = colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                        )
                                    }
                                }

                                Surface(
                                    shape = cardShape,
                                    color = colorScheme.tertiaryContainer,
                                    contentColor = colorScheme.onTertiaryContainer,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Text(
                                            text = remember(todayStat, hoursMinutesFormat, minutesFormat) {
                                                formatDuration(todayStat?.breakTime ?: 0L)
                                            },
                                            style = typography.headlineMedium.copy(
                                                fontFamily = fonts.topBarTitle,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            maxLines = 1
                                        )
                                        Text(
                                            text = stringResource(Res.string.break_),
                                            style = typography.labelLarge,
                                            color = colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                            }

                            if (goal > 0 && todayStat != null) {
                                val progress =
                                    (todayStat.totalFocusTime().toFloat() / goal).coerceAtMost(1f)
                                LinearWavyProgressIndicator(
                                    progress = { progress },
                                    color = colorScheme.primary,
                                    trackColor = colorScheme.secondaryContainer,
                                    stroke = wavyStroke,
                                    trackStroke = wavyStroke,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                item(key = "spacer_1") { Spacer(Modifier.height(12.dp)) }

                // 2. THIS WEEK CARD (Top Segmented Item)
                item(key = "last_week_card") {
                    Box(
                        modifier = Modifier
                            .clip(topListItemShape)
                            .background(listItemColors.containerColor)
                            .clickable { onNavigate(Screen.Stats.LastWeek) }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    top = 20.dp,
                                    start = 20.dp,
                                    end = 20.dp,
                                    bottom = 16.dp
                                )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    stringResource(Res.string.last_week),
                                    style = typography.headlineSmall.copy(fontFamily = fonts.topBarTitle),
                                    color = listItemColors.contentColor
                                )

                                Surface(
                                    shape = CircleShape,
                                    color = colorScheme.primaryContainer,
                                    contentColor = colorScheme.onPrimaryContainer
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.End,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = remember(lastWeekAverageFocusTime, hoursMinutesFormat, minutesFormat) {
                                                formatDuration(lastWeekAverageFocusTime)
                                            },
                                            style = typography.titleLarge.copy(
                                                fontFamily = fonts.topBarTitle,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        Text(
                                            text = stringResource(Res.string.focus_per_day_avg),
                                            style = typography.labelSmall
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(14.dp))

                            TimeColumnChart(
                                modelProducer = lastWeekSummaryChartData.first,
                                goal = goal,
                                zoomEnabled = false,
                                hoursFormat = hoursFormat,
                                hoursMinutesFormat = hoursMinutesFormat,
                                minutesFormat = minutesFormat,
                                xValueFormatter = remember(lastWeekSummaryChartData.second) {
                                    CartesianValueFormatter { context, x, _ ->
                                        context.model.extraStore.getOrNull(lastWeekSummaryChartData.second)
                                            ?.getOrNull(x.toInt()) ?: ""
                                    }
                                },
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }

                // 3. THIS MONTH CARD (Bottom Segmented Item — This Year is removed)
                item(key = "last_month_card") {
                    Box(
                        modifier = Modifier
                            .clip(bottomListItemShape)
                            .background(listItemColors.containerColor)
                            .clickable { onNavigate(Screen.Stats.LastMonth) }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    top = 20.dp,
                                    start = 20.dp,
                                    end = 20.dp,
                                    bottom = 16.dp
                                )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    stringResource(Res.string.last_month),
                                    style = typography.headlineSmall.copy(fontFamily = fonts.topBarTitle),
                                    color = listItemColors.contentColor
                                )

                                Surface(
                                    shape = CircleShape,
                                    color = colorScheme.secondaryContainer,
                                    contentColor = colorScheme.onSecondaryContainer
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.End,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = remember(lastMonthAverageFocusTime, hoursMinutesFormat, minutesFormat) {
                                                formatDuration(lastMonthAverageFocusTime)
                                            },
                                            style = typography.titleLarge.copy(
                                                fontFamily = fonts.topBarTitle,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        Text(
                                            text = stringResource(Res.string.focus_per_day_avg),
                                            style = typography.labelSmall
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(14.dp))

                            TimeColumnChart(
                                modelProducer = lastMonthSummaryChartData.first,
                                goal = goal,
                                zoomEnabled = false,
                                thickness = 8.dp,
                                hoursFormat = hoursFormat,
                                hoursMinutesFormat = hoursMinutesFormat,
                                minutesFormat = minutesFormat,
                                xValueFormatter = remember(lastMonthSummaryChartData.second) {
                                    CartesianValueFormatter { context, x, _ ->
                                        context.model.extraStore.getOrNull(lastMonthSummaryChartData.second)
                                            ?.getOrNull(x.toInt()) ?: ""
                                    }
                                },
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }

                item(key = "spacer_2") { Spacer(Modifier.height(12.dp)) }

                // 4. LIFETIME CARD
                item(key = "lifetime_card") {
                    Card(
                        shape = cardShape,
                        colors = CardDefaults.cardColors(containerColor = listItemColors.containerColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(52.dp)
                                        .background(
                                            colorScheme.tertiaryContainer,
                                            cookieShape
                                        )
                                ) {
                                    Icon(
                                        painter = painterResource(Res.drawable.monitoring),
                                        contentDescription = null,
                                        tint = colorScheme.onTertiaryContainer,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = stringResource(Res.string.lifetime),
                                        style = typography.headlineSmall.copy(fontFamily = fonts.topBarTitle),
                                        color = colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${stringResource(Res.string.total)} ${
                                            stringResource(Res.string.focus).lowercase()
                                        }",
                                        style = typography.bodyMedium,
                                        color = colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Surface(
                                shape = CircleShape,
                                color = colorScheme.primaryContainer,
                                contentColor = colorScheme.onPrimaryContainer
                            ) {
                                Text(
                                    text = remember(allTimeTotalFocus, hoursMinutesFormat, minutesFormat) {
                                        formatDuration(allTimeTotalFocus ?: 0L)
                                    },
                                    style = typography.headlineSmall.copy(
                                        fontFamily = fonts.topBarTitle,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
                                )
                            }
                        }
                    }
                }

                item(key = "bottom_spacer") { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}