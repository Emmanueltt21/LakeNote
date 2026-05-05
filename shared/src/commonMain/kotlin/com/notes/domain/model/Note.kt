package com.notes.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Pure domain model — no framework annotations, no database concerns.
 *
 * Clean Architecture rule: Domain layer has ZERO dependencies on outer layers.
 * This class is shared 100% across Android and iOS via KMP commonMain.
 */
data class Note(
    val id: Long = 0,
    val title: String,
    val content: String,
    val priority: Priority,
    val category: Category = Category.GENERAL,
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val tags: List<String> = emptyList(),
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now(),
) {
    /** Domain validation — business rules live here, not in the ViewModel */
    val isValid: Boolean
        get() = title.isNotBlank() && title.length <= MAX_TITLE_LENGTH

    val preview: String
        get() = content.take(PREVIEW_LENGTH).trimEnd()
            .let { if (content.length > PREVIEW_LENGTH) "$it…" else it }

    val isHighPriority: Boolean
        get() = priority == Priority.HIGH || priority == Priority.URGENT

    companion object {
        const val MAX_TITLE_LENGTH = 200
        const val PREVIEW_LENGTH = 150
    }
}

/**
 * Priority levels with ordering — URGENT > HIGH > MEDIUM > LOW.
 * The ordinal drives sort order in queries.
 */
enum class Priority(val label: String, val emoji: String) {
    LOW(label = "Low", emoji = "🔵"),
    MEDIUM(label = "Medium", emoji = "🟡"),
    HIGH(label = "High", emoji = "🔴"),
    URGENT(label = "Urgent", emoji = "🚨");

    companion object {
        fun fromOrdinal(ordinal: Int): Priority =
            entries.getOrElse(ordinal) { MEDIUM }
    }
}

enum class Category(val label: String) {
    GENERAL("General"),
    WORK("Work"),
    PERSONAL("Personal"),
    IDEA("Ideas"),
    TASK("Tasks"),
    LEARNING("Learning");
}

/** Sort options exposed to the UI through the domain layer */
enum class NoteSort {
    BY_UPDATED_DESC,
    BY_CREATED_DESC,
    BY_PRIORITY_DESC,
    BY_TITLE_ASC,
}
