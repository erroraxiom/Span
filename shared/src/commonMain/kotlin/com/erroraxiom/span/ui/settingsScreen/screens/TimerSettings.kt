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

package com.erroraxiom.span.ui.settingsScreen.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SliderState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import com.erroraxiom.span.data.Topic
import com.erroraxiom.span.ui.mergePaddingValues
import com.erroraxiom.span.ui.performReject
import com.erroraxiom.span.ui.performToggle
import com.erroraxiom.span.ui.settingsScreen.SettingsSwitchItem
import com.erroraxiom.span.ui.settingsScreen.components.MinuteInputs
import com.erroraxiom.span.ui.settingsScreen.components.TopicTimerSettings
import com.erroraxiom.span.ui.settingsScreen.viewModel.SettingsAction
import com.erroraxiom.span.ui.settingsScreen.viewModel.SettingsState
import com.erroraxiom.span.ui.theme.CustomColors.detailPaneTopBarColors
import com.erroraxiom.span.ui.theme.CustomColors.listItemColors
import com.erroraxiom.span.ui.theme.CustomColors.switchColors
import com.erroraxiom.span.ui.theme.CustomColors.topBarColors
import com.erroraxiom.span.ui.theme.LocalAppFonts
import com.erroraxiom.span.ui.theme.SpanShapeDefaults.PANE_MAX_WIDTH
import com.erroraxiom.span.ui.theme.SpanShapeDefaults.bottomListItemShape
import com.erroraxiom.span.ui.theme.SpanShapeDefaults.cardShape
import com.erroraxiom.span.ui.theme.SpanShapeDefaults.middleListItemShape
import com.erroraxiom.span.ui.theme.SpanShapeDefaults.topListItemShape
import com.erroraxiom.span.ui.theme.SpanTheme
import com.erroraxiom.span.ui.topBarWindowInsets
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import span.shared.generated.resources.Res
import span.shared.generated.resources.always_on_display
import span.shared.generated.resources.always_on_display_desc
import span.shared.generated.resources.aod
import span.shared.generated.resources.arrow_back
import span.shared.generated.resources.back
import span.shared.generated.resources.check
import span.shared.generated.resources.clear
import span.shared.generated.resources.mobile_lock_portrait
import span.shared.generated.resources.secure_aod
import span.shared.generated.resources.secure_aod_desc
import span.shared.generated.resources.session_only_progress
import span.shared.generated.resources.session_only_progress_desc
import span.shared.generated.resources.settings
import span.shared.generated.resources.timer
import span.shared.generated.resources.timer_settings_reset_info
import span.shared.generated.resources.view_day

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TimerSettings(
    settingsState: SettingsState,
    topics: List<Topic>,
    editingTopic: Topic,
    currentTopicId: Long,
    sessionActive: Boolean,
    contentPadding: PaddingValues,
    minuteInputs: MinuteInputs,
    sessionsSliderState: SliderState,
    onAction: (SettingsAction) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }

    val widthExpanded = currentWindowAdaptiveInfoV2()
        .windowSizeClass
        .isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND)

    val switchItems = remember(
        settingsState.aodEnabled,
        settingsState.secureAod,
        settingsState.singleProgressBar,
        sessionActive
    ) {
        listOf(
            listOf(
                SettingsSwitchItem(
                    checked = settingsState.aodEnabled,
                    icon = Res.drawable.aod,
                    label = Res.string.always_on_display,
                    description = Res.string.always_on_display_desc,
                    onClick = { onAction(SettingsAction.SaveAodEnabled(it)) }
                ),
                SettingsSwitchItem(
                    checked = settingsState.secureAod,
                    enabled = settingsState.aodEnabled,
                    collapsible = true,
                    icon = Res.drawable.mobile_lock_portrait,
                    label = Res.string.secure_aod,
                    description = Res.string.secure_aod_desc,
                    onClick = { onAction(SettingsAction.SaveSecureAod(it)) }
                )
            ),
            listOf(
                SettingsSwitchItem(
                    checked = settingsState.singleProgressBar,
                    enabled = !sessionActive,
                    icon = Res.drawable.view_day,
                    label = Res.string.session_only_progress,
                    description = Res.string.session_only_progress_desc,
                    onClick = { onAction(SettingsAction.SaveSingleProgressBar(it)) }
                )
            )
        )
    }

    val barColors = if (widthExpanded) detailPaneTopBarColors else topBarColors

    val timerSettingsResetInfoString = stringResource(Res.string.timer_settings_reset_info)
    val onDisabledClick: () -> Unit = {
        haptic.performReject()
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(
                timerSettingsResetInfoString,
                withDismissAction = true,
                duration = SnackbarDuration.Short
            )
        }
    }

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
                            stringResource(Res.string.timer),
                            fontFamily = LocalAppFonts.current.topBarTitle
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
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = barColors.containerColor,
            modifier = modifier
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
                item(key = "top_spacer") {
                    Spacer(Modifier.height(12.dp))
                }

                item(key = "topic_timer_settings") {
                    TopicTimerSettings(
                        topic = editingTopic,
                        topics = topics,
                        topicRunning = sessionActive && editingTopic.id == currentTopicId,
                        minuteInputs = minuteInputs,
                        sessionsSliderState = sessionsSliderState,
                        onAction = onAction,
                        inTimerScreen = true,
                        onDisabledClick = onDisabledClick
                    )
                }

                switchItems.forEachIndexed { sIdx, section ->
                    item(key = "spacer_section_$sIdx") {
                        Spacer(Modifier.height(12.dp))
                    }
                    itemsIndexed(
                        section,
                        key = { _, item -> "switch_${sIdx}_${item.label}" }
                    ) { index, item ->
                        AnimatedVisibility(item.enabled || !item.collapsible) {
                            Box {
                                ListItem(
                                    leadingContent = {
                                        Icon(
                                            painterResource(item.icon),
                                            contentDescription = null,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    },
                                    headlineContent = { Text(stringResource(item.label)) },
                                    supportingContent = { Text(stringResource(item.description)) },
                                    trailingContent = {
                                        Switch(
                                            checked = item.checked,
                                            onCheckedChange = {
                                                haptic.performToggle(it)
                                                item.onClick(it)
                                            },
                                            enabled = item.enabled,
                                            thumbContent = {
                                                if (item.checked) {
                                                    Icon(
                                                        painter = painterResource(Res.drawable.check),
                                                        contentDescription = null,
                                                        modifier = Modifier.size(SwitchDefaults.IconSize),
                                                    )
                                                } else {
                                                    Icon(
                                                        painter = painterResource(Res.drawable.clear),
                                                        contentDescription = null,
                                                        modifier = Modifier.size(SwitchDefaults.IconSize),
                                                    )
                                                }
                                            },
                                            colors = switchColors
                                        )
                                    },
                                    colors = listItemColors,
                                    modifier = Modifier
                                        .clip(
                                            when {
                                                section.size == 1 -> cardShape
                                                index == 0 -> topListItemShape
                                                index == section.lastIndex -> bottomListItemShape
                                                else -> middleListItemShape
                                            }
                                        )
                                        .clickable(enabled = item.enabled) {
                                            haptic.performToggle(!item.checked)
                                            item.onClick(!item.checked)
                                        }
                                )

                                if (!item.enabled) {
                                    Box(
                                        modifier = Modifier
                                            .matchParentSize()
                                            .clickable(
                                                enabled = sessionActive,
                                                indication = null,
                                                interactionSource = null,
                                                onClick = onDisabledClick
                                            )
                                    )
                                }
                            }
                        }
                    }
                }

                item(key = "bottom_spacer") { Spacer(Modifier.height(12.dp)) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    showSystemUi = true,
    device = Devices.PIXEL_9_PRO
)
@Composable
fun TimerSettingsPreview() {
    val settingsState = SettingsState()
    SpanTheme(dynamicColor = false) {
        TimerSettings(
            settingsState = settingsState,
            topics = sampleTopics,
            editingTopic = Topic.defaultTopic,
            currentTopicId = Topic.DEFAULT_TOPIC_ID,
            sessionActive = false,
            contentPadding = PaddingValues(),
            minuteInputs = MinuteInputs("25", "5", "15"),
            sessionsSliderState = rememberSliderState(value = 4f, steps = 7, valueRange = 1f..8f),
            onAction = {},
            onBack = {}
        )
    }
}