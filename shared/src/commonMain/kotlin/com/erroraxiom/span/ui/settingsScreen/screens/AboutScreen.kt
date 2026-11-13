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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import com.erroraxiom.span.BuildKonfig
import com.erroraxiom.span.ui.mergePaddingValues
import com.erroraxiom.span.ui.settingsScreen.components.LicenseBottomSheet
import com.erroraxiom.span.ui.theme.CustomColors.detailPaneTopBarColors
import com.erroraxiom.span.ui.theme.CustomColors.listItemColors
import com.erroraxiom.span.ui.theme.CustomColors.topBarColors
import com.erroraxiom.span.ui.theme.LocalAppFonts
import com.erroraxiom.span.ui.theme.SpanShapeDefaults.PANE_MAX_WIDTH
import com.erroraxiom.span.ui.theme.SpanShapeDefaults.cardShape
import com.erroraxiom.span.ui.theme.SpanTheme
import com.erroraxiom.span.ui.topBarWindowInsets
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import span.shared.generated.resources.Res
import span.shared.generated.resources.about
import span.shared.generated.resources.app_name
import span.shared.generated.resources.arrow_back
import span.shared.generated.resources.back
import span.shared.generated.resources.email
import span.shared.generated.resources.github
import span.shared.generated.resources.instagram
import span.shared.generated.resources.settings
import span.shared.generated.resources.span_logo_notification

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AboutScreen(
    contentPadding: PaddingValues,
    isPlus: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fonts = LocalAppFonts.current
    val uriHandler = LocalUriHandler.current
    var showLicenseSheet by remember { mutableStateOf(false) }

    val widthExpanded = currentWindowAdaptiveInfoV2()
        .windowSizeClass
        .isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND)

    val barColors = if (widthExpanded) detailPaneTopBarColors else topBarColors

    val sunnyShape = MaterialShapes.Sunny.toShape()

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(barColors.containerColor)
    ) {
        Scaffold(
            topBar = {
                LargeFlexibleTopAppBar(
                    windowInsets = topBarWindowInsets(),
                    title = {
                        Text(
                            stringResource(Res.string.about),
                            fontFamily = fonts.topBarTitle
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
                    colors = barColors
                )
            },
            containerColor = barColors.containerColor,
            modifier = Modifier
                .widthIn(max = PANE_MAX_WIDTH)
                .fillMaxSize()
        ) { innerPadding ->
            val insets = mergePaddingValues(innerPadding, contentPadding)
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = insets,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                item(key = "top_spacer") { Spacer(Modifier.height(8.dp)) }

                // Hero App Card with SVG logo
                item(key = "hero_card") {
                    Card(
                        shape = cardShape,
                        colors = CardDefaults.cardColors(containerColor = listItemColors.containerColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(84.dp)
                                    .background(
                                        colorScheme.primaryContainer,
                                        sunnyShape
                                    )
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.span_logo_notification),
                                    contentDescription = stringResource(Res.string.app_name),
                                    tint = colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(44.dp)
                                )
                            }

                            Text(
                                text = stringResource(Res.string.app_name),
                                style = typography.headlineLarge.copy(
                                    fontFamily = fonts.topBarTitle,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = colorScheme.onSurface
                            )

                            Surface(
                                shape = CircleShape,
                                color = colorScheme.secondaryContainer,
                                contentColor = colorScheme.onSecondaryContainer
                            ) {
                                Text(
                                    text = "v${BuildKonfig.VERSION_NAME}",
                                    style = typography.labelLarge,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                                )
                            }

                            Text(
                                text = "An expressive Material 3 focus & Pomodoro timer.",
                                style = typography.bodyMedium,
                                color = colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // 1. License Card
                item(key = "license_card") {
                    Card(
                        shape = cardShape,
                        colors = CardDefaults.cardColors(containerColor = listItemColors.containerColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(cardShape)
                            .clickable { showLicenseSheet = true }
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 18.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = "License",
                                    style = typography.labelMedium,
                                    color = colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "GNU GPL v3.0",
                                    style = typography.titleMedium,
                                    color = colorScheme.onSurface
                                )
                            }

                            Surface(
                                shape = CircleShape,
                                color = colorScheme.surfaceVariant,
                                contentColor = colorScheme.onSurfaceVariant
                            ) {
                                Text(
                                    text = "Open Source",
                                    style = typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                // 2. Developer Card with 3 contact pill icons
                item(key = "developer_card") {
                    Card(
                        shape = cardShape,
                        colors = CardDefaults.cardColors(containerColor = listItemColors.containerColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 18.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = "Developer",
                                        style = typography.labelMedium,
                                        color = colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Erroraxiom",
                                        style = typography.titleMedium,
                                        color = colorScheme.onSurface
                                    )
                                }

                                Surface(
                                    shape = CircleShape,
                                    color = colorScheme.primaryContainer,
                                    contentColor = colorScheme.onPrimaryContainer
                                ) {
                                    Text(
                                        text = "@erroraxiom",
                                        style = typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }

                            // 3 Contact Pill Icons: Instagram, Mail, GitHub
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // 1. Instagram Pill
                                Surface(
                                    onClick = { uriHandler.openUri("https://www.instagram.com/erroraxiom") },
                                    shape = CircleShape,
                                    color = colorScheme.secondaryContainer,
                                    contentColor = colorScheme.onSecondaryContainer,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(Res.drawable.instagram),
                                            contentDescription = "Instagram",
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(Modifier.size(6.dp))
                                        Text(
                                            text = "Insta",
                                            style = typography.labelMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                // 2. Gmail / Mail Pill
                                Surface(
                                    onClick = { uriHandler.openUri("mailto:erroraxiom.dev@gmail.com") },
                                    shape = CircleShape,
                                    color = colorScheme.tertiaryContainer,
                                    contentColor = colorScheme.onTertiaryContainer,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(Res.drawable.email),
                                            contentDescription = "Email",
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(Modifier.size(6.dp))
                                        Text(
                                            text = "Email",
                                            style = typography.labelMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                // 3. GitHub Pill
                                Surface(
                                    onClick = { uriHandler.openUri("https://github.com/erroraxiom") },
                                    shape = CircleShape,
                                    color = colorScheme.surfaceVariant,
                                    contentColor = colorScheme.onSurfaceVariant,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(Res.drawable.github),
                                            contentDescription = "GitHub",
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(Modifier.size(6.dp))
                                        Text(
                                            text = "GitHub",
                                            style = typography.labelMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item(key = "bottom_spacer") { Spacer(Modifier.height(16.dp)) }
            }
        }

        if (showLicenseSheet) {
            LicenseBottomSheet(setShowSheet = { showLicenseSheet = it })
        }
    }
}

@Preview
@Composable
private fun AboutScreenPreview() {
    SpanTheme {
        AboutScreen(
            contentPadding = PaddingValues(),
            isPlus = true,
            onBack = {}
        )
    }
}
