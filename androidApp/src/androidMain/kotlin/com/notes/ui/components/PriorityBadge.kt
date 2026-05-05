package com.notes.ui.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.notes.domain.model.Priority
import com.notes.ui.theme.FieryTerracotta
import com.notes.ui.theme.Oswald
import com.notes.ui.theme.PrussianBlue
import com.notes.ui.theme.getPriorityColor
import com.notes.ui.theme.getPriorityTextColor

@Composable
fun PriorityBadge(priority: Priority, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val borderColor by infiniteTransition.animateColor(
        initialValue = FieryTerracotta,
        targetValue = PrussianBlue,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "borderColor"
    )

    val badgeModifier = if (priority == Priority.URGENT) {
        modifier.border(2.dp, borderColor, RoundedCornerShape(24.dp))
    } else {
        modifier
    }

    Surface(
        color = getPriorityColor(priority),
        contentColor = getPriorityTextColor(priority),
        shape = RoundedCornerShape(24.dp),
        modifier = badgeModifier
    ) {
        Text(
            text = priority.name.uppercase(),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            fontSize = 12.sp,
            fontFamily = Oswald,
            fontWeight = FontWeight.Bold
        )
    }
}
