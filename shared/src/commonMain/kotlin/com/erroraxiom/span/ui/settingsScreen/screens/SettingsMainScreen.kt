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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastJoinToString
import androidx.compose.ui.util.fastMap
import com.erroraxiom.span.settingsScreens
import com.erroraxiom.span.ui.Screen
import com.erroraxiom.span.ui.mergePaddingValues
import com.erroraxiom.span.ui.settingsScreen.viewModel.SettingsState
import com.erroraxiom.span.ui.theme.CustomColors.listItemColors
import com.erroraxiom.span.ui.theme.CustomColors.topBarColors
import com.erroraxiom.span.ui.theme.LocalAppFonts
import com.erroraxiom.span.ui.theme.SpanShapeDefaults.PANE_MAX_WIDTH
import com.erroraxiom.span.ui.theme.SpanShapeDefaults.segmentedListItemShapes
import com.erroraxiom.span.ui.topBarWindowInsets
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import span.shared.generated.resources.Res
import span.shared.generated.resources.about
import span.shared.generated.resources.info
import span.shared.generated.resources.reset_data
import span.shared.generated.resources.restore
import span.shared.generated.resources.settings

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsMainScreen(
    settingsState: SettingsState,
    contentPadding: PaddingValues,
    isPlus: Boolean,
    onResetDataClick: () -> Unit,
    onNavigate: (Screen.Settings) -> Unit,
    modifier: Modifier = Modifier
) {
    val fonts = LocalAppFonts.current
    val cookieShape = MaterialShapes.Cookie9Sided.toShape()

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
                            stringResource(Res.string.settings),
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
            CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    contentPadding = insets,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    item(key = "top_spacer") { Spacer(Modifier.height(8.dp)) }

                    item(key = "reset_data") {
                        SegmentedListItem(
                            onClick = onResetDataClick,
                            leadingContent = {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            colorScheme.secondaryContainer,
                                            cookieShape
                                        )
                                ) {
                                    Icon(
                                        painter = painterResource(Res.drawable.restore),
                                        contentDescription = null,
                                        tint = colorScheme.onSecondaryContainer,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            },
                            content = {
                                Text(
                                    stringResource(Res.string.reset_data),
                                    style = typography.titleMedium
                                )
                            },
                            colors = listItemColors,
                            shapes = segmentedListItemShapes(0, 1)
                        )
                    }

                    item(key = "spacer_1") { Spacer(Modifier.height(12.dp)) }

                    itemsIndexed(
                        items = settingsScreens,
                        key = { index, _ -> "settings_item_$index" }
                    ) { index, item ->
                        SegmentedListItem(
                            onClick = { onNavigate(item.route) },
                            leadingContent = {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(42.dp)
                                        .background(
                                            colorScheme.primaryContainer,
                                            cookieShape
                                        )
                                ) {
                                    Icon(
                                        painter = painterResource(item.icon),
                                        contentDescription = null,
                                        tint = colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            },
                            content = {
                                Text(
                                    text = stringResource(item.label),
                                    style = typography.titleMedium
                                )
                            },
                            supportingContent = item.innerSettings?.let { innerList ->
                                {
                                    Text(
                                        text = innerList
                                            .fastMap { stringResource(it) }
                                            .fastJoinToString(", "),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            },
                            colors = listItemColors,
                            shapes = segmentedListItemShapes(index, settingsScreens.size)
                        )
                    }

                    item(key = "spacer_2") { Spacer(Modifier.height(12.dp)) }

                    item(key = "platform_settings") {
                        PlatformSettings()
                    }

                    item(key = "spacer_3") { Spacer(Modifier.height(12.dp)) }

                    item(key = "about") {
                        SegmentedListItem(
                            onClick = { onNavigate(Screen.Settings.About) },
                            leadingContent = {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            colorScheme.tertiaryContainer,
                                            cookieShape
                                        )
                                ) {
                                    Icon(
                                        painter = painterResource(Res.drawable.info),
                                        contentDescription = null,
                                        tint = colorScheme.onTertiaryContainer,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            },
                            content = {
                                Text(
                                    text = stringResource(Res.string.about),
                                    style = typography.titleMedium
                                )
                            },
                            colors = listItemColors,
                            shapes = segmentedListItemShapes(0, 1)
                        )
                    }

                    item(key = "bottom_spacer") { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}
