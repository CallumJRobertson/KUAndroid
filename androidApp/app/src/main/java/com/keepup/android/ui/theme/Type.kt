package com.keepup.android.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Typography matching iOS specifications
val Typography = Typography(
    // Large Title: 34sp, Bold
    displayLarge = TextStyle(
        fontSize = 34.sp,
        fontWeight = FontWeight.Bold
    ),
    // Title: 28sp, Bold
    displayMedium = TextStyle(
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold
    ),
    // Headline: 17sp, Semibold
    headlineMedium = TextStyle(
        fontSize = 17.sp,
        fontWeight = FontWeight.SemiBold
    ),
    // Body: 17sp, Regular
    bodyLarge = TextStyle(
        fontSize = 17.sp,
        fontWeight = FontWeight.Normal
    ),
    // Callout: 16sp, Regular
    bodyMedium = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal
    ),
    // Subheadline: 15sp, Regular
    bodySmall = TextStyle(
        fontSize = 15.sp,
        fontWeight = FontWeight.Normal
    ),
    // Footnote: 13sp, Regular
    labelLarge = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.Normal
    ),
    // Caption: 12sp, Regular
    labelSmall = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal
    )
)
