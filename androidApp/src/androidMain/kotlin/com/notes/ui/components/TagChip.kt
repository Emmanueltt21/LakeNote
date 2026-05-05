package com.notes.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.notes.ui.theme.LightGold
import com.notes.ui.theme.Oswald
import com.notes.ui.theme.PrussianBlue

@Composable
fun TagChip(tag: String, modifier: Modifier = Modifier) {
    AssistChip(
        onClick = { },
        label = {
            Text(
                text = tag.uppercase(),
                fontSize = 10.sp,
                fontFamily = Oswald,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
        },
        shape = RoundedCornerShape(24.dp),
        colors = AssistChipDefaults.assistChipColors(
            containerColor = LightGold,
            labelColor = PrussianBlue
        ),
        border = null,
        modifier = modifier
    )
}
