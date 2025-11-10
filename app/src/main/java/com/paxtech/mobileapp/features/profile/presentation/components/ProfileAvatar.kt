package com.paxtech.mobileapp.features.profile.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.paxtech.mobileapp.ui.theme.LightPurple
import com.paxtech.mobileapp.ui.theme.PrimaryPurple

@Composable
internal fun ProfileAvatar(
    modifier: Modifier = Modifier,
    avatarUrl: String? = null,
    containerColor: Color = LightPurple,
    contentColor: Color = PrimaryPurple,
    size: Dp = 72.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(containerColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(size * 0.5f)
        )
    }
}
