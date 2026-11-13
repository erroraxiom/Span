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

package com.erroraxiom.span.ui.timerScreen.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.erroraxiom.span.data.Stat
import com.erroraxiom.span.data.StatRepository
import com.erroraxiom.span.data.StateRepository
import com.erroraxiom.span.data.TopicRepository
import com.erroraxiom.span.service.TimerHelper
import com.erroraxiom.span.ui.Screen
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.time.Duration.Companion.milliseconds

import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@OptIn(FlowPreview::class)
class TimerViewModel(
    private val timerHelper: TimerHelper,
    private val stateRepository: StateRepository,
    private val statRepository: StatRepository,
    private val topicRepository: TopicRepository
) : ViewModel() {
    val rootBackstack = mutableStateListOf<Screen>(Screen.Timer)

    private val _time: MutableStateFlow<Long> = stateRepository.time
    val timerState: StateFlow<TimerState> = stateRepository.timerState.asStateFlow()

    val currentTopic = stateRepository.currentTopic

    val timerMode: StateFlow<TimerMode> = timerState
        .map { it.timerMode }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TimerMode.FOCUS)

    val alarmRinging: StateFlow<Boolean> = timerState
        .map { it.alarmRinging }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val progress = _time.combine(stateRepository.timerState) { remainingTime, uiState ->
        (uiState.totalTime.toFloat() - remainingTime) / uiState.totalTime
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val topicIds = topicRepository.getTopicIds()
            var lastDate = statRepository.getLastDate()
            val today = LocalDate.now()

            // Fills dates between today and lastDate with 0s to ensure continuous history
            if (lastDate != null) {
                while (ChronoUnit.DAYS.between(lastDate, today) > 0) {
                    lastDate = lastDate?.plusDays(1)
                    topicIds.forEach { topicId ->
                        statRepository.insertStat(Stat(lastDate!!, topicId, 0, 0, 0, 0, 0))
                    }
                }
            } else {
                topicIds.forEach { topicId ->
                    statRepository.insertStat(Stat(today, topicId, 0, 0, 0, 0, 0))
                }
            }

            delay(1500.milliseconds)

            stateRepository.timerState.update { currentState ->
                currentState.copy(showBrandTitle = false)
            }
        }
    }

    fun onAction(action: TimerAction) {
        when (action) {
            is TimerAction.SetInfiniteFocus -> {
                stateRepository.timerState.update {
                    it.copy(
                        infiniteFocus = action.value
                    )
                }
                onAction(TimerAction.ResetTimer)
            }

            is TimerAction.SetTopic -> {
                if (!timerState.value.sessionActive) {
                    viewModelScope.launch {
                        stateRepository.setTopic(action.topic)
                        onAction(TimerAction.ResetTimer)
                    }
                }
            }

            else -> timerHelper.onAction(action)
        }
    }
}