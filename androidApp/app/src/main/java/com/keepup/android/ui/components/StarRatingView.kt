package com.keepup.android.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.ceil
import kotlin.math.floor

/**
 * Star rating view component with:
 * - Display star rating (0-5)
 * - Half stars support
 * - Yellow color
 * - Customizable size
 */
@Composable
fun StarRatingView(
    rating: Float,
    modifier: Modifier = Modifier,
    starSize: Dp = 16.dp,
    color: Color = Color(0xFFFFD700) // Gold/Yellow
) {
    val fullStars = floor(rating).toInt()
    val hasHalfStar = (rating - fullStars) >= 0.25f && (rating - fullStars) < 0.75f
    val emptyStars = 5 - fullStars - if (hasHalfStar) 1 else 0
    
    Row(modifier = modifier) {
        // Full stars
        repeat(fullStars) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(starSize)
            )
        }
        
        // Half star
        if (hasHalfStar) {
            Icon(
                imageVector = Icons.Filled.StarHalf,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(starSize)
            )
        }
        
        // Empty stars
        repeat(emptyStars) {
            Icon(
                imageVector = Icons.Outlined.StarOutline,
                contentDescription = null,
                tint = color.copy(alpha = 0.3f),
                modifier = Modifier.size(starSize)
            )
        }
    }
}

/**
 * Interactive star rating selector for reviews
 */
@Composable
fun StarRatingSelector(
    rating: Float,
    onRatingChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    starSize: Dp = 32.dp
) {
    Row(modifier = modifier) {
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.StarOutline,
                contentDescription = "Star $i",
                tint = if (i <= rating) Color(0xFFFFD700) else Color.Gray,
                modifier = Modifier
                    .size(starSize)
                    .then(
                        androidx.compose.foundation.clickable {
                            onRatingChange(i.toFloat())
                        }
                    )
            )
        }
    }
}
