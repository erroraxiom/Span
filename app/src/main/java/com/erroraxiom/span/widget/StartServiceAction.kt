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

package com.erroraxiom.span.widget

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.erroraxiom.span.service.TimerService

class StartServiceAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val timerAction = parameters[key] as TimerService.Actions

        val serviceIntent = Intent(context, TimerService::class.java).apply {
            action = timerAction.toString()
        }

        try {
            context.startForegroundService(serviceIntent)
        } catch (e: Exception) {
            Log.e("StartServiceAction", "Cannot start service: ${e.message}")
            e.printStackTrace()
        }
    }

    companion object {
        val key = ActionParameters.Key<TimerService.Actions>("action")
    }
}