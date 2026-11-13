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

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.motionScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.erroraxiom.span.ui.theme.CustomColors.listItemColors
import com.erroraxiom.span.ui.theme.LocalAppFonts

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MinuteInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    maxDigits: Int = 2,
    shape: Shape = shapes.largeIncreased,
    containerColor: Color = listItemColors.containerColor,
    imeAction: ImeAction = ImeAction.Next
) {
    var focused by remember { mutableStateOf(false) }
    val bgColor by animateColorAsState(
        if (focused) colorScheme.primaryContainer
        else containerColor,
        motionScheme.defaultEffectsSpec()
    )
    val contentColor by animateColorAsState(
        if (!enabled) listItemColors.disabledContentColor
        else if (focused) colorScheme.onPrimaryContainer
        else listItemColors.contentColor,
        motionScheme.defaultEffectsSpec()
    )

    BasicTextField(
        value = value,
        onValueChange = { newValue ->
            if (newValue.isMinutesInput(maxDigits)) {
                onValueChange(newValue)
            }
        },
        enabled = enabled,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword,
            imeAction = imeAction
        ),
        textStyle = TextStyle(
            fontFamily = LocalAppFonts.current.topBarTitle,
            fontSize = typography.displayLarge.fontSize,
            lineHeight = typography.displayLarge.lineHeight,
            letterSpacing = typography.displayLarge.letterSpacing,
            color = contentColor,
            textAlign = TextAlign.Center
        ),
        cursorBrush = SolidColor(contentColor),
        decorationBox = { innerTextField ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier
                    .size(112.dp, 100.dp)
                    .background(bgColor, shape)
            ) {
                innerTextField()
            }
        },
        modifier = Modifier.onFocusChanged {
            focused = it.isFocused
        }
    )
}
