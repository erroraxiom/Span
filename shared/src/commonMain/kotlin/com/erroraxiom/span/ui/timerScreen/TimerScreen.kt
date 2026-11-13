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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.Surface
import androidx.compose.material3.VerticalDragHandle
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.AdaptStrategy
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldDefaults
import androidx.compose.material3.adaptive.layout.rememberPaneExpansionState
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import com.erroraxiom.span.data.Topic
import com.erroraxiom.span.ui.androidSystemGestureExclusion
import com.erroraxiom.span.ui.settingsScreen.screens.sampleTopics
import com.erroraxiom.span.ui.theme.SeededTheme
import com.erroraxiom.span.ui.theme.SpanTheme
import com.erroraxiom.span.ui.timerScreen.viewModel.TimerAction
import com.erroraxiom.span.ui.timerScreen.viewModel.TimerMode
import com.erroraxiom.span.ui.timerScreen.viewModel.TimerState

import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun TimerScreen(
    timerState: TimerState,
    topics: List<Topic>,
    currentTopic: Topic,
    contentPadding: PaddingValues,
    progressFlow: StateFlow<Float>,
    onAction: (TimerAction) -> Unit,
    onAddTopic: () -> Unit,
    onOpenTopicSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val widthExpanded = currentWindowAdaptiveInfoV2()
        .windowSizeClass
        .isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND)

    SeededTheme(
        seedColor = if (currentTopic.id == Topic.DEFAULT_TOPIC_ID) {
            androidx.compose.ui.graphics.Color.White
        } else {
            currentTopic.color
        }
    ) {
        if (!widthExpanded) {
            TimerMainPane(
                timerState = timerState,
                currentTopic = currentTopic,
                topics = topics,
                contentPadding = contentPadding,
                progressFlow = progressFlow,
                onAction = onAction,
                onAddTopic = onAddTopic,
                onOpenTopicSettings = onOpenTopicSettings,
                modifier = modifier
            )
        } else {
            val navigator = rememberSupportingPaneScaffoldNavigator(
                adaptStrategies = SupportingPaneScaffoldDefaults.adaptStrategies(supportingPaneAdaptStrategy = AdaptStrategy.Hide)
            )
            val expansionState = rememberPaneExpansionState()

            SupportingPaneScaffold(
                directive = navigator.scaffoldDirective,
                scaffoldState = navigator.scaffoldState,
                mainPane = {
                    AnimatedPane {
                        TimerMainPane(
                            timerState = timerState,
                            currentTopic = currentTopic,
                            topics = topics,
                            contentPadding = contentPadding,
                            progressFlow = progressFlow,
                            onAction = onAction,
                            onAddTopic = onAddTopic,
                            onOpenTopicSettings = onOpenTopicSettings,
                            modifier = modifier
                        )
                    }
                },
                supportingPane = {
                    AnimatedPane {
                        TimerSupportingPane(
                            timerState = timerState,
                            currentTopic = currentTopic,
                            contentPadding = contentPadding
                        )
                    }
                },
                paneExpansionDragHandle = {
                    val interactionSource =
                        remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                    VerticalDragHandle(
                        modifier = Modifier
                            .paneExpansionDraggable(
                                expansionState,
                                LocalMinimumInteractiveComponentSize.current,
                                interactionSource
                            )
                            .androidSystemGestureExclusion()
                    )
                },
                paneExpansionState = expansionState
            )
        }
    }
}

@Preview(
    showSystemUi = true,
    device = Devices.PIXEL_9_PRO
)
@Composable
fun TimerScreenPreview() {
    val timerState = TimerState(
        timeStr = "03:34", nextTimeStr = "5:00", timerMode = TimerMode.FOCUS, timerRunning = true
    )
    SpanTheme {
        Surface {
            TimerScreen(
                timerState = timerState,
                topics = sampleTopics,
                currentTopic = sampleTopics[5],
                contentPadding = PaddingValues(),
                progressFlow = remember { kotlinx.coroutines.flow.MutableStateFlow(0.3f) },
                onAction = {},
                onAddTopic = {},
                onOpenTopicSettings = {}
            )
        }
    }
}
