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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.erroraxiom.span.ui.performConfirm
import com.erroraxiom.span.ui.theme.SpanTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import span.shared.generated.resources.Res
import span.shared.generated.resources.cancel
import span.shared.generated.resources.delete
import span.shared.generated.resources.delete_topic
import span.shared.generated.resources.delete_topic_and_stats
import span.shared.generated.resources.delete_topic_dialog_text
import span.shared.generated.resources.delete_topic_dialog_title

/**
 * Asks the user to confirm deleting a topic, and whether its stats should go with it
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DeleteTopicDialog(
    defaultTopicName: String,
    onDismiss: () -> Unit,
    onDeleteTopic: () -> Unit,
    onDeleteTopicAndStats: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = AlertDialogDefaults.shape,
            tonalElevation = AlertDialogDefaults.TonalElevation,
            color = AlertDialogDefaults.containerColor
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            colorScheme.errorContainer,
                            MaterialShapes.Cookie9Sided.toShape()
                        )
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.delete),
                        contentDescription = null,
                        tint = colorScheme.onErrorContainer
                    )
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    text = stringResource(Res.string.delete_topic_dialog_title),
                    textAlign = TextAlign.Center,
                    style = typography.headlineSmall,
                    color = AlertDialogDefaults.titleContentColor
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = stringResource(
                        Res.string.delete_topic_dialog_text,
                        defaultTopicName
                    ),
                    style = typography.bodyMedium,
                    color = AlertDialogDefaults.textContentColor,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(24.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FilledTonalButton(
                        onClick = {
                            haptic.performConfirm()
                            onDeleteTopic()
                        },
                        shapes = ButtonDefaults.shapes(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(Res.string.delete_topic))
                    }

                    Button(
                        onClick = {
                            haptic.performConfirm()
                            onDeleteTopicAndStats()
                        },
                        shapes = ButtonDefaults.shapes(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.error,
                            contentColor = colorScheme.onError
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(Res.string.delete_topic_and_stats))
                    }

                    TextButton(
                        onClick = onDismiss,
                        shapes = ButtonDefaults.shapes(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(Res.string.cancel))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun DeleteTopicDialogPreview() {
    SpanTheme {
        Surface {
            DeleteTopicDialog(
                defaultTopicName = "Default",
                onDismiss = {},
                onDeleteTopic = {},
                onDeleteTopicAndStats = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
