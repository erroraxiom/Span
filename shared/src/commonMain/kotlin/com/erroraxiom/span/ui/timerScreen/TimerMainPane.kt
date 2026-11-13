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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.motionScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDropdownMenuItem
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import com.erroraxiom.span.data.Topic
import kotlinx.coroutines.flow.StateFlow
import com.erroraxiom.span.ui.performLongPress
import com.erroraxiom.span.ui.performReject
import com.erroraxiom.span.ui.performToggle
import com.erroraxiom.span.ui.performVirtualKey
import com.erroraxiom.span.ui.rememberRequestNotificationPermissionCallback
import com.erroraxiom.span.ui.settingsScreen.screens.sampleTopics
import com.erroraxiom.span.ui.theme.LocalAppFonts
import com.erroraxiom.span.ui.theme.SpanShapeDefaults.cardShape
import com.erroraxiom.span.ui.theme.SpanTheme
import com.erroraxiom.span.ui.timerScreen.viewModel.TimerAction
import com.erroraxiom.span.ui.timerScreen.viewModel.TimerMode
import com.erroraxiom.span.ui.timerScreen.viewModel.TimerState
import com.erroraxiom.span.ui.topBarWindowInsets
import com.erroraxiom.span.utils.androidSdkVersionAtLeast
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import span.shared.generated.resources.Res
import span.shared.generated.resources.app_name
import span.shared.generated.resources.create_new_topic
import span.shared.generated.resources.focus
import span.shared.generated.resources.goto_settings
import span.shared.generated.resources.infinite_focus
import span.shared.generated.resources.label
import span.shared.generated.resources.long_break
import span.shared.generated.resources.new_label
import span.shared.generated.resources.pause
import span.shared.generated.resources.pause_large
import span.shared.generated.resources.play
import span.shared.generated.resources.play_large
import span.shared.generated.resources.restart
import span.shared.generated.resources.restart_large
import span.shared.generated.resources.short_break
import span.shared.generated.resources.skip_next_large
import span.shared.generated.resources.skip_to_next
import span.shared.generated.resources.timer_reset_message
import span.shared.generated.resources.timer_session_count
import span.shared.generated.resources.timer_settings_reset_info
import span.shared.generated.resources.topic_settings
import span.shared.generated.resources.undo
import span.shared.generated.resources.up_next

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TimerMainPane(
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
    val scope = rememberCoroutineScope()
    val motionScheme = motionScheme
    val timerResetSettingsInfo = stringResource(Res.string.timer_settings_reset_info)
    val timerResetMessage = stringResource(Res.string.timer_reset_message)
    val undo = stringResource(Res.string.undo)

    val colorFraction by animateFloatAsState(
        targetValue = if (timerState.timerMode == TimerMode.FOCUS) 0f else 1f,
        animationSpec = motionScheme.slowEffectsSpec()
    )
    val primary = lerp(colorScheme.primary, colorScheme.tertiary, colorFraction)
    val onPrimary = lerp(colorScheme.onPrimary, colorScheme.onTertiary, colorFraction)
    val primaryContainer = lerp(colorScheme.primaryContainer, colorScheme.tertiaryContainer, colorFraction)
    val onPrimaryContainer = lerp(colorScheme.onPrimaryContainer, colorScheme.onTertiaryContainer, colorFraction)
    val secondaryContainer = lerp(colorScheme.secondaryContainer, colorScheme.tertiaryContainer, colorFraction)
    val onSecondaryContainer = lerp(colorScheme.onSecondaryContainer, colorScheme.onTertiaryContainer, colorFraction)

    val widthExpanded = currentWindowAdaptiveInfoV2()
        .windowSizeClass
        .isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND)

    val requestNotificationPermissionCallback = rememberRequestNotificationPermissionCallback()
    val snackbarHostState = remember { SnackbarHostState() }

    val onShowRunningResetSnackbar: () -> Unit = remember(scope, snackbarHostState, timerResetSettingsInfo) {
        {
            scope.launch {
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar(
                    timerResetSettingsInfo,
                    withDismissAction = true,
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    val onResetSnackbar: () -> Unit = remember(scope, snackbarHostState, timerResetMessage, undo, onAction) {
        {
            scope.launch {
                snackbarHostState.currentSnackbarData?.dismiss()
                val result = snackbarHostState.showSnackbar(
                    timerResetMessage,
                    actionLabel = undo,
                    withDismissAction = true,
                    duration = SnackbarDuration.Long
                )
                if (result == SnackbarResult.ActionPerformed) {
                    onAction(TimerAction.UndoReset)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TimerTopAppBar(
                showBrandTitle = timerState.showBrandTitle,
                infiniteFocus = timerState.infiniteFocus,
                timerMode = timerState.timerMode,
                sessionActive = timerState.sessionActive,
                currentTopic = currentTopic,
                topics = topics,
                primary = primary,
                onPrimary = onPrimary,
                primaryContainer = primaryContainer,
                secondaryContainer = secondaryContainer,
                onSecondaryContainer = onSecondaryContainer,
                onAction = onAction,
                onAddTopic = onAddTopic,
                onOpenTopicSettings = onOpenTopicSettings
            )
        },
        bottomBar = { Spacer(Modifier.height(contentPadding.calculateBottomPadding())) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(18.dp, Alignment.CenterVertically),
            horizontalAlignment = CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            TimerModePillsRow(
                infiniteFocus = timerState.infiniteFocus,
                timerMode = timerState.timerMode,
                currentFocusCount = timerState.currentFocusCount,
                totalFocusCount = timerState.totalFocusCount,
                primaryContainer = primaryContainer,
                onPrimaryContainer = onPrimaryContainer
            )

            TimerDialSection(
                timeStr = timerState.timeStr,
                infiniteFocus = timerState.infiniteFocus,
                timerMode = timerState.timerMode,
                timerRunning = timerState.timerRunning,
                showBrandTitle = timerState.showBrandTitle,
                currentFocusCount = timerState.currentFocusCount,
                totalFocusCount = timerState.totalFocusCount,
                primary = primary,
                secondaryContainer = secondaryContainer,
                progressFlow = progressFlow,
                onAction = onAction,
                onShowRunningResetSnackbar = onShowRunningResetSnackbar
            )

            TimerControlsRow(
                timerRunning = timerState.timerRunning,
                infiniteFocus = timerState.infiniteFocus,
                primary = primary,
                onPrimary = onPrimary,
                primaryContainer = primaryContainer,
                onPrimaryContainer = onPrimaryContainer,
                secondaryContainer = secondaryContainer,
                onSecondaryContainer = onSecondaryContainer,
                onAction = onAction,
                onResetSnackbar = onResetSnackbar,
                requestNotificationPermissionCallback = requestNotificationPermissionCallback
            )

            if (!widthExpanded) {
                TimerUpNextCard(
                    infiniteFocus = timerState.infiniteFocus,
                    nextTimerMode = timerState.nextTimerMode,
                    nextTimeStr = timerState.nextTimeStr
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TimerTopAppBar(
    showBrandTitle: Boolean,
    infiniteFocus: Boolean,
    timerMode: TimerMode,
    sessionActive: Boolean,
    currentTopic: Topic,
    topics: List<Topic>,
    primary: Color,
    onPrimary: Color,
    primaryContainer: Color,
    secondaryContainer: Color,
    onSecondaryContainer: Color,
    onAction: (TimerAction) -> Unit,
    onAddTopic: () -> Unit,
    onOpenTopicSettings: () -> Unit
) {
    val motionScheme = motionScheme
    val fonts = LocalAppFonts.current
    val cookieShape = MaterialShapes.Cookie9Sided.toShape()

    TopAppBar(
        windowInsets = topBarWindowInsets(),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(34.dp)
                        .background(primaryContainer, cookieShape)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(primary, CircleShape)
                    )
                }

                AnimatedContent(
                    targetState = if (!showBrandTitle) timerMode else TimerMode.BRAND,
                    transitionSpec = {
                        slideInVertically(
                            animationSpec = motionScheme.defaultSpatialSpec(),
                            initialOffsetY = { (-it * 1.25).toInt() }
                        ).togetherWith(
                            slideOutVertically(
                                animationSpec = motionScheme.defaultSpatialSpec(),
                                targetOffsetY = { (it * 1.25).toInt() }
                            )
                        )
                    },
                    label = "titleAnimation"
                ) { mode ->
                    Text(
                        text = when (mode) {
                            TimerMode.BRAND ->
                                if (!infiniteFocus || timerMode != TimerMode.FOCUS) stringResource(Res.string.app_name)
                                else stringResource(Res.string.infinite_focus)

                            TimerMode.FOCUS ->
                                if (infiniteFocus) stringResource(Res.string.infinite_focus)
                                else stringResource(Res.string.focus)
                            TimerMode.SHORT_BREAK -> stringResource(Res.string.short_break)
                            TimerMode.LONG_BREAK -> stringResource(Res.string.long_break)
                        },
                        style = TextStyle(
                            fontFamily = fonts.topBarTitle,
                            fontSize = 26.sp,
                            lineHeight = 30.sp,
                            color = colorScheme.onSurface
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        actions = {
            Box(modifier = Modifier.padding(end = 8.dp)) {
                var expanded by remember { mutableStateOf(false) }
                val canSwitchTopic = !sessionActive

                Surface(
                    onClick = { expanded = !expanded },
                    shape = if (expanded) shapes.medium else CircleShape,
                    color = if (expanded) primary else secondaryContainer,
                    contentColor = if (expanded) onPrimary else onSecondaryContainer,
                    modifier = Modifier
                        .height(42.dp)
                        .testTag("topic_selector_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(horizontal = 14.dp)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.label),
                            contentDescription = currentTopic.name,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = currentTopic.name,
                            style = typography.labelLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                if (expanded) {
                    val topicItemColors = MenuDefaults.selectableItemColors()
                    DropdownMenuPopup(
                        expanded = true,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuGroup(shapes = MenuDefaults.groupShape(0, 2)) {
                            topics.fastForEachIndexed { index, topic ->
                                SelectableDropdownMenuItem(
                                    selected = topic.id == currentTopic.id,
                                    onClick = {
                                        expanded = false
                                        if (canSwitchTopic) onAction(TimerAction.SetTopic(topic))
                                    },
                                    text = { Text(topic.name) },
                                    colors = topicItemColors,
                                    shapes = MenuDefaults.itemShape(index, topics.size),
                                    enabled = canSwitchTopic
                                )
                            }
                        }

                        Spacer(Modifier.height(MenuDefaults.GroupSpacing))

                        DropdownMenuGroup(shapes = MenuDefaults.groupShape(1, 2)) {
                            DropdownMenuItem(
                                onClick = {
                                    expanded = false
                                    onAddTopic()
                                },
                                text = { Text(stringResource(Res.string.create_new_topic)) },
                                leadingIcon = {
                                    Icon(painterResource(Res.drawable.new_label), null)
                                },
                                shape = MenuDefaults.leadingItemShape
                            )

                            DropdownMenuItem(
                                onClick = {
                                    expanded = false
                                    onOpenTopicSettings()
                                },
                                text = { Text(stringResource(Res.string.topic_settings)) },
                                leadingIcon = {
                                    Icon(painterResource(Res.drawable.goto_settings), null)
                                },
                                shape = MenuDefaults.trailingItemShape
                            )
                        }
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
    )
}

@Composable
private fun TimerModePillsRow(
    infiniteFocus: Boolean,
    timerMode: TimerMode,
    currentFocusCount: Int,
    totalFocusCount: Int,
    primaryContainer: Color,
    onPrimaryContainer: Color
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = primaryContainer,
            contentColor = onPrimaryContainer,
            shape = CircleShape
        ) {
            Text(
                text = when {
                    infiniteFocus && timerMode == TimerMode.FOCUS -> stringResource(Res.string.infinite_focus)
                    timerMode == TimerMode.FOCUS -> stringResource(Res.string.focus)
                    timerMode == TimerMode.SHORT_BREAK -> stringResource(Res.string.short_break)
                    else -> stringResource(Res.string.long_break)
                },
                style = typography.labelLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        if (!infiniteFocus || timerMode != TimerMode.FOCUS) {
            Surface(
                color = colorScheme.surfaceContainerHigh,
                contentColor = colorScheme.onSurfaceVariant,
                shape = shapes.medium
            ) {
                Text(
                    text = stringResource(
                        Res.string.timer_session_count,
                        currentFocusCount,
                        totalFocusCount
                    ),
                    style = typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TimerDialSection(
    timeStr: String,
    infiniteFocus: Boolean,
    timerMode: TimerMode,
    timerRunning: Boolean,
    showBrandTitle: Boolean,
    currentFocusCount: Int,
    totalFocusCount: Int,
    primary: Color,
    secondaryContainer: Color,
    progressFlow: StateFlow<Float>,
    onAction: (TimerAction) -> Unit,
    onShowRunningResetSnackbar: () -> Unit
) {
    val progressState = progressFlow.collectAsStateWithLifecycle()
    val progress = remember(progressState) { { progressState.value } }

    val motionScheme = motionScheme
    val haptic = LocalHapticFeedback.current
    val fonts = LocalAppFonts.current
    val density = LocalDensity.current
    val strokePx = remember(density) { with(density) { 16.dp.toPx() } }
    val wavyStroke = remember(strokePx) { Stroke(width = strokePx, cap = StrokeCap.Round) }

    val isInfinite = infiniteFocus && timerMode == TimerMode.FOCUS

    Column(horizontalAlignment = CenterHorizontally) {
        Box(contentAlignment = Alignment.Center) {
            if (!isInfinite) {
                if (timerMode == TimerMode.FOCUS) {
                    CircularProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .widthIn(max = 310.dp)
                            .fillMaxWidth(0.84f)
                            .aspectRatio(1f),
                        color = primary,
                        trackColor = secondaryContainer,
                        strokeWidth = 16.dp,
                        gapSize = 10.dp
                    )
                } else {
                    val amplitude by animateFloatAsState(
                        targetValue = if (timerRunning) 1f else 0f,
                        animationSpec = motionScheme.slowEffectsSpec()
                    )
                    val waveSpeed = if (timerRunning) 20.dp else 0.dp
                    CircularWavyProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .widthIn(max = 310.dp)
                            .fillMaxWidth(0.84f)
                            .aspectRatio(1f),
                        color = primary,
                        trackColor = secondaryContainer,
                        stroke = wavyStroke,
                        trackStroke = wavyStroke,
                        wavelength = 60.dp,
                        waveSpeed = waveSpeed,
                        amplitude = { amplitude },
                        gapSize = 10.dp
                    )
                }
            }

            var expanded by remember { mutableStateOf(showBrandTitle) }
            LaunchedEffect(showBrandTitle) {
                expanded = showBrandTitle
            }
            Column(
                horizontalAlignment = CenterHorizontally,
                modifier = Modifier
                    .clip(shapes.largeIncreased)
                    .combinedClickable(
                        hapticFeedbackEnabled = false,
                        onClick = { expanded = !expanded },
                        onLongClick = {
                            if (!timerRunning) {
                                haptic.performLongPress()
                                onAction(TimerAction.SetInfiniteFocus(!infiniteFocus))
                            } else {
                                haptic.performReject()
                                onShowRunningResetSnackbar()
                            }
                        }
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .testTag("timer_display_readout")
            ) {
                val clockFontSize = with(density) {
                    if (!isInfinite) {
                        if (timeStr.length < 6) 48.dp.toSp() else 36.dp.toSp()
                    } else {
                        50.dp.toSp()
                    }
                }

                Text(
                    text = timeStr,
                    style = TextStyle(
                        fontFamily = fonts.clock,
                        fontSize = clockFontSize,
                        letterSpacing = 0.sp,
                        fontFeatureSettings = "tnum"
                    ),
                    textAlign = TextAlign.Center,
                    color = colorScheme.onSurface,
                    maxLines = 1,
                    softWrap = false,
                    modifier = if (isInfinite) Modifier.padding(vertical = 12.dp) else Modifier
                )

                AnimatedVisibility(
                    visible = expanded && !isInfinite,
                    enter = fadeIn(motionScheme.defaultEffectsSpec()) +
                        expandVertically(motionScheme.defaultSpatialSpec()),
                    exit = fadeOut(motionScheme.defaultEffectsSpec()) +
                        shrinkVertically(motionScheme.defaultSpatialSpec())
                ) {
                    Text(
                        text = stringResource(
                            Res.string.timer_session_count,
                            currentFocusCount,
                            totalFocusCount
                        ),
                        fontFamily = fonts.topBarTitle,
                        style = typography.titleLarge,
                        color = colorScheme.outline
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TimerControlsRow(
    timerRunning: Boolean,
    infiniteFocus: Boolean,
    primary: Color,
    onPrimary: Color,
    primaryContainer: Color,
    onPrimaryContainer: Color,
    secondaryContainer: Color,
    onSecondaryContainer: Color,
    onAction: (TimerAction) -> Unit,
    onResetSnackbar: () -> Unit,
    requestNotificationPermissionCallback: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val fonts = LocalAppFonts.current

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        FilledTonalIconButton(
            onClick = {
                onAction(TimerAction.ResetTimer)
                haptic.performVirtualKey()
                onResetSnackbar()
            },
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = secondaryContainer,
                contentColor = onSecondaryContainer
            ),
            shapes = IconButtonDefaults.shapes(),
            modifier = Modifier
                .size(64.dp)
                .testTag("reset_timer_button")
        ) {
            Icon(
                painterResource(Res.drawable.restart_large),
                contentDescription = stringResource(Res.string.restart),
                modifier = Modifier.size(30.dp)
            )
        }

        FilledIconToggleButton(
            onCheckedChange = { checked ->
                onAction(TimerAction.ToggleTimer)
                haptic.performToggle(checked)

                if (androidSdkVersionAtLeast(33) && checked) {
                    requestNotificationPermissionCallback()
                }
            },
            checked = timerRunning,
            colors = IconButtonDefaults.filledIconToggleButtonColors(
                checkedContainerColor = primary,
                checkedContentColor = onPrimary,
                containerColor = primaryContainer,
                contentColor = onPrimaryContainer
            ),
            shapes = IconButtonDefaults.toggleableShapes(
                shape = CircleShape,
                checkedShape = shapes.largeIncreased
            ),
            modifier = Modifier
                .size(width = 148.dp, height = 64.dp)
                .testTag("primary_timer_toggle_button")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = painterResource(
                        if (timerRunning) Res.drawable.pause_large
                        else Res.drawable.play_large
                    ),
                    contentDescription = if (timerRunning) {
                        stringResource(Res.string.pause)
                    } else {
                        stringResource(Res.string.play)
                    },
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = if (timerRunning) {
                        stringResource(Res.string.pause)
                    } else {
                        stringResource(Res.string.play)
                    },
                    style = typography.titleMedium.copy(
                        fontFamily = fonts.topBarTitle,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        FilledTonalIconButton(
            onClick = {
                onAction(TimerAction.SkipTimer(fromButton = true))
                haptic.performVirtualKey()
            },
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = secondaryContainer,
                contentColor = onSecondaryContainer
            ),
            shapes = IconButtonDefaults.shapes(),
            modifier = Modifier
                .size(64.dp)
                .testTag("skip_timer_button")
        ) {
            Icon(
                painterResource(Res.drawable.skip_next_large),
                contentDescription = stringResource(Res.string.skip_to_next),
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TimerUpNextCard(
    infiniteFocus: Boolean,
    nextTimerMode: TimerMode,
    nextTimeStr: String
) {
    val motionScheme = motionScheme
    val fonts = LocalAppFonts.current

    Box(
        modifier = Modifier
            .widthIn(max = 400.dp)
            .fillMaxWidth()
    ) {
        Card(
            shape = cardShape,
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surfaceContainer
            ),
            modifier = Modifier
                .widthIn(max = 400.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = stringResource(Res.string.up_next),
                        style = typography.labelMedium,
                        color = colorScheme.onSurfaceVariant
                    )
                    AnimatedContent(
                        targetState = nextTimerMode,
                        transitionSpec = {
                            fadeIn(motionScheme.defaultEffectsSpec())
                                .togetherWith(fadeOut(motionScheme.defaultEffectsSpec()))
                        }
                    ) { nextMode ->
                        Text(
                            text = when (nextMode) {
                                TimerMode.FOCUS -> stringResource(Res.string.focus)
                                TimerMode.SHORT_BREAK -> stringResource(Res.string.short_break)
                                else -> stringResource(Res.string.long_break)
                            },
                            style = typography.titleMedium.copy(
                                fontFamily = fonts.topBarTitle
                            ),
                            color = colorScheme.onSurface
                        )
                    }
                }

                AnimatedContent(
                    targetState = nextTimeStr,
                    transitionSpec = {
                        fadeIn(motionScheme.defaultEffectsSpec())
                            .togetherWith(fadeOut(motionScheme.defaultEffectsSpec()))
                    }
                ) { nextTime ->
                    val isNextFocus = nextTimerMode == TimerMode.FOCUS
                    Surface(
                        shape = CircleShape,
                        color = if (isNextFocus) colorScheme.primaryContainer else colorScheme.tertiaryContainer,
                        contentColor = if (isNextFocus) colorScheme.onPrimaryContainer else colorScheme.onTertiaryContainer
                    ) {
                        Text(
                            text = nextTime,
                            style = typography.titleLarge.copy(
                                fontFamily = fonts.topBarTitle
                            ),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun TimerMainPanePreview() {
    val timerState = TimerState(
        timeStr = "03:34", nextTimeStr = "5:00", timerMode = TimerMode.FOCUS, timerRunning = true
    )
    SpanTheme {
        Surface {
            TimerMainPane(
                timerState = timerState,
                topics = sampleTopics,
                currentTopic = sampleTopics[5],
                contentPadding = PaddingValues(),
                progressFlow = remember { kotlinx.coroutines.flow.MutableStateFlow(0.3f) },
                {},
                {},
                {}
            )
        }
    }
}
