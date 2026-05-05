package com.notes.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room database entity — lives in the DATA layer.
 *
 * Key design decisions:
 * 1. Separate from domain Note model (NoteEntity ≠ Note)
 *    - DB schema can evolve independently from domain model
 *    - No Room annotations leak into the domain layer
 * 2. Instants stored as Long (epoch millis) — Room doesn't know kotlinx.datetime
 * 3. Tags stored as a comma-separated String — avoids a junction table for simplicity
 * 4. Index on priority + isArchived + updatedAt to support common sort queries efficiently
 */
@Entity(
    tableName = "notes",
    indices = [
        Index(value = ["priority", "is_archived"]),
        Index(value = ["updated_at"]),
        Index(value = ["is_pinned"]),
    ]
)
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val priority: Int,           // Stored as ordinal for efficient sorting
    val category: String,
    val is_pinned: Boolean,
    val is_archived: Boolean,
    val tags: String,            // Comma-separated: "kotlin,android,kmp"
    val created_at: Long,        // Epoch milliseconds
    val updated_at: Long,        // Epoch milliseconds
)
