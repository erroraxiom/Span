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

package com.erroraxiom.span.ui.statsScreen.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter

@Composable
fun FocusBreakdownChart(
    modelProducer: CartesianChartModelProducer,
    hoursFormat: String,
    hoursMinutesFormat: String,
    minutesFormat: String,
    modifier: Modifier = Modifier
) {
    TimeColumnChart(
        modelProducer,
        hoursFormat = hoursFormat,
        hoursMinutesFormat = hoursMinutesFormat,
        minutesFormat = minutesFormat,
        xValueFormatter = remember {
            CartesianValueFormatter { _, value, _ ->
                when (value) {
                    0.0 -> "0 - 6"
                    1.0 -> "6 - 12"
                    2.0 -> "12 - 18"
                    3.0 -> "18 - 24"
                    else -> ""
                }
            }
        },
        goal = 0,
        zoomEnabled = false,
        modifier = modifier
    )
}