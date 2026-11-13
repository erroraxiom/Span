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

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.motionScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.erroraxiom.span.ui.theme.SpanTheme
import com.erroraxiom.span.ui.timerScreen.viewModel.TimerMode
import com.erroraxiom.span.ui.timerScreen.viewModel.TimerState
import kotlin.time.Duration.Companion.milliseconds
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow

@Composable
fun AlwaysOnDisplay(
    timerState: TimerState,
    secureAod: Boolean,
    progressFlow: StateFlow<Float>,
    setTimerFrequency: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val progressState = progressFlow.collectAsStateWithLifecycle()
    val progress = remember(progressState) { { progressState.value } }

    var sharedElementTransitionComplete by remember { mutableStateOf(false) }

    val density = LocalDensity.current
    val windowInfo = LocalWindowInfo.current
    val strokePx = remember(density) { with(density) { 8.dp.toPx() } }
    val wavyStroke = remember(strokePx) { Stroke(width = strokePx, cap = StrokeCap.Round) }

    AodSystemBarsHandler(density, windowInfo, secureAod, setTimerFrequency)

    LaunchedEffect(Unit) {
        delay(300.milliseconds)
        sharedElementTransitionComplete = true
    }

    val sharedElementFraction by animateFloatAsState(
        if (sharedElementTransitionComplete) 1f else 0f,
        animationSpec = motionScheme.slowEffectsSpec()
    )
    val focusFraction by animateFloatAsState(
        if (timerState.timerMode == TimerMode.FOCUS) 0f else 1f,
        animationSpec = motionScheme.slowEffectsSpec()
    )

    val primary = lerp(
        lerp(colorScheme.primary, colorScheme.tertiary, focusFraction),
        Color(0xFFA2A2A2),
        sharedElementFraction
    )
    val secondaryContainer = lerp(
        lerp(colorScheme.secondaryContainer, colorScheme.tertiaryContainer, focusFraction),
        Color(0xFF1D1D1D),
        sharedElementFraction
    )
    val surface = lerp(colorScheme.surface, Color.Black, sharedElementFraction)
    val onSurface = lerp(colorScheme.onSurface, Color(0xFFE3E3E3), sharedElementFraction)

    val elementSize = 250.dp.toIntPx(density)
    val margin = 16.dp.toIntPx(density)

    var x by remember(windowInfo.containerSize) {
        mutableIntStateOf(windowInfo.containerSize.width / 2 - elementSize / 2)
    }
    var y by remember(windowInfo.containerSize) {
        mutableIntStateOf(windowInfo.containerSize.height / 2 - elementSize / 2)
    }

    var xIncrement by remember { mutableIntStateOf(2) }
    var yIncrement by remember { mutableIntStateOf(2) }

    LaunchedEffect(timerState.timeStr.getOrNull(1)) {
        if (sharedElementTransitionComplete && windowInfo.containerSize.width > 0 && windowInfo.containerSize.height > 0) {
            if (windowInfo.containerSize.width - elementSize - margin < x + xIncrement || x + xIncrement < margin)
                xIncrement = -xIncrement
            if (windowInfo.containerSize.height - elementSize - margin < y + yIncrement || y + yIncrement < margin)
                yIncrement = -yIncrement
            x += xIncrement
            y += yIncrement
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(surface)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(250.dp)
                .offset {
                    IntOffset(x, y)
                }
        ) {
            if (!timerState.infiniteFocus) {
                if (timerState.timerMode == TimerMode.FOCUS) {
                    CircularProgressIndicator(
                        progress = progress,
                        modifier = Modifier.size(250.dp),
                        color = primary,
                        trackColor = secondaryContainer,
                        strokeWidth = 8.dp,
                        gapSize = 4.dp,
                    )
                } else {
                    CircularWavyProgressIndicator(
                        progress = progress,
                        modifier = Modifier.size(250.dp),
                        color = primary,
                        trackColor = secondaryContainer,
                        stroke = wavyStroke,
                        trackStroke = wavyStroke,
                        wavelength = 42.dp,
                        gapSize = 4.dp
                    )
                }
            } else {
                Box(modifier = Modifier.size(250.dp))
            }

            val clockFontSize = with(density) {
                if (!timerState.infiniteFocus) {
                    if (timerState.timeStr.length < 6) 44.dp.toSp() else 34.dp.toSp()
                } else {
                    58.dp.toSp()
                }
            }

            Text(
                text = timerState.timeStr,
                style = TextStyle(
                    fontFamily = typography.bodyMedium.fontFamily,
                    fontSize = clockFontSize,
                    letterSpacing = (-1).sp,
                    fontFeatureSettings = "tnum"
                ),
                textAlign = TextAlign.Center,
                color = onSurface,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}

@Preview
@Composable
private fun AlwaysOnDisplayPreview() {
    val timerState = TimerState()
    SpanTheme {
        AlwaysOnDisplay(
            timerState = timerState,
            secureAod = true,
            progressFlow = remember { kotlinx.coroutines.flow.MutableStateFlow(0.5f) },
            setTimerFrequency = {}
        )
    }
}

fun Dp.toIntPx(density: Density) = with(density) { toPx().toInt() }
