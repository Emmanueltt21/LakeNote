package com.notes

import android.app.Application
import com.notes.di.androidModule
import com.notes.di.sharedModules
import com.notes.domain.model.Category
import com.notes.domain.model.Note
import com.notes.domain.model.Priority
import com.notes.domain.repository.NoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.java.KoinJavaComponent.getKoin

/**
 * Application entry point — initializes Koin DI graph on startup.
 *
 * startKoin {} is called once here and the DI graph is available app-wide.
 * The ordering matters: sharedModules first (infrastructure), then androidModule
 * which overrides the expect DatabaseFactory with the Android actual.
 */
class NotesApplication : Application() {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@NotesApplication)
            modules(sharedModules + androidModule)
        }
        seedSampleNotes()
    }

    /**
     * Seeds 10 sample notes on first launch if the database is empty.
     */
    private fun seedSampleNotes() {
        appScope.launch {
            val repository: NoteRepository = getKoin().get()
            val existingNotes = repository.observeNotes().first()
            if (existingNotes.isNotEmpty()) return@launch

            val sampleNotes = listOf(
                Note(
                    title = "Welcome to LakeNote!",
                    content = "This is your personal note-taking app. Create, organize, and search your notes with ease. Try pinning important notes so they always stay on top!",
                    priority = Priority.HIGH,
                    category = Category.GENERAL,
                    isPinned = true
                ),
                Note(
                    title = "Weekly Meeting Agenda",
                    content = "1. Review last week's action items\n2. Sprint progress update\n3. Design review for the new dashboard\n4. QA status on release v2.5\n5. Open floor discussion",
                    priority = Priority.HIGH,
                    category = Category.WORK,
                    isPinned = true
                ),
                Note(
                    title = "Grocery Shopping List",
                    content = "• Organic eggs (1 dozen)\n• Whole wheat bread\n• Fresh salmon fillets\n• Avocados (4)\n• Greek yogurt\n• Mixed berries\n• Olive oil\n• Garlic cloves",
                    priority = Priority.MEDIUM,
                    category = Category.PERSONAL
                ),
                Note(
                    title = "Kotlin Coroutines Notes",
                    content = "Key concepts:\n- suspend functions can only be called from coroutines\n- Flow is a cold asynchronous stream\n- StateFlow holds a single value and is hot\n- viewModelScope auto-cancels on ViewModel destruction\n- Use Dispatchers.IO for disk/network work",
                    priority = Priority.MEDIUM,
                    category = Category.LEARNING
                ),
                Note(
                    title = "App Feature Ideas",
                    content = "💡 Add voice note recording\n💡 Markdown support in note editor\n💡 Cloud sync with Google Drive\n💡 Widget for quick note capture\n💡 Collaborative notes with share links",
                    priority = Priority.LOW,
                    category = Category.IDEA
                ),
                Note(
                    title = "Book Recommendations",
                    content = "📚 Atomic Habits — James Clear\n📚 Deep Work — Cal Newport\n📚 Clean Architecture — Robert C. Martin\n📚 The Pragmatic Programmer — Hunt & Thomas\n📚 Thinking, Fast and Slow — Daniel Kahneman",
                    priority = Priority.LOW,
                    category = Category.PERSONAL
                ),
                Note(
                    title = "Bug Fix: Login Timeout",
                    content = "Issue: Users report session timeouts after 5 minutes of inactivity.\nRoot cause: Token refresh logic not triggering properly.\nFix: Update the interceptor to check token expiry 60s before actual expiration.\nStatus: PR submitted, awaiting review.",
                    priority = Priority.URGENT,
                    category = Category.WORK
                ),
                Note(
                    title = "Fitness Plan — Week 3",
                    content = "Monday: Upper body + 20 min cardio\nTuesday: Legs + core\nWednesday: Rest / yoga\nThursday: Full body HIIT\nFriday: Swimming 45 min\nSaturday: Hiking trail\nSunday: Active recovery + stretching",
                    priority = Priority.MEDIUM,
                    category = Category.PERSONAL
                ),
                Note(
                    title = "API Endpoints to Implement",
                    content = "POST /api/v1/notes — Create note\nGET /api/v1/notes — List all notes\nGET /api/v1/notes/{id} — Get note by ID\nPUT /api/v1/notes/{id} — Update note\nDELETE /api/v1/notes/{id} — Delete note\nPOST /api/v1/notes/{id}/archive — Archive note",
                    priority = Priority.HIGH,
                    category = Category.WORK
                ),
                Note(
                    title = "Travel Packing Checklist",
                    content = "✅ Passport & boarding pass\n✅ Phone charger & power bank\n✅ Headphones\n✅ Travel adapter\n✅ Toiletries bag\n✅ Snacks for the flight\n✅ Book or Kindle\n✅ Light jacket\n✅ Comfortable walking shoes",
                    priority = Priority.LOW,
                    category = Category.PERSONAL
                )
            )

            sampleNotes.forEach { note ->
                repository.saveNote(note)
            }
        }
    }
}

