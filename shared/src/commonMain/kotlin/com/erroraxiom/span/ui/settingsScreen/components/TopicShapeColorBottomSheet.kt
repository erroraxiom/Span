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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.erroraxiom.span.data.Topic
import com.erroraxiom.span.ui.settingsScreen.viewModel.SettingsAction
import com.erroraxiom.span.ui.theme.SeededTheme
import com.erroraxiom.span.ui.theme.SpanTheme

/**
 * A bottom sheet that lets the user edit the name, the color and the shape of [topic].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicShapeColorBottomSheet(
    topic: Topic,
    topics: List<Topic>,
    setShowSheet: (Boolean) -> Unit,
    onAction: (SettingsAction) -> Unit,
    modifier: Modifier = Modifier
) {
    SeededTheme(topic.color) {
        val colorScheme = MaterialTheme.colorScheme

        var name by remember { mutableStateOf(topic.name) }
        val nameTaken = remember(name, topics, topic.id) {
            val trimmedName = name.trim()
            topics.any { it.id != topic.id && it.name.equals(trimmedName, true) }
        }

        ModalBottomSheet(
            sheetState = rememberBottomSheetState(
                initialValue = SheetValue.Hidden,
                enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded)
            ),
            onDismissRequest = { setShowSheet(false) },
            containerColor = colorScheme.surfaceContainer,
            modifier = modifier
        ) {
            TopicShapeColorPicker(
                name = name,
                onNameValueChange = { name = it },
                color = topic.color,
                shape = topic.shape,
                nameTaken = nameTaken,
                onNameChange = { onAction(SettingsAction.SetEditingTopicName(it)) },
                onColorChange = { onAction(SettingsAction.SetEditingTopicColor(it)) },
                onShapeChange = { onAction(SettingsAction.SetEditingTopicShape(it)) },
                containerColor = colorScheme.surfaceContainer,
                modifier = Modifier
                    .heightIn(max = maxSheetHeight())
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 16.dp)
            )
        }
    }
}

@Preview
@Composable
private fun TopicShapeColorBottomSheetPreview() {
    var topic by remember { mutableStateOf(Topic.defaultTopic) }
    SpanTheme(dynamicColor = false) {
        Surface(Modifier.fillMaxSize()) {
            TopicShapeColorBottomSheet(
                topic = topic,
                topics = listOf(topic),
                setShowSheet = {},
                onAction = { action ->
                    when (action) {
                        is SettingsAction.SetEditingTopicName -> topic =
                            topic.copy(name = action.name)

                        is SettingsAction.SetEditingTopicShape -> topic =
                            topic.copy(shape = action.shape)

                        is SettingsAction.SetEditingTopicColor -> topic =
                            topic.copy(color = action.color)

                        else -> Unit
                    }
                }
            )
        }
    }
}
