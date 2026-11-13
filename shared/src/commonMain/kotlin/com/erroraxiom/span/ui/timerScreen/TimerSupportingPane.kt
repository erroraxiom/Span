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

package com.erroraxiom.span.ui.timerScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.erroraxiom.span.data.Topic
import com.erroraxiom.span.ui.theme.CustomColors.detailPaneTopBarColors
import com.erroraxiom.span.ui.theme.CustomColors.listItemColors
import com.erroraxiom.span.ui.theme.LocalAppFonts
import com.erroraxiom.span.ui.theme.SpanShapeDefaults.segmentedListItemShapes
import com.erroraxiom.span.ui.theme.SpanTheme
import com.erroraxiom.span.ui.timerScreen.viewModel.TimerMode
import com.erroraxiom.span.ui.timerScreen.viewModel.TimerState
import com.erroraxiom.span.utils.millisecondsToStr
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import span.shared.generated.resources.Res
import span.shared.generated.resources.check_circle_40dp
import span.shared.generated.resources.focus
import span.shared.generated.resources.in_progress_40dp
import span.shared.generated.resources.long_break
import span.shared.generated.resources.not_started_40dp
import span.shared.generated.resources.short_break
import span.shared.generated.resources.up_next

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TimerSupportingPane(
    timerState: TimerState,
    currentTopic: Topic,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val isFocus = timerState.timerMode == TimerMode.FOCUS
    LazyColumn(
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .background(detailPaneTopBarColors.containerColor)
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        item {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.up_next),
                        fontFamily = LocalAppFonts.current.topBarTitle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                windowInsets = WindowInsets(),
                colors = detailPaneTopBarColors
            )
        }
        items(timerState.totalFocusCount) {
            val currentSession = it + 1 == timerState.currentFocusCount
            CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    SegmentedListItem(
                        onClick = {},
                        enabled = it + 1 >= timerState.currentFocusCount && !(currentSession && !isFocus),
                        colors = listItemColors,
                        shapes = segmentedListItemShapes(0, 2),
                        leadingContent = {
                            Icon(
                                painter = painterResource(
                                    when {
                                        currentSession && isFocus -> Res.drawable.in_progress_40dp
                                        it + 1 < timerState.currentFocusCount || (currentSession && !isFocus) -> Res.drawable.check_circle_40dp
                                        else -> Res.drawable.not_started_40dp
                                    }
                                ),
                                contentDescription = null,
                                tint = colorScheme.primary
                            )
                        },
                        supportingContent = { Text(stringResource(Res.string.focus)) },
                        content = { Text(millisecondsToStr(currentTopic.focusTime)) }
                    )

                    SegmentedListItem(
                        onClick = {},
                        enabled = it + 1 >= timerState.currentFocusCount,
                        colors = listItemColors,
                        shapes = segmentedListItemShapes(1, 2),
                        leadingContent = {
                            Icon(
                                painter = painterResource(
                                    when {
                                        currentSession && !isFocus -> Res.drawable.in_progress_40dp
                                        it + 1 < timerState.currentFocusCount -> Res.drawable.check_circle_40dp
                                        else -> Res.drawable.not_started_40dp
                                    }
                                ),
                                contentDescription = null,
                                tint = colorScheme.tertiary
                            )
                        },
                        supportingContent = {
                            Text(
                                if (it != timerState.totalFocusCount - 1) stringResource(Res.string.short_break)
                                else stringResource(Res.string.long_break)
                            )
                        },
                        content = {
                            Text(
                                if (it != timerState.totalFocusCount - 1) millisecondsToStr(
                                    currentTopic.shortBreakTime
                                )
                                else millisecondsToStr(currentTopic.longBreakTime)
                            )
                        }
                    )
                }
            }
        }
    }
}

@Preview(
    showSystemUi = true,
    device = Devices.PIXEL_9_PRO
)
@Composable
fun TimerSupportingPanePreview() {
    val timerState = TimerState(
        timeStr = "03:34",
        nextTimeStr = "5:00",
        timerMode = TimerMode.FOCUS,
        timerRunning = true,
        totalFocusCount = 4,
        currentFocusCount = 2
    )
    SpanTheme {
        Surface {
            TimerSupportingPane(
                timerState,
                Topic.defaultTopic,
                contentPadding = PaddingValues()
            )
        }
    }
}
