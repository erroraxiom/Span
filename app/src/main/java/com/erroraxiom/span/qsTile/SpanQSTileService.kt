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

package com.erroraxiom.span.qsTile

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import org.koin.android.ext.android.get
import com.erroraxiom.span.R
import com.erroraxiom.span.data.StateRepository
import com.erroraxiom.span.service.AndroidTimerHelper
import com.erroraxiom.span.ui.timerScreen.viewModel.TimerAction
import com.erroraxiom.span.ui.timerScreen.viewModel.TimerMode
import com.erroraxiom.span.utils.androidSdkVersionAtLeast

class SpanQSTileService : TileService() {
    override fun onTileAdded() {
        updateTile()
    }

    override fun onStartListening() {
        updateTile()
    }

    override fun onClick() {
        super.onClick()

        val stateRepository: StateRepository = get()
        val timerRunning = stateRepository.timerState.value.timerRunning

        val serviceHelper: AndroidTimerHelper = get()
        serviceHelper.onAction(TimerAction.ToggleTimer)

        val tile = qsTile ?: return

        tile.state = if (timerRunning) Tile.STATE_INACTIVE else Tile.STATE_ACTIVE

        if (androidSdkVersionAtLeast(29))
            tile.subtitle =
                when (stateRepository.timerState.value.timerMode) {
                    TimerMode.SHORT_BREAK -> getString(R.string.short_break)
                    TimerMode.LONG_BREAK -> getString(R.string.long_break)
                    else -> getString(R.string.focus)
                }

        tile.updateTile()
    }

    private fun updateTile() {
        val stateRepository: StateRepository = get()
        val tile = qsTile ?: return

        tile.state =
            if (stateRepository.timerState.value.timerRunning) Tile.STATE_ACTIVE
            else Tile.STATE_INACTIVE

        if (androidSdkVersionAtLeast(29))
            tile.subtitle =
                when (stateRepository.timerState.value.timerMode) {
                    TimerMode.SHORT_BREAK -> getString(R.string.short_break)
                    TimerMode.LONG_BREAK -> getString(R.string.long_break)
                    else -> getString(R.string.focus)
                }

        tile.updateTile()
    }
}