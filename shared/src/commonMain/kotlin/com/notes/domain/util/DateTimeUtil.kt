package com.notes.domain.util

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object DateTimeUtil {
    fun formatNoteDate(instant: Instant): String {
        val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val month = dateTime.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
        val day = dateTime.dayOfMonth.toString().padStart(2, '0')
        val year = dateTime.year
        
        return "$month $day, $year"
    }
}
