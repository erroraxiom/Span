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

package com.erroraxiom.span.ui.settingsScreen.components

import android.app.LocaleConfig
import android.app.LocaleManager
import android.os.Build
import android.os.LocaleList
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import com.erroraxiom.span.ui.theme.CustomColors.listItemColors
import com.erroraxiom.span.ui.theme.SpanShapeDefaults.segmentedListItemShapes
import span.shared.generated.resources.Res
import span.shared.generated.resources.check
import span.shared.generated.resources.choose_language
import span.shared.generated.resources.selected
import span.shared.generated.resources.system_default

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun LocaleBottomSheet(
    setShowSheet: (Boolean) -> Unit,
    modifier: Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val currentLocales =
        if (Build.VERSION.SDK_INT >= 33) {
            context
                .getSystemService(LocaleManager::class.java)
                .applicationLocales
        } else LocaleList.getEmptyLocaleList()

    val supportedLocales = remember {
        if (Build.VERSION.SDK_INT >= 33) {
            LocaleConfig(context).supportedLocales
        } else null
    }
    val supportedLocalesSize = supportedLocales?.size() ?: 0

    val supportedLocalesList: List<AppLocale>? = remember {
        if (supportedLocales != null) {
            buildList {
                for (i in 0 until supportedLocalesSize) {
                    add(AppLocale(supportedLocales.get(i), supportedLocales.get(i).displayName))
                }
                sortWith(compareBy { it.name })
            }
        } else null
    }

    val bottomSheetState = rememberBottomSheetState(initialValue = SheetValue.Hidden)
    val listState = rememberLazyListState()

    ModalBottomSheet(
        onDismissRequest = { setShowSheet(false) },
        sheetState = bottomSheetState,
        modifier = modifier
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(Res.string.choose_language),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            if (supportedLocalesList != null) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    state = listState,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .clip(shapes.large)
                ) {
                    item {
                        SegmentedListItem(
                            onClick = {
                                scope
                                    .launch { bottomSheetState.hide() }
                                    .invokeOnCompletion {
                                        if (Build.VERSION.SDK_INT >= 33) {
                                            context
                                                .getSystemService(LocaleManager::class.java)
                                                .applicationLocales = LocaleList()
                                        }
                                        setShowSheet(false)
                                    }
                            },
                            content = {
                                Text(stringResource(Res.string.system_default))
                            },
                            trailingContent = {
                                if (currentLocales.isEmpty)
                                    Icon(
                                        painterResource(Res.drawable.check),
                                        contentDescription = stringResource(Res.string.selected)
                                    )
                            },
                            colors = listItemColors,
                            selected = currentLocales.isEmpty,
                            shapes = segmentedListItemShapes(0, 1)
                        )
                    }
                    item {
                        Spacer(Modifier.height(12.dp))
                    }
                    itemsIndexed(
                        supportedLocalesList,
                        key = { _: Int, it: AppLocale -> it.name }
                    ) { index, it ->
                        val selected = !currentLocales.isEmpty && it.locale == currentLocales.get(0)

                        SegmentedListItem(
                            onClick = {
                                scope
                                    .launch { bottomSheetState.hide() }
                                    .invokeOnCompletion { _ ->
                                        if (Build.VERSION.SDK_INT >= 33) {
                                            context.getSystemService(LocaleManager::class.java)
                                                .applicationLocales =
                                                LocaleList(it.locale)
                                        }
                                        setShowSheet(false)
                                    }
                            },
                            content = { Text(it.name) },
                            trailingContent = {
                                if (selected)
                                    Icon(
                                        painterResource(Res.drawable.check),
                                        tint = colorScheme.primary,
                                        contentDescription = stringResource(Res.string.selected)
                                    )
                            },
                            colors = listItemColors,
                            selected = selected,
                            shapes = segmentedListItemShapes(index, supportedLocalesSize)
                        )
                    }
                }
            }
        }
    }
}
