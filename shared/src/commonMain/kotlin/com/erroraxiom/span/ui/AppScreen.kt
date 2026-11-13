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

package com.erroraxiom.span.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.motionScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import com.erroraxiom.span.data.Topic
import com.erroraxiom.span.ui.settingsScreen.SettingsScreenRoot
import com.erroraxiom.span.ui.settingsScreen.components.CreateTopicBottomSheet
import com.erroraxiom.span.ui.settingsScreen.viewModel.SettingsViewModel
import com.erroraxiom.span.ui.statsScreen.StatsScreenRoot
import com.erroraxiom.span.ui.statsScreen.viewModel.StatsViewModel
import com.erroraxiom.span.ui.theme.SeededTheme
import com.erroraxiom.span.ui.timerScreen.AlarmDialog
import com.erroraxiom.span.ui.timerScreen.TimerScreen
import com.erroraxiom.span.ui.timerScreen.viewModel.TimerAction
import com.erroraxiom.span.ui.timerScreen.viewModel.TimerMode
import com.erroraxiom.span.ui.timerScreen.viewModel.TimerViewModel
import com.erroraxiom.span.utils.onBack
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import span.shared.generated.resources.Res
import span.shared.generated.resources.monitoring
import span.shared.generated.resources.monitoring_filled
import span.shared.generated.resources.settings
import span.shared.generated.resources.settings_filled
import span.shared.generated.resources.stats
import span.shared.generated.resources.timer
import span.shared.generated.resources.timer_filled
import span.shared.generated.resources.timer_outlined

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppScreen(
    isAODEnabled: Boolean,
    setTimerFrequency: (Float) -> Unit,
    modifier: Modifier = Modifier,
    timerViewModel: TimerViewModel = koinViewModel(),
    settingsViewModel: SettingsViewModel = koinViewModel(),
    statsViewModel: StatsViewModel = koinViewModel()
) {
    val timerMode by timerViewModel.timerMode.collectAsStateWithLifecycle()
    val alarmRinging by timerViewModel.alarmRinging.collectAsStateWithLifecycle()
    val currentTopic by timerViewModel.currentTopic.collectAsStateWithLifecycle()

    val layoutDirection = LocalLayoutDirection.current
    val haptic = LocalHapticFeedback.current
    val motionScheme = motionScheme
    val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
    val systemBarsInsets = WindowInsets.systemBars.asPaddingValues()
    val cutoutInsets = WindowInsets.displayCutout.asPaddingValues()

    val backStack = timerViewModel.rootBackstack

    val mainScreens = remember {
        listOf(
            NavItem(
                Screen.Timer,
                Res.drawable.timer_outlined,
                Res.drawable.timer_filled,
                Res.string.timer
            ) {},
            NavItem(
                Screen.Stats.Main,
                Res.drawable.monitoring,
                Res.drawable.monitoring_filled,
                Res.string.stats
            ) {
                if (statsViewModel.backStack.size > 1) {
                    statsViewModel.backStack.removeRange(1, statsViewModel.backStack.size)
                }
            },
            NavItem(
                Screen.Settings.Main,
                Res.drawable.settings,
                Res.drawable.settings_filled,
                Res.string.settings
            ) {
                if (settingsViewModel.backStack.size > 1) {
                    settingsViewModel.backStack.removeRange(1, settingsViewModel.backStack.size)
                }
            }
        )
    }

    val isInTimer by remember {
        derivedStateOf { backStack.lastOrNull() == Screen.Timer }
    }

    val colorFraction by animateFloatAsState(
        targetValue = if (timerMode == TimerMode.FOCUS || !isInTimer) 0f else 1f,
        animationSpec = motionScheme.slowEffectsSpec()
    )

    Scaffold(
        contentWindowInsets = LocalContentWindowInsets.current(),
        bottomBar = {
            AnimatedVisibility(
                visible = backStack.lastOrNull() !is Screen.AOD,
                enter = slideInVertically(motionScheme.defaultSpatialSpec()) { it },
                exit = slideOutVertically(motionScheme.defaultSpatialSpec()) { it }
            ) {
                val wide = remember(windowSizeClass) {
                    windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)
                }

                SeededTheme(
                    if (isInTimer && currentTopic.id != Topic.DEFAULT_TOPIC_ID) {
                        currentTopic.color
                    } else {
                        Color.White
                    }
                ) {
                    val primary = lerp(
                        colorScheme.primary,
                        colorScheme.tertiary,
                        colorFraction
                    )
                    val onPrimary = lerp(
                        colorScheme.onPrimary,
                        colorScheme.onTertiary,
                        colorFraction
                    )
                    val primaryContainer = lerp(
                        colorScheme.primaryContainer,
                        colorScheme.tertiaryContainer,
                        colorFraction
                    )
                    val onPrimaryContainer = lerp(
                        colorScheme.onPrimaryContainer,
                        colorScheme.onTertiaryContainer,
                        colorFraction
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = cutoutInsets.calculateStartPadding(layoutDirection),
                                end = cutoutInsets.calculateEndPadding(layoutDirection)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        HorizontalFloatingToolbar(
                            expanded = true,
                            colors = FloatingToolbarDefaults.vibrantFloatingToolbarColors(
                                toolbarContainerColor = primaryContainer,
                                toolbarContentColor = onPrimaryContainer
                            ),
                            modifier = Modifier
                                .padding(
                                    top = FloatingToolbarDefaults.ScreenOffset,
                                    bottom = systemBarsInsets.calculateBottomPadding()
                                        + FloatingToolbarDefaults.ScreenOffset
                                )
                                .zIndex(1f)
                        ) {
                            mainScreens.fastForEachIndexed { index, item ->
                                val selected = backStack.lastOrNull() == item.route
                                val labelStr = stringResource(item.label)

                                ToggleButton(
                                    checked = selected,
                                    onCheckedChange = {
                                        haptic.performVirtualKey()
                                        if (item.route != Screen.Timer) {
                                            if (!selected) {
                                                if (backStack.size < 2) {
                                                    backStack.add(item.route)
                                                } else {
                                                    backStack[1] = item.route
                                                    if (backStack.size > 2) {
                                                        backStack.removeRange(2, backStack.size)
                                                    }
                                                }
                                            } else {
                                                item.onNavigateHome()
                                            }
                                        } else {
                                            if (backStack.size > 1) {
                                                backStack.removeRange(1, backStack.size)
                                            }
                                        }
                                    },
                                    colors = ToggleButtonDefaults.colors(
                                        containerColor = primaryContainer,
                                        contentColor = onPrimaryContainer,
                                        checkedContainerColor = primary,
                                        checkedContentColor = onPrimary
                                    ),
                                    shapes = when (index) {
                                        0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                        mainScreens.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                        else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                                    },
                                    modifier = Modifier
                                        .height(48.dp)
                                        .testTag("nav_tab_${labelStr.lowercase()}")
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            painter = painterResource(
                                                if (selected) item.selectedIcon else item.unselectedIcon
                                            ),
                                            contentDescription = labelStr
                                        )
                                        if (selected || wide) {
                                            Spacer(Modifier.width(8.dp))
                                            Text(
                                                text = labelStr,
                                                fontSize = 16.sp,
                                                style = typography.labelLarge,
                                                maxLines = 1,
                                                softWrap = false,
                                                overflow = TextOverflow.Clip
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        modifier = modifier
    ) { contentPadding ->
        val aodInteractionSource = remember { MutableInteractionSource() }
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
                entry<Screen.Timer> {
                    val uiState by timerViewModel.timerState.collectAsStateWithLifecycle()
                    val topics by settingsViewModel.allTopics.collectAsStateWithLifecycle()
                    var showCreateTopicSheet by remember { mutableStateOf(false) }

                    if (showCreateTopicSheet) {
                        CreateTopicBottomSheet(
                            topics = topics,
                            setShowSheet = { showCreateTopicSheet = it },
                            onAction = settingsViewModel::onAction,
                            setAsCurrent = true
                        )
                    }

                    TimerScreen(
                        timerState = uiState,
                        topics = topics,
                        currentTopic = currentTopic,
                        contentPadding = contentPadding,
                        progressFlow = timerViewModel.progress,
                        onAction = timerViewModel::onAction,
                        onAddTopic = { showCreateTopicSheet = true },
                        onOpenTopicSettings = {
                            if (backStack.size < 2) backStack.add(Screen.Settings.Main)
                            else backStack[1] = Screen.Settings.Main
                            settingsViewModel.backStack.clear()
                            settingsViewModel.backStack.add(Screen.Settings.Main)
                            settingsViewModel.backStack.add(Screen.Settings.Topics)
                        },
                        modifier = if (isAODEnabled) Modifier
                            .clickable(
                                interactionSource = aodInteractionSource,
                                onClick = {
                                    if (!uiState.timerRunning)
                                        timerViewModel.onAction(TimerAction.ToggleTimer)
                                    if (backStack.size < 2)
                                        backStack.add(Screen.AOD)
                                }
                            )
                        else Modifier
                    )
                }

                entry<Screen.AOD> {
                    val uiState by timerViewModel.timerState.collectAsStateWithLifecycle()
                    val settingsState by settingsViewModel.settingsState.collectAsStateWithLifecycle()
                    AlwaysOnDisplay(
                        timerState = uiState,
                        secureAod = settingsState.secureAod,
                        progressFlow = timerViewModel.progress,
                        setTimerFrequency = setTimerFrequency,
                        modifier = if (isAODEnabled) Modifier
                            .clickable(
                                interactionSource = aodInteractionSource,
                                indication = null,
                                onClick = { if (backStack.size > 1) backStack.removeLastOrNull() }
                            )
                            .hideCursor()
                        else Modifier
                    )
                }

                entry<Screen.Settings.Main> {
                    SettingsScreenRoot(
                        contentPadding = contentPadding,
                        viewModel = settingsViewModel
                    )
                }

                entry<Screen.Stats.Main> {
                    val settingsState by settingsViewModel.settingsState.collectAsStateWithLifecycle()
                    StatsScreenRoot(
                        contentPadding = contentPadding,
                        focusGoal = settingsState.focusGoal,
                        viewModel = statsViewModel
                    )
                }
            }
        )
    }

    AnimatedVisibility(
        visible = alarmRinging,
        enter = fadeIn(motionScheme.defaultEffectsSpec()) + slideInVertically(motionScheme.slowSpatialSpec()) { -it },
        exit = fadeOut(motionScheme.defaultEffectsSpec()) + slideOutVertically(motionScheme.slowSpatialSpec()) { -it }
    ) {
        AlarmDialog {
            timerViewModel.onAction(TimerAction.StopAlarm)
        }
    }
}

val LocalContentWindowInsets = compositionLocalOf {
    @Composable { ScaffoldDefaults.contentWindowInsets }
}

@Composable
fun topBarWindowInsets(): WindowInsets =
    LocalContentWindowInsets
        .current()
        .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
