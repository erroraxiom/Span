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

package com.erroraxiom.span.ui.settingsScreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.SliderState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.erroraxiom.span.data.Topic
import com.erroraxiom.span.data.TopicShape
import com.erroraxiom.span.ui.performToggle
import com.erroraxiom.span.ui.rememberRequestDndPermissionCallback
import com.erroraxiom.span.ui.settingsScreen.viewModel.SettingsAction
import com.erroraxiom.span.ui.theme.CustomColors.listItemColors
import com.erroraxiom.span.ui.theme.CustomColors.switchColors
import com.erroraxiom.span.ui.theme.SpanShapeDefaults.bottomListItemShape
import com.erroraxiom.span.ui.theme.SpanShapeDefaults.cardShape
import com.erroraxiom.span.ui.theme.SpanShapeDefaults.middleListItemShape
import com.erroraxiom.span.ui.theme.SpanShapeDefaults.segmentedListItemShapes
import com.erroraxiom.span.ui.theme.SpanShapeDefaults.topListItemShape
import com.erroraxiom.span.ui.theme.SpanTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import span.shared.generated.resources.Res
import span.shared.generated.resources.auto_start_next_timer
import span.shared.generated.resources.auto_start_next_timer_desc
import span.shared.generated.resources.autoplay
import span.shared.generated.resources.check
import span.shared.generated.resources.clear
import span.shared.generated.resources.clocks
import span.shared.generated.resources.delete
import span.shared.generated.resources.delete_topic
import span.shared.generated.resources.dnd
import span.shared.generated.resources.dnd_desc
import span.shared.generated.resources.edit_topic_desc
import span.shared.generated.resources.focus
import span.shared.generated.resources.long_break
import span.shared.generated.resources.session_length
import span.shared.generated.resources.session_length_desc
import span.shared.generated.resources.short_break

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TopicTimerSettings(
    topic: Topic,
    topics: List<Topic>,
    topicRunning: Boolean,
    minuteInputs: MinuteInputs,
    sessionsSliderState: SliderState,
    onAction: (SettingsAction) -> Unit,
    modifier: Modifier = Modifier,
    inTimerScreen: Boolean = false,
    onDisabledClick: () -> Unit = {}
) {
    var showEditSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showEditSheet) {
        TopicShapeColorBottomSheet(
            topic = topic,
            topics = topics,
            setShowSheet = { showEditSheet = it },
            onAction = onAction
        )
    }

    if (showDeleteDialog) {
        val defaultTopicName = topics.find { it.id == Topic.DEFAULT_TOPIC_ID }?.name
            ?: Topic.defaultTopic.name
        DeleteTopicDialog(
            defaultTopicName = defaultTopicName,
            onDismiss = { showDeleteDialog = false },
            onDeleteTopic = {
                showDeleteDialog = false
                onAction(SettingsAction.DeleteTopic(topic, deleteStats = false))
            },
            onDeleteTopicAndStats = {
                showDeleteDialog = false
                onAction(SettingsAction.DeleteTopic(topic, deleteStats = true))
            }
        )
    }

    val requestDndPermission = rememberRequestDndPermissionCallback()

    TopicTimerProperties(
        topicId = topic.id,
        topicName = topic.name,
        topicColor = topic.color,
        topicShape = topic.shape,
        autostartNextSession = topic.autostartNextSession,
        dndEnabled = topic.dndEnabled,
        topicRunning = topicRunning,
        inTimerScreen = inTimerScreen,
        minuteInputs = minuteInputs,
        onMinuteInputsChange = { onAction(SettingsAction.SetEditingTopicMinutes(it)) },
        sessionsSliderState = sessionsSliderState,
        onAutostartNextSessionChange = { onAction(SettingsAction.SaveAutostartNextSession(it)) },
        onDndEnabledChange = {
            requestDndPermission(it)
            onAction(SettingsAction.SaveDndEnabled(it))
        },
        showEditButtons = !inTimerScreen,
        onEditClick = { showEditSheet = true },
        onDeleteClick = { showDeleteDialog = true },
        onDisabledClick = onDisabledClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TopicTimerProperties(
    topicId: Long,
    autostartNextSession: Boolean,
    dndEnabled: Boolean,
    topicRunning: Boolean,
    inTimerScreen: Boolean,
    minuteInputs: MinuteInputs,
    onMinuteInputsChange: (MinuteInputs) -> Unit,
    sessionsSliderState: SliderState,
    onAutostartNextSessionChange: (Boolean) -> Unit,
    onDndEnabledChange: (Boolean) -> Unit,
    showEditButtons: Boolean,
    modifier: Modifier = Modifier,
    topicName: String = "",
    topicColor: Color = Color.White,
    topicShape: TopicShape = TopicShape.CIRCLE,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onDisabledClick: () -> Unit = {}
) {
    val haptic = LocalHapticFeedback.current
    val sessionsFormat = stringResource(Res.string.session_length_desc)

    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = modifier
    ) {
        if (showEditButtons && !inTimerScreen) {
            val shape = topicShape.toShape()
            TopicShapeColorPickerButton(
                topicName = topicName,
                color = topicColor,
                shape = shape,
                enabled = topicId != Topic.DEFAULT_TOPIC_ID,
                onClick = onEditClick
            )
            Spacer(Modifier.height(12.dp))
        }

        // Minute Inputs Row (Focus / Short Break / Long Break)
        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        MinuteInputField(
                            value = minuteInputs.focus,
                            onValueChange = {
                                onMinuteInputsChange(minuteInputs.copy(focus = it))
                            },
                            enabled = !topicRunning,
                            shape = topListItemShape.copy(
                                topEnd = shapes.extraSmall.topEnd,
                                bottomEnd = shapes.extraSmall.bottomEnd
                            )
                        )
                        Text(
                            stringResource(Res.string.focus),
                            style = typography.titleSmall,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        MinuteInputField(
                            value = minuteInputs.shortBreak,
                            onValueChange = {
                                onMinuteInputsChange(minuteInputs.copy(shortBreak = it))
                            },
                            enabled = !topicRunning,
                            shape = middleListItemShape
                        )
                        Text(
                            stringResource(Res.string.short_break),
                            style = typography.titleSmall,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        MinuteInputField(
                            value = minuteInputs.longBreak,
                            onValueChange = {
                                onMinuteInputsChange(minuteInputs.copy(longBreak = it))
                            },
                            enabled = !topicRunning,
                            imeAction = ImeAction.Done,
                            shape = topListItemShape.copy(
                                topStart = shapes.extraSmall.topStart,
                                bottomStart = shapes.extraSmall.bottomStart
                            )
                        )
                        Text(
                            stringResource(Res.string.long_break),
                            style = typography.titleSmall,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (topicRunning) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable(
                                indication = null,
                                interactionSource = null,
                                onClick = onDisabledClick
                            )
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Session Length Slider
        Box {
            SliderListItem(
                state = sessionsSliderState,
                enabled = !topicRunning,
                label = stringResource(Res.string.session_length),
                trailingLabel = { String.format(sessionsFormat, it.toInt()) },
                trailingIcon = { Icon(painterResource(Res.drawable.clocks), null) },
                shape = cardShape
            )
            if (topicRunning) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable(
                            indication = null,
                            interactionSource = null,
                            onClick = onDisabledClick
                        )
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // Autostart Next Session & Do Not Disturb
        ListItem(
            leadingContent = {
                Icon(
                    painterResource(Res.drawable.autoplay),
                    contentDescription = null,
                    modifier = Modifier.padding(top = 4.dp)
                )
            },
            headlineContent = { Text(stringResource(Res.string.auto_start_next_timer)) },
            supportingContent = { Text(stringResource(Res.string.auto_start_next_timer_desc)) },
            trailingContent = {
                Switch(
                    checked = autostartNextSession,
                    onCheckedChange = {
                        haptic.performToggle(it)
                        onAutostartNextSessionChange(it)
                    },
                    thumbContent = {
                        Icon(
                            painter = painterResource(
                                if (autostartNextSession) Res.drawable.check else Res.drawable.clear
                            ),
                            contentDescription = null,
                            modifier = Modifier.size(SwitchDefaults.IconSize)
                        )
                    },
                    colors = switchColors
                )
            },
            colors = listItemColors,
            modifier = Modifier
                .clip(topListItemShape)
                .clickable {
                    haptic.performToggle(!autostartNextSession)
                    onAutostartNextSessionChange(!autostartNextSession)
                }
        )

        Box {
            ListItem(
                leadingContent = {
                    Icon(
                        painterResource(Res.drawable.dnd),
                        contentDescription = null,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                },
                headlineContent = { Text(stringResource(Res.string.dnd)) },
                supportingContent = { Text(stringResource(Res.string.dnd_desc)) },
                trailingContent = {
                    Switch(
                        checked = dndEnabled,
                        enabled = !topicRunning,
                        onCheckedChange = {
                            haptic.performToggle(it)
                            onDndEnabledChange(it)
                        },
                        thumbContent = {
                            Icon(
                                painter = painterResource(
                                    if (dndEnabled) Res.drawable.check else Res.drawable.clear
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(SwitchDefaults.IconSize)
                            )
                        },
                        colors = switchColors
                    )
                },
                colors = listItemColors,
                modifier = Modifier
                    .clip(bottomListItemShape)
                    .clickable(enabled = !topicRunning) {
                        haptic.performToggle(!dndEnabled)
                        onDndEnabledChange(!dndEnabled)
                    }
            )
            if (topicRunning) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable(
                            indication = null,
                            interactionSource = null,
                            onClick = onDisabledClick
                        )
                )
            }
        }

        if (showEditButtons && !inTimerScreen && topicId != Topic.DEFAULT_TOPIC_ID) {
            Spacer(Modifier.height(12.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                FilledTonalButton(
                    onClick = onDeleteClick,
                    enabled = !topicRunning,
                    shapes = ButtonDefaults.shapes(),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = colorScheme.errorContainer,
                        contentColor = colorScheme.onErrorContainer
                    )
                ) {
                    Icon(painterResource(Res.drawable.delete), null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(Res.string.delete_topic))
                }
            }
        }
    }
}

@Composable
fun TopicShapeColorPickerButton(
    topicName: String,
    color: Color,
    shape: Shape,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    SegmentedListItem(
        onClick = onClick,
        enabled = enabled,
        shapes = segmentedListItemShapes(0, 1),
        colors = listItemColors,
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (enabled) color else colorScheme.surfaceVariant,
                        shape
                    )
            )
        },
        content = { Text(topicName) },
        supportingContent = { Text(stringResource(Res.string.edit_topic_desc)) },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun TopicTimerSettingsPreview() {
    SpanTheme {
        TopicTimerSettings(
            topic = Topic.defaultTopic,
            topics = listOf(Topic.defaultTopic),
            topicRunning = false,
            minuteInputs = MinuteInputs("25", "5", "15"),
            sessionsSliderState = rememberSliderState(
                value = 4f,
                valueRange = 1f..8f,
                steps = 6
            ),
            onAction = {},
            inTimerScreen = true
        )
    }
}
