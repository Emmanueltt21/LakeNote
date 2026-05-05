package com.notes.data.mapper

import com.notes.data.local.entity.NoteEntity
import com.notes.domain.model.Category
import com.notes.domain.model.Note
import com.notes.domain.model.Priority
import kotlinx.datetime.Instant

/**
 * Mapper functions — bridge between data and domain layers.
 *
 * Interview talking points:
 * - Mappers keep data and domain models decoupled
 * - Extension functions are idiomatic Kotlin — no mapper class needed
 * - toDomain() and toEntity() are the two directions
 * - Tags stored as CSV in DB, converted to/from List<String> here
 * - This is where you'd also handle API response → domain model mapping
 */

fun NoteEntity.toDomain(): Note = Note(
    id = id,
    title = title,
    content = content,
    priority = Priority.fromOrdinal(priority),
    category = Category.entries.find { it.name == category } ?: Category.GENERAL,
    isPinned = is_pinned,
    isArchived = is_archived,
    tags = if (tags.isBlank()) emptyList() else tags.split(",").map { it.trim() },
    createdAt = Instant.fromEpochMilliseconds(created_at),
    updatedAt = Instant.fromEpochMilliseconds(updated_at),
)

fun Note.toEntity(): NoteEntity = NoteEntity(
    id = id,
    title = title,
    content = content,
    priority = priority.ordinal,
    category = category.name,
    is_pinned = isPinned,
    is_archived = isArchived,
    tags = tags.joinToString(","),
    created_at = createdAt.toEpochMilliseconds(),
    updated_at = updatedAt.toEpochMilliseconds(),
)

fun List<NoteEntity>.toDomain(): List<Note> = map { it.toDomain() }
