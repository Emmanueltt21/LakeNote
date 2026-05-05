package com.notes.data.local

/**
 * expect/actual — KMP's mechanism for platform-specific implementations.
 *
 * This is one of the most important KMP concepts for interviews:
 *
 * - `expect` declares the API in commonMain (the "what")
 * - `actual` provides the implementation per platform (the "how")
 * - The compiler enforces that every expect has an actual in ALL configured targets
 * - This is how KMP achieves "write once, customize where needed"
 *
 * DatabaseFactory is a perfect candidate because:
 * - Android needs a Context to build the DB
 * - iOS needs a path to the Documents directory
 * - The Room builder API differs per platform
 */
expect class DatabaseFactory {
    fun create(): NotesDatabase
}
