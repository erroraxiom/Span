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

import androidx.compose.foundation.background
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
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import com.erroraxiom.span.ui.mergePaddingValues
import com.erroraxiom.span.ui.performToggle
import com.erroraxiom.span.ui.settingsScreen.SettingsSwitchItem
import com.erroraxiom.span.ui.settingsScreen.components.ColorSchemePickerListItem
import com.erroraxiom.span.ui.settingsScreen.components.ThemePickerListItem
import com.erroraxiom.span.ui.settingsScreen.viewModel.SettingsAction
import com.erroraxiom.span.ui.settingsScreen.viewModel.SettingsState
import com.erroraxiom.span.ui.theme.CustomColors.detailPaneTopBarColors
import com.erroraxiom.span.ui.theme.CustomColors.listItemColors
import com.erroraxiom.span.ui.theme.CustomColors.switchColors
import com.erroraxiom.span.ui.theme.CustomColors.topBarColors
import com.erroraxiom.span.ui.theme.LocalAppFonts
import com.erroraxiom.span.ui.theme.SpanShapeDefaults.PANE_MAX_WIDTH
import com.erroraxiom.span.ui.theme.SpanShapeDefaults.segmentedListItemShapes
import com.erroraxiom.span.ui.theme.SpanTheme
import com.erroraxiom.span.ui.topBarWindowInsets
import span.shared.generated.resources.Res
import span.shared.generated.resources.appearance
import span.shared.generated.resources.arrow_back
import span.shared.generated.resources.back
import span.shared.generated.resources.black_theme
import span.shared.generated.resources.black_theme_desc
import span.shared.generated.resources.check
import span.shared.generated.resources.clear
import span.shared.generated.resources.contrast
import span.shared.generated.resources.settings

@Composable
fun AppearanceSettings(
    settingsState: SettingsState,
    contentPadding: PaddingValues,
    onAction: (SettingsAction) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

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
                            stringResource(Res.string.appearance),
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
                    Spacer(Modifier.height(14.dp))
                }
                item(key = "theme_picker") {
                    ThemePickerListItem(
                        theme = settingsState.theme,
                        onThemeChange = { onAction(SettingsAction.SaveTheme(it)) },
                        items = 3,
                        index = 0
                    )
                }

                item(key = "color_picker") {
                    ColorSchemePickerListItem(
                        color = settingsState.colorScheme,
                        items = 3,
                        index = 1,
                        onColorChange = { onAction(SettingsAction.SaveColorScheme(it)) },
                    )
                }
                item(key = "black_theme") {
                    val item = SettingsSwitchItem(
                        checked = settingsState.blackTheme,
                        icon = Res.drawable.contrast,
                        label = Res.string.black_theme,
                        description = Res.string.black_theme_desc,
                        onClick = { onAction(SettingsAction.SaveBlackTheme(it)) }
                    )
                    SegmentedListItem(
                        onClick = {
                            haptic.performToggle(!item.checked)
                            item.onClick(!item.checked)
                        },
                        leadingContent = {
                            Icon(painterResource(item.icon), contentDescription = null)
                        },
                        content = { Text(stringResource(item.label)) },
                        supportingContent = { Text(stringResource(item.description)) },
                        trailingContent = {
                            Switch(
                                checked = item.checked,
                                onCheckedChange = {
                                    haptic.performToggle(it)
                                    item.onClick(it)
                                },
                                enabled = true,
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
                        enabled = true,
                        shapes = segmentedListItemShapes(2, 3)
                    )
                }

                item(key = "bottom_spacer") { Spacer(Modifier.height(12.dp)) }
            }
        }
    }
}

@Preview
@Composable
fun AppearanceSettingsPreview() {
    val settingsState = SettingsState()
    SpanTheme(dynamicColor = false) {
        AppearanceSettings(
            settingsState = settingsState,
            contentPadding = PaddingValues(),
            onAction = {},
            onBack = {}
        )
    }
}
