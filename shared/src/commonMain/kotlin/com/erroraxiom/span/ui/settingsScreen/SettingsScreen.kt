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

package com.erroraxiom.span.ui.settingsScreen

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import com.erroraxiom.span.ui.Screen
import com.erroraxiom.span.ui.androidSystemGestureExclusion
import com.erroraxiom.span.ui.calculatePaneScaffoldDirective
import com.erroraxiom.span.ui.settingsScreen.screens.AboutScreen
import com.erroraxiom.span.ui.settingsScreen.screens.AlarmSettings
import com.erroraxiom.span.ui.settingsScreen.screens.AppearanceSettings
import com.erroraxiom.span.ui.settingsScreen.screens.SettingsMainScreen
import com.erroraxiom.span.ui.settingsScreen.screens.TimerSettings
import com.erroraxiom.span.ui.settingsScreen.screens.TopicsSettings
import com.erroraxiom.span.ui.settingsScreen.viewModel.SettingsAction
import com.erroraxiom.span.ui.settingsScreen.viewModel.SettingsState
import com.erroraxiom.span.ui.settingsScreen.viewModel.SettingsViewModel
import com.erroraxiom.span.ui.theme.CustomColors.detailPaneTopBarColors
import com.erroraxiom.span.utils.onBack
import org.koin.compose.viewmodel.koinViewModel
import span.shared.generated.resources.Res
import span.shared.generated.resources.settings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenRoot(
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val backStack = viewModel.backStack

    DisposableEffect(Unit) {
        onDispose { viewModel.onSettingsClosed() }
    }

    val settingsState by viewModel.settingsState.collectAsStateWithLifecycle()
    val isPlus by viewModel.isPlus.collectAsStateWithLifecycle()

    if (settingsState.isShowingEraseDataDialog) {
        ResetDataDialog(
            resetData = { viewModel.onAction(SettingsAction.EraseData) },
            onDismiss = { viewModel.onAction(SettingsAction.CancelEraseData) }
        )
    }

    SettingsScreen(
        settingsState = settingsState,
        backStack = backStack,
        viewModel = viewModel,
        isPlus = isPlus,
        contentPadding = contentPadding,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun SettingsScreen(
    settingsState: SettingsState,
    backStack: SnapshotStateList<Screen.Settings>,
    viewModel: SettingsViewModel,
    isPlus: Boolean,
    contentPadding: PaddingValues,
    onAction: (SettingsAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val motionScheme = motionScheme
    val adaptiveInfo = currentWindowAdaptiveInfoV2()
    val widthExpanded = adaptiveInfo.windowSizeClass
        .isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND)

    val navigateToSubScreen: (Screen.Settings) -> Unit = { route ->
        if (backStack.size < 2) backStack.add(route)
        else backStack[backStack.lastIndex] = route
    }

    @Composable
    fun SettingsNavDisplay(isTwoPane: Boolean) {
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
                entry<Screen.Settings.Main> {
                    if (isTwoPane) {
                        DetailPlaceholder(
                            icon = Res.drawable.settings,
                            background = detailPaneTopBarColors.containerColor
                        )
                    } else {
                        SettingsMainScreen(
                            settingsState = settingsState,
                            contentPadding = contentPadding,
                            isPlus = isPlus,
                            onResetDataClick = { onAction(SettingsAction.AskEraseData) },
                            onNavigate = navigateToSubScreen
                        )
                    }
                }
                entry<Screen.Settings.Alarm> {
                    AlarmSettings(
                        settingsState = settingsState,
                        contentPadding = contentPadding,
                        onAction = onAction,
                        onBack = backStack::onBack
                    )
                }
                entry<Screen.Settings.Appearance> {
                    AppearanceSettings(
                        settingsState = settingsState,
                        contentPadding = contentPadding,
                        onAction = onAction,
                        onBack = backStack::onBack
                    )
                }
                entry<Screen.Settings.Timer> {
                    val editingTopic by viewModel.editingTopic.collectAsStateWithLifecycle()
                    val currentTopicId by viewModel.currentTopicId.collectAsStateWithLifecycle()
                    val allTopics by viewModel.allTopics.collectAsStateWithLifecycle()
                    val sessionActive by viewModel.sessionActive.collectAsStateWithLifecycle()
                    TimerSettings(
                        settingsState = settingsState,
                        topics = allTopics,
                        editingTopic = editingTopic,
                        currentTopicId = currentTopicId,
                        sessionActive = sessionActive,
                        contentPadding = contentPadding,
                        minuteInputs = viewModel.minuteInputs,
                        sessionsSliderState = viewModel.sessionsSliderState,
                        onAction = onAction,
                        onBack = backStack::onBack
                    )
                }
                entry<Screen.Settings.Topics> {
                    val editingTopic by viewModel.editingTopic.collectAsStateWithLifecycle()
                    val currentTopicId by viewModel.currentTopicId.collectAsStateWithLifecycle()
                    val allTopics by viewModel.allTopics.collectAsStateWithLifecycle()
                    val sessionActive by viewModel.sessionActive.collectAsStateWithLifecycle()
                    TopicsSettings(
                        topics = allTopics,
                        editingTopic = editingTopic,
                        sessionActive = sessionActive,
                        currentTopicId = currentTopicId,
                        minuteInputs = viewModel.minuteInputs,
                        sessionsSliderState = viewModel.sessionsSliderState,
                        contentPadding = contentPadding,
                        onAction = onAction,
                        onBack = backStack::onBack
                    )
                }
                entry<Screen.Settings.About> {
                    AboutScreen(
                        contentPadding = contentPadding,
                        isPlus = isPlus,
                        onBack = backStack::onBack
                    )
                }
            }
        )
    }

    if (!widthExpanded) {
        SettingsNavDisplay(isTwoPane = false)
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
                    SettingsMainScreen(
                        settingsState = settingsState,
                        contentPadding = contentPadding,
                        isPlus = isPlus,
                        onResetDataClick = { onAction(SettingsAction.AskEraseData) },
                        onNavigate = navigateToSubScreen
                    )
                }
            },
            detailPane = {
                AnimatedPane {
                    SettingsNavDisplay(isTwoPane = true)
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
