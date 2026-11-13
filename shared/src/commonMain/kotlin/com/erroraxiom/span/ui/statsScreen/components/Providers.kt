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

package com.erroraxiom.span.ui.statsScreen.components

import com.patrykandpatrick.vico.compose.cartesian.data.ColumnCartesianLayerModel
import com.patrykandpatrick.vico.compose.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.compose.common.component.LineComponent
import com.patrykandpatrick.vico.compose.common.data.ExtraStore

class Providers {
    companion object {
        private data class ColumnProviderWithLimit(
            private val limit: Double,
            private val belowLimitComponent: LineComponent,
            private val aboveLimitComponent: LineComponent
        ) : ColumnCartesianLayer.ColumnProvider {
            override fun getColumn(
                entry: ColumnCartesianLayerModel.Entry,
                extraStore: ExtraStore,
            ): LineComponent {
                return if (entry.y < limit) belowLimitComponent
                else aboveLimitComponent
            }

            override fun getWidestSeriesColumn(
                seriesKey: Any,
                seriesIndex: Int,
                extraStore: ExtraStore
            ): LineComponent {
                return if (belowLimitComponent.thickness > aboveLimitComponent.thickness) belowLimitComponent
                else aboveLimitComponent
            }
        }

        fun columnProviderWithLimit(
            limit: Number,
            belowLimitComponent: LineComponent,
            aboveLimitComponent: LineComponent
        ): ColumnCartesianLayer.ColumnProvider =
            ColumnProviderWithLimit(limit.toDouble(), belowLimitComponent, aboveLimitComponent)
    }
}
