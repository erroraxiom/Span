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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.erroraxiom.span.ui.performSegmentFrequentTick
import com.erroraxiom.span.ui.performSegmentTick
import com.erroraxiom.span.ui.theme.CustomColors.listItemColors
import com.erroraxiom.span.ui.theme.SpanShapeDefaults.middleListItemShape
import kotlinx.coroutines.flow.drop
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SliderListItem(
    state: SliderState,
    enabled: Boolean,
    label: String,
    trailingLabel: @Composable (Float) -> String,
    trailingIcon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = middleListItemShape,
    colors: ListItemColors = listItemColors
) {
    val haptic = LocalHapticFeedback.current
    var lastHapticStep by remember { mutableIntStateOf(state.value.roundToInt()) }

    LaunchedEffect(state) {
        snapshotFlow { state.value }
            .drop(1)
            .collect { value ->
                if (state.steps > 0) {
                    val currentStep = value.roundToInt()
                    if (currentStep != lastHapticStep) {
                        haptic.performSegmentTick()
                        lastHapticStep = currentStep
                    }
                } else {
                    val currentStep = (value * 100).roundToInt()
                    if (currentStep != lastHapticStep) {
                        haptic.performSegmentFrequentTick()
                        lastHapticStep = currentStep
                    }
                }
            }
    }

    ListItem(
        leadingContent = {
            trailingIcon()
        },
        headlineContent = { Text(label) },
        supportingContent = {
            Column {
                Slider(
                    state = state,
                    enabled = enabled,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        },
        trailingContent = {
            Text(trailingLabel(state.value))
        },
        colors = colors,
        modifier = modifier.clip(shape)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SliderListItem(
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    enabled: Boolean,
    label: String,
    trailingLabel: @Composable (Float) -> String,
    trailingIcon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    steps: Int = 0,
    shape: Shape = middleListItemShape,
    colors: ListItemColors = listItemColors,
    onValueChangeFinished: (Float) -> Unit
) {
    val state = rememberSliderState(
        value = value,
        steps = steps,
        valueRange = valueRange
    )
    LaunchedEffect(value) {
        if (state.value != value) {
            state.value = value
        }
    }
    state.onValueChangeFinished = {
        onValueChangeFinished(state.value)
    }

    SliderListItem(
        state = state,
        enabled = enabled,
        label = label,
        trailingLabel = trailingLabel,
        trailingIcon = trailingIcon,
        modifier = modifier,
        shape = shape,
        colors = colors
    )
}