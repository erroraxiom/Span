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

package com.erroraxiom.span.ui.settingsScreen.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import com.erroraxiom.span.ui.settingsScreen.components.LocaleBottomSheet
import com.erroraxiom.span.ui.theme.CustomColors.listItemColors
import com.erroraxiom.span.ui.theme.SpanShapeDefaults.segmentedListItemShapes
import com.erroraxiom.span.utils.androidDeviceManufacturerIs
import com.erroraxiom.span.utils.androidSdkVersionAtLeast
import span.shared.generated.resources.Res
import span.shared.generated.resources.language
import span.shared.generated.resources.mobile_text
import span.shared.generated.resources.now_bar
import span.shared.generated.resources.open_in_browser

@Composable
actual fun PlatformSettings() {
    val currentLocale =
        if (androidSdkVersionAtLeast(33)) {
            LocalLocale.current
        } else null

    var showLocaleSheet by remember { mutableStateOf(false) }

    if (showLocaleSheet)
        LocaleBottomSheet({ showLocaleSheet = it })

    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        if (currentLocale != null)
            SegmentedListItem(
                leadingContent = {
                    Icon(painterResource(Res.drawable.language), contentDescription = null)
                },
                supportingContent = {
                    Text(currentLocale.platformLocale.displayLanguage)
                },
                selected = showLocaleSheet,
                shapes = segmentedListItemShapes(0, 1),
                colors = listItemColors,
                onClick = { showLocaleSheet = true }
            ) { Text(stringResource(Res.string.language)) }

        if (androidSdkVersionAtLeast(36) && androidDeviceManufacturerIs("samsung")) {
            val uriHandler = LocalUriHandler.current
            Spacer(Modifier.height(14.dp))
            SegmentedListItem(
                leadingContent = {
                    Icon(painterResource(Res.drawable.mobile_text), null)
                },
                trailingContent = {
                    Icon(painterResource(Res.drawable.open_in_browser), null)
                },
                shapes = segmentedListItemShapes(0, 1),
                colors = listItemColors,
                onClick = { uriHandler.openUri("https://gist.github.com/erroraxiom/3b42969aef017d98f72b097f1eca8911") }
            ) { Text(stringResource(Res.string.now_bar)) }
        }
    }
}