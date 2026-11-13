/*
 * Copyright (c) 2025 Erroraxiom
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

package com.erroraxiom.span

import org.koin.dsl.module
import com.erroraxiom.span.billing.BillingManager
import com.erroraxiom.span.billing.FossBillingManager
import com.erroraxiom.span.di.FlavorUI
import com.erroraxiom.span.ui.settingsScreen.components.BottomButton
import com.erroraxiom.span.ui.settingsScreen.components.TopButton

val flavorModule = module {
    single<BillingManager> { FossBillingManager() }
}

val flavorUiModule = module {
    single {
        FlavorUI(
            topButton = ::TopButton,
            bottomButton = ::BottomButton
        )
    }
}