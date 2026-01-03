package com.keepup.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.keepup.android.ui.theme.SurfaceVariant

/**
 * Reusable poster view component with:
 * - Async image loading with Coil
 * - Shimmer placeholder
 * - Error fallback
 * - Rounded corners
 * - Proper aspect ratio
 */
@Composable
fun PosterView(
    posterUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    aspectRatio: Float = 2f / 3f, // Standard poster aspect ratio
    cornerRadius: Dp = 12.dp
) {
    SubcomposeAsyncImage(
        model = posterUrl,
        contentDescription = contentDescription,
        modifier = modifier
            .aspectRatio(aspectRatio)
            .clip(RoundedCornerShape(cornerRadius)),
        contentScale = ContentScale.Crop,
        loading = {
            // Shimmer placeholder
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SurfaceVariant)
            )
        },
        error = {
            // Error fallback
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.BrokenImage,
                    contentDescription = "Failed to load poster",
                    tint = Color.White.copy(alpha = 0.3f),
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    )
}
