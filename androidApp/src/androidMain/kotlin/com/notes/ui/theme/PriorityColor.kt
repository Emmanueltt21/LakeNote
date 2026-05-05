package com.notes.ui.theme

import com.notes.domain.model.Priority
import androidx.compose.ui.graphics.Color

fun getPriorityColor(priority: Priority): Color = when (priority) {
    Priority.LOW -> LightGold
    Priority.MEDIUM -> SandyBrown
    Priority.HIGH -> FieryTerracotta
    Priority.URGENT -> FieryTerracotta
}

fun getPriorityTextColor(priority: Priority): Color = when (priority) {
    Priority.LOW -> PrussianBlue
    Priority.MEDIUM -> PrussianBlue
    Priority.HIGH -> VanillaCustard
    Priority.URGENT -> VanillaCustard
}
