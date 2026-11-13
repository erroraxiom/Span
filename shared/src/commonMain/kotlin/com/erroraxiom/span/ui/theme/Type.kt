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

package com.erroraxiom.span.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font
import span.shared.generated.resources.Res
import span.shared.generated.resources.dm_sans
import span.shared.generated.resources.space_grotesk
import span.shared.generated.resources.space_mono

@Immutable
data class AppFonts(
    val display: FontFamily = FontFamily.Default,
    val topBarTitle: FontFamily = FontFamily.Default,
    val clock: FontFamily = FontFamily.Default,
    val mono: FontFamily = FontFamily.Monospace,
    val body: FontFamily = FontFamily.Default
)

val LocalAppFonts = staticCompositionLocalOf { AppFonts() }

@Composable
fun rememberAppFonts(): AppFonts {
    val spaceGroteskBold = Font(
        Res.font.space_grotesk,
        weight = FontWeight.Bold,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(700)
        )
    )
    val spaceMonoNormal = Font(Res.font.space_mono, FontWeight.Normal)
    val spaceMonoBold = Font(Res.font.space_mono, FontWeight.Bold)
    val dmSansNormal = Font(Res.font.dm_sans, FontWeight.Normal)
    val dmSansMedium = Font(Res.font.dm_sans, FontWeight.Medium)
    val dmSansSemiBold = Font(Res.font.dm_sans, FontWeight.SemiBold)
    val dmSansBold = Font(Res.font.dm_sans, FontWeight.Bold)

    return remember(
        spaceGroteskBold,
        spaceMonoNormal,
        spaceMonoBold,
        dmSansNormal,
        dmSansMedium,
        dmSansSemiBold,
        dmSansBold
    ) {
        val expressiveTitle = FontFamily(spaceGroteskBold)
        val spaceMono = FontFamily(spaceMonoNormal, spaceMonoBold)
        val dmSans = FontFamily(dmSansNormal, dmSansMedium, dmSansSemiBold, dmSansBold)
        AppFonts(
            display = expressiveTitle,
            topBarTitle = expressiveTitle,
            clock = expressiveTitle,
            mono = spaceMono,
            body = dmSans
        )
    }
}

@Composable
fun typography(appFonts: AppFonts = rememberAppFonts()): Typography {
    val defaultTypography = MaterialTheme.typography
    val expressiveTitle = appFonts.topBarTitle
    val expressiveClock = appFonts.clock
    val bodyFont = appFonts.body

    return remember(defaultTypography, appFonts) {
        Typography(
            displayLarge = defaultTypography.displayLarge.copy(
                fontFamily = expressiveClock,
                fontFeatureSettings = "tnum"
            ),
            displayMedium = defaultTypography.displayMedium.copy(
                fontFamily = expressiveTitle
            ),
            displaySmall = defaultTypography.displaySmall.copy(
                fontFamily = expressiveTitle,
                fontFeatureSettings = "tnum"
            ),
            headlineLarge = defaultTypography.headlineLarge.copy(
                fontFamily = expressiveTitle,
                fontFeatureSettings = "tnum"
            ),
            headlineMedium = defaultTypography.headlineMedium.copy(
                fontFamily = expressiveTitle,
                fontFeatureSettings = "tnum"
            ),
            headlineSmall = defaultTypography.headlineSmall.copy(
                fontFamily = expressiveTitle,
                fontFeatureSettings = "tnum"
            ),
            titleLarge = defaultTypography.titleLarge.copy(
                fontFamily = expressiveTitle,
                fontFeatureSettings = "tnum"
            ),
            titleMedium = defaultTypography.titleMedium.copy(
                fontFamily = bodyFont,
                fontWeight = FontWeight.SemiBold,
                fontFeatureSettings = "tnum"
            ),
            titleSmall = defaultTypography.titleSmall.copy(
                fontFamily = bodyFont,
                fontWeight = FontWeight.SemiBold,
                fontFeatureSettings = "tnum"
            ),
            bodyLarge = defaultTypography.bodyLarge.copy(
                fontFamily = bodyFont,
                fontFeatureSettings = "tnum"
            ),
            bodyMedium = defaultTypography.bodyMedium.copy(
                fontFamily = bodyFont,
                fontFeatureSettings = "tnum"
            ),
            bodySmall = defaultTypography.bodySmall.copy(
                fontFamily = bodyFont,
                fontFeatureSettings = "tnum"
            ),
            labelLarge = defaultTypography.labelLarge.copy(
                fontFamily = bodyFont,
                fontWeight = FontWeight.SemiBold,
                fontFeatureSettings = "tnum"
            ),
            labelMedium = defaultTypography.labelMedium.copy(
                fontFamily = bodyFont,
                fontFeatureSettings = "tnum"
            ),
            labelSmall = defaultTypography.labelSmall.copy(
                fontFamily = bodyFont,
                fontFeatureSettings = "tnum"
            )
        )
    }
}
