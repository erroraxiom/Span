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

@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.erroraxiom.span.ui.settingsScreen.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.StyleScope
import androidx.compose.foundation.style.StyleStateKey
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.motionScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.SliderState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.rememberSliderState
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import com.materialkolor.ktx.harmonize
import com.erroraxiom.span.data.Topic
import com.erroraxiom.span.data.TopicShape
import com.erroraxiom.span.ui.mergePaddingValues
import com.erroraxiom.span.ui.performSegmentTick
import com.erroraxiom.span.ui.settingsScreen.components.CreateTopicBottomSheet
import com.erroraxiom.span.ui.settingsScreen.components.MinuteInputs
import com.erroraxiom.span.ui.settingsScreen.components.TopicShapeIcon
import com.erroraxiom.span.ui.settingsScreen.components.TopicTimerSettings
import com.erroraxiom.span.ui.settingsScreen.viewModel.SettingsAction
import com.erroraxiom.span.ui.theme.CustomColors.detailPaneTopBarColors
import com.erroraxiom.span.ui.theme.CustomColors.listItemColors
import com.erroraxiom.span.ui.theme.CustomColors.topBarColors
import com.erroraxiom.span.ui.theme.LocalAppFonts
import com.erroraxiom.span.ui.theme.SeededTheme
import com.erroraxiom.span.ui.theme.SpanShapeDefaults.PANE_MAX_WIDTH
import com.erroraxiom.span.ui.theme.SpanShapeDefaults.segmentedListItemShapes
import com.erroraxiom.span.ui.theme.SpanTheme
import com.erroraxiom.span.ui.topBarWindowInsets
import kotlin.math.PI
import kotlin.math.cos
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import span.shared.generated.resources.Res
import span.shared.generated.resources.add
import span.shared.generated.resources.arrow_back
import span.shared.generated.resources.back
import span.shared.generated.resources.create_new_topic
import span.shared.generated.resources.minutes_format
import span.shared.generated.resources.settings
import span.shared.generated.resources.topic_summary_format
import span.shared.generated.resources.topics

val selectedKey = StyleStateKey(false)

var MutableStyleState.selected: Boolean
    get() = this[selectedKey]
    set(value) {
        this[selectedKey] = value
    }

fun StyleScope.selected(block: () -> Unit) {
    state(selectedKey, block) { key, state -> state[key] }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TopicsSettings(
    topics: List<Topic>,
    editingTopic: Topic,
    sessionActive: Boolean,
    currentTopicId: Long,
    minuteInputs: MinuteInputs,
    sessionsSliderState: SliderState,
    contentPadding: PaddingValues,
    onBack: () -> Unit,
    onAction: (SettingsAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val haptic = LocalHapticFeedback.current
    val defaultTopicColor = colorScheme.primary

    var showCreateTopicSheet by remember { mutableStateOf(false) }

    SeededTheme(editingTopic.color) {
        val colorScheme = colorScheme
        val motionScheme = motionScheme
        val topBarTitle = LocalAppFonts.current.topBarTitle
        val boomShape = MaterialShapes.Boom.toShape()

        val unselectedFont = typography.titleLargeEmphasized.copy(
            fontFamily = typography.bodyLarge.fontFamily
        )

        val selectedFont = typography.titleLargeEmphasized.copy(
            fontFamily = topBarTitle
        )

        val widthExpanded = currentWindowAdaptiveInfoV2()
            .windowSizeClass
            .isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND)

        val barColors = if (widthExpanded) detailPaneTopBarColors
        else topBarColors

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(barColors.containerColor)
        ) {
            Scaffold(
                topBar = {
                    LargeFlexibleTopAppBar(
                        windowInsets = topBarWindowInsets(),
                        title = {
                            Text(
                                stringResource(Res.string.topics),
                                fontFamily = topBarTitle
                            )
                        },
                        subtitle = {
                            Text(stringResource(Res.string.settings))
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
                        scrollBehavior = scrollBehavior
                    )
                },
                containerColor = barColors.containerColor,
                modifier = modifier
                    .widthIn(max = PANE_MAX_WIDTH)
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
            ) { innerPadding ->
                val insets = mergePaddingValues(innerPadding, contentPadding)
                val minFormat = stringResource(Res.string.minutes_format)
                val summaryFormat = stringResource(Res.string.topic_summary_format)
                val scrollState = rememberScrollState()
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(insets)
                ) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(40.dp))
                            .background(colorScheme.primary)
                            .clickable { showCreateTopicSheet = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(colorScheme.onPrimary, CircleShape)
                            ) {
                                Box(
                                    Modifier
                                        .size(22.dp)
                                        .background(colorScheme.onPrimaryContainer, boomShape)
                                )
                            }
                            Text(
                                stringResource(Res.string.create_new_topic),
                                style = typography.bodyLargeEmphasized,
                                color = colorScheme.onPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            FilledIconButton(
                                onClick = { showCreateTopicSheet = true },
                                shapes = IconButtonDefaults.shapes(),
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = colorScheme.primaryContainer
                                ),
                                modifier = Modifier.size(
                                    IconButtonDefaults.smallContainerSize(
                                        IconButtonDefaults.IconButtonWidthOption.Wide
                                    )
                                )
                            ) { Icon(painterResource(Res.drawable.add), null) }
                        }
                    }
                    val dividerColor = colorScheme.primary
                    val wavePath = remember { Path() }
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 36.dp, vertical = 20.dp)
                            .height(12.dp)
                    ) {
                        val wavelengthPx = 20.dp.toPx()
                        val amplitudePx = 2.dp.toPx()
                        val strokeWidthPx = 4.dp.toPx()
                        val centerY = size.height / 2f

                        wavePath.reset()
                        wavePath.moveTo(0f, centerY + amplitudePx)
                        var x = 0f
                        while (x < size.width) {
                            x += 4f
                            val y = centerY +
                                amplitudePx * cos(2 * PI * x / wavelengthPx).toFloat()
                            wavePath.lineTo(x, y)
                        }

                        drawPath(
                            path = wavePath,
                            color = dividerColor,
                            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                        )
                    }

                    topics.fastForEachIndexed { index, topic ->
                        key(topic.id) {
                            val selected = topic.id == editingTopic.id
                            val shape = topic.shape.toShape()

                            val topicColor =
                                if (topic.color == Color.White) defaultTopicColor
                                else topic.color

                            val primary = remember(topicColor, colorScheme.primary) {
                                topicColor.harmonize(colorScheme.primary, true)
                            }
                            val onPrimary = remember(topicColor, colorScheme.onPrimary) {
                                topicColor.harmonize(colorScheme.onPrimary, true)
                            }
                            val primaryContainer =
                                remember(topicColor, colorScheme.primaryContainer) {
                                    topicColor.harmonize(colorScheme.primaryContainer, true)
                                }
                            val onPrimaryContainer =
                                remember(topicColor, colorScheme.onPrimaryContainer) {
                                    topicColor.harmonize(colorScheme.onPrimaryContainer, true)
                                }
                            val surfaceBright =
                                remember(topicColor, colorScheme.surfaceBright) {
                                    topicColor.harmonize(colorScheme.surfaceBright, true)
                                }

                            val titleFontFamily: TextStyle =
                                if (selected) selectedFont else unselectedFont

                            val summaryText = remember(
                                summaryFormat,
                                minFormat,
                                topic.focusTime,
                                topic.shortBreakTime,
                                topic.longBreakTime,
                                topic.sessionLength
                            ) {
                                String.format(
                                    summaryFormat,
                                    String.format(minFormat, topic.focusTime / 60000),
                                    String.format(minFormat, topic.shortBreakTime / 60000),
                                    String.format(minFormat, topic.longBreakTime / 60000),
                                    topic.sessionLength
                                )
                            }

                            SegmentedListItem(
                                checked = selected,
                                onCheckedChange = {
                                    if (!selected) haptic.performSegmentTick()
                                    onAction(
                                        SettingsAction.SetEditingTopic(
                                            topic
                                        )
                                    )
                                },
                                shapes = segmentedListItemShapes(
                                    index,
                                    topics.size
                                ),
                                colors = listItemColors.copy(
                                    containerColor = surfaceBright,
                                    selectedContainerColor = primaryContainer
                                ),
                                verticalAlignment = Alignment.CenterVertically,
                                leadingContent = {
                                    TopicShapeIcon(
                                        shape = shape,
                                        containerColor = primaryContainer,
                                        shapeColor = primary,
                                        selectedContainerColor = primary,
                                        selectedShapeColor = onPrimary,
                                        selected = selected,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                },
                                supportingContent = {
                                    Text(
                                        summaryText,
                                        style = typography.labelLarge,
                                        color = colorScheme.onSecondaryContainer
                                    )
                                },
                                modifier = Modifier
                                    .padding(
                                        start = 16.dp,
                                        end = 16.dp,
                                        top = if (selected) 2.dp else 0.dp
                                    )
                            ) {
                                Text(
                                    topic.name,
                                    style = titleFontFamily,
                                    color = if (!selected) colorScheme.onSurface else onPrimaryContainer
                                )
                            }

                            AnimatedVisibility(
                                selected,
                                enter = expandVertically(motionScheme.slowSpatialSpec()),
                                exit = shrinkVertically(motionScheme.slowSpatialSpec())
                            ) {
                                TopicTimerSettings(
                                    topic = topic,
                                    topics = topics,
                                    topicRunning = sessionActive && topic.id == currentTopicId,
                                    minuteInputs = minuteInputs,
                                    sessionsSliderState = sessionsSliderState,
                                    onAction = onAction,
                                    modifier = Modifier.padding(
                                        start = 16.dp,
                                        top = 2.dp,
                                        end = 16.dp,
                                        bottom = 2.dp
                                    ),
                                    inTimerScreen = false
                                )
                            }
                        }
                    }
                }
            }
            if (showCreateTopicSheet) {
                CreateTopicBottomSheet(
                    topics = topics,
                    setShowSheet = { showCreateTopicSheet = it },
                    onAction = onAction
                )
            }
        }
    }
}

@Preview
@Composable
fun TopicsSettingsPreview() {
    var topics by remember { mutableStateOf(sampleTopics) }
    var editingTopic by remember {
        mutableStateOf(sampleTopics[5])
    }
    SpanTheme(dynamicColor = false) {
        TopicsSettings(
            topics = topics,
            editingTopic = editingTopic,
            sessionActive = false,
            currentTopicId = editingTopic.id,
            minuteInputs = MinuteInputs("25", "5", "15"),
            sessionsSliderState = rememberSliderState(4f, valueRange = 1f..10f),
            contentPadding = PaddingValues(0.dp),
            onBack = {},
            onAction = { action ->
                when (action) {
                    is SettingsAction.SetEditingTopic -> editingTopic = action.topic
                    is SettingsAction.CreateTopic -> {
                        val topic = action.topic.copy(id = topics.maxOf { it.id } + 1)
                        topics = (topics + topic).sortedBy { it.name }
                        editingTopic = topic
                    }

                    else -> Unit
                }
            }
        )
    }
}

@Preview
@Composable
fun TopicsSettingsDarkPreview() {
    var topics by remember { mutableStateOf(sampleTopics) }
    var editingTopic by remember {
        mutableStateOf(sampleTopics[5])
    }
    SpanTheme(darkTheme = true, dynamicColor = false) {
        TopicsSettings(
            topics = topics,
            editingTopic = editingTopic,
            sessionActive = false,
            currentTopicId = editingTopic.id,
            minuteInputs = MinuteInputs("25", "5", "15"),
            sessionsSliderState = rememberSliderState(4f, valueRange = 1f..10f),
            contentPadding = PaddingValues(0.dp),
            onBack = {},
            onAction = { action ->
                when (action) {
                    is SettingsAction.SetEditingTopic -> editingTopic = action.topic
                    is SettingsAction.CreateTopic -> {
                        val topic = action.topic.copy(id = topics.maxOf { it.id } + 1)
                        topics = (topics + topic).sortedBy { it.name }
                        editingTopic = topic
                    }

                    else -> Unit
                }
            }
        )
    }
}

val sampleTopics = listOf(
    Topic(
        id = 1,
        name = "Default",
        color = Color.White,
        shape = TopicShape.COOKIE_12_SIDED,
        focusTime = 25 * 60000L,
        shortBreakTime = 5 * 60000L,
        longBreakTime = 15 * 60000L,
        sessionLength = 4,
        autostartNextSession = false,
        dndEnabled = false
    ),
    Topic(
        id = 2,
        name = "Work",
        color = Color(0xFF2196F3),
        shape = TopicShape.SQUARE,
        focusTime = 50 * 60000L,
        shortBreakTime = 10 * 60000L,
        longBreakTime = 30 * 60000L,
        sessionLength = 3,
        autostartNextSession = true,
        dndEnabled = true
    ),
    Topic(
        id = 3,
        name = "Study",
        color = Color(0xFF4CAF50),
        shape = TopicShape.TRIANGLE,
        focusTime = 45 * 60000L,
        shortBreakTime = 5 * 60000L,
        longBreakTime = 20 * 60000L,
        sessionLength = 4,
        autostartNextSession = false,
        dndEnabled = true
    ),
    Topic(
        id = 4,
        name = "Fitness",
        color = Color(0xFFF44336),
        shape = TopicShape.CIRCLE,
        focusTime = 30 * 60000L,
        shortBreakTime = 2 * 60000L,
        longBreakTime = 10 * 60000L,
        sessionLength = 6,
        autostartNextSession = true,
        dndEnabled = false
    ),
    Topic(
        id = 5,
        name = "Coding",
        color = Color(0xFF9C27B0),
        shape = TopicShape.DIAMOND,
        focusTime = 60 * 60000L,
        shortBreakTime = 10 * 60000L,
        longBreakTime = 40 * 60000L,
        sessionLength = 2,
        autostartNextSession = false,
        dndEnabled = true
    ),
    Topic(
        id = 6,
        name = "Reading",
        color = Color(0xFFFF9800),
        shape = TopicShape.PENTAGON,
        focusTime = 20 * 60000L,
        shortBreakTime = 3 * 60000L,
        longBreakTime = 15 * 60000L,
        sessionLength = 5,
        autostartNextSession = true,
        dndEnabled = false
    ),
    Topic(
        id = 7,
        name = "Meditation",
        color = Color(0xFF00BCD4),
        shape = TopicShape.SUNNY,
        focusTime = 15 * 60000L,
        shortBreakTime = 0,
        longBreakTime = 0,
        sessionLength = 1,
        autostartNextSession = false,
        dndEnabled = true
    ),
    Topic(
        id = 8,
        name = "Gaming",
        color = Color(0xFF795548),
        shape = TopicShape.BOOM,
        focusTime = 120 * 60000L,
        shortBreakTime = 15 * 60000L,
        longBreakTime = 60 * 60000L,
        sessionLength = 2,
        autostartNextSession = false,
        dndEnabled = false
    ),
    Topic(
        id = 9,
        name = "Chores",
        color = Color(0xFF607D8B),
        shape = TopicShape.FLOWER,
        focusTime = 10 * 60000L,
        shortBreakTime = 2 * 60000L,
        longBreakTime = 5 * 60000L,
        sessionLength = 10,
        autostartNextSession = true,
        dndEnabled = false
    ),
    Topic(
        id = 10,
        name = "Music",
        color = Color(0xFFFFE082),
        shape = TopicShape.HEART,
        focusTime = 35 * 60000L,
        shortBreakTime = 5 * 60000L,
        longBreakTime = 15 * 60000L,
        sessionLength = 4,
        autostartNextSession = false,
        dndEnabled = false
    ),
    Topic(
        id = 11,
        name = "Travel",
        color = Color(0xFF8BC34A),
        shape = TopicShape.PILL,
        focusTime = 40 * 60000L,
        shortBreakTime = 8 * 60000L,
        longBreakTime = 25 * 60000L,
        sessionLength = 3,
        autostartNextSession = true,
        dndEnabled = true
    )
)
