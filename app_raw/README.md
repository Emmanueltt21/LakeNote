# KMP Notes App — Clean Architecture Reference

A production-quality **Kotlin Multiplatform** note-taking app with task priority, built for KMP interview preparation.

---

## Project Structure

```
kmp-notes-app/
├── shared/                          # KMP shared module (commonMain + platform actuals)
│   └── src/
│       ├── commonMain/kotlin/com/notes/
│       │   ├── domain/              # 🟡 Domain Layer (pure Kotlin, zero framework deps)
│       │   │   ├── model/           #    Note.kt, Priority, Category, NoteSort
│       │   │   ├── repository/      #    NoteRepository interface (the contract)
│       │   │   └── usecase/         #    GetNotesUseCase, SaveNoteUseCase, etc.
│       │   ├── data/                # 🔵 Data Layer (Room, mappers, repository impl)
│       │   │   ├── local/           #    NotesDatabase, NoteDao, NoteEntity, DatabaseFactory
│       │   │   ├── mapper/          #    NoteMapper.kt (entity ↔ domain)
│       │   │   └── repository/      #    NoteRepositoryImpl.kt
│       │   └── di/                  #    Koin sharedModules
│       ├── androidMain/             # Android actuals (DatabaseFactory.android.kt)
│       └── iosMain/                 # iOS actuals (DatabaseFactory.ios.kt)
│
├── androidApp/                      # 🔴 Android Presentation Layer
│   └── src/main/kotlin/com/notes/
│       ├── presentation/            #    ViewModels (MVI: state + events)
│       ├── ui/                      #    Compose screens
│       └── di/                      #    androidModule (ViewModels + Context)
│
└── iosApp/                          # iOS Presentation (SwiftUI, uses shared module)
```

---

## Clean Architecture Layers

```
┌──────────────────────────────────────────────────────┐
│  Presentation (Android: Compose + ViewModel)         │  Platform-specific
│  Presentation (iOS: SwiftUI + ObservableObject)      │
├──────────────────────────────────────────────────────┤
│  Domain (commonMain — shared 100%)                   │  ← CORE
│  • Note, Priority, Category (domain models)          │
│  • NoteRepository interface (the contract)           │
│  • Use Cases: GetNotes, SaveNote, Search, etc.       │
├──────────────────────────────────────────────────────┤
│  Data (commonMain — shared 100%)                     │  Platform-specific
│  • Room: NoteEntity, NoteDao, NotesDatabase          │  DB creation
│  • NoteRepositoryImpl (implements domain contract)   │
│  • NoteMapper (entity ↔ domain)                      │
└──────────────────────────────────────────────────────┘
```

### Dependency Rule (strictly enforced)
- **Domain → nothing** (no imports from data or presentation)
- **Data → Domain** (implements domain interfaces)
- **Presentation → Domain** (uses use cases, never imports from data)

---

## Key KMP Concepts Covered

### 1. `expect` / `actual`
```kotlin
// commonMain — declares the API
expect class DatabaseFactory {
    fun create(): NotesDatabase
}

// androidMain — Android implementation
actual class DatabaseFactory(private val context: Context) {
    actual fun create() = Room.databaseBuilder(context, ...).build()
}

// iosMain — iOS implementation
actual class DatabaseFactory {
    actual fun create() = Room.databaseBuilder<NotesDatabase>(name = dbPath)
        .setDriver(BundledSQLiteDriver()).build()
}
```

### 2. Room KMP (2.7+)
- Works on both Android and iOS targets
- Android: standard Room SQLite driver (OS-provided)
- iOS: `BundledSQLiteDriver` — SQLite bundled in the compiled framework
- KSP targets required per platform in `build.gradle.kts`

### 3. Coroutines + Flow as the reactive backbone
- `Flow<List<Note>>` from Room — emits on every DB change
- `StateFlow` in ViewModel — UI always sees the latest state
- `SharedFlow` for one-time events — snackbar, navigation
- `combine()` — merges multiple flows reactively
- `flatMapLatest()` — cancels previous search on new query
- `debounce(300)` — avoids a DB query per keystroke

### 4. Dependency Inversion via Koin
```
NoteRepository (domain interface)
    ↑ wired by Koin
NoteRepositoryImpl (data class)
```
ViewModel depends on `NoteRepository`, not `NoteRepositoryImpl`.

---

## Interview Q&A

**Q: Why separate `NoteEntity` from `Note`?**
> The domain model evolves with business rules; the DB schema evolves with persistence needs. Keeping them separate allows either to change without breaking the other. The mapper is the seam.

**Q: Why use interfaces for repositories?**
> Testability. In tests, you inject `FakeNoteRepository`. In production, Koin wires `NoteRepositoryImpl`. The ViewModel never knows which it gets.

**Q: What's the difference between `StateFlow` and `SharedFlow`?**
> `StateFlow` always has a value and replays the latest to new collectors — perfect for UI state. `SharedFlow` can have 0 replays — perfect for one-time events like snackbars.

**Q: How does Room work on iOS in KMP?**
> Room 2.7+ supports iOS via `BundledSQLiteDriver`, which bundles SQLite with the compiled Kotlin framework. The `DatabaseFactory` `expect/actual` handles the platform difference.

**Q: Why is `sharedModules` in commonMain but `androidModule` in androidApp?**
> ViewModels extend `androidx.lifecycle.ViewModel` — an Android-only class. Domain and data modules are pure Kotlin, so they're shared. Platform-specific wiring goes in platform source sets.

**Q: What is `@Upsert` in Room?**
> It's `INSERT OR REPLACE` in a single annotation. When `id == 0`, it inserts and auto-generates an id. When `id > 0`, it replaces the existing row.

**Q: Why `SharingStarted.WhileSubscribed(5_000)`?**
> It keeps the upstream Flow active for 5 seconds after the last collector unsubscribes. This handles configuration changes (screen rotation) without restarting the upstream query.

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Shared Logic | Kotlin Multiplatform 2.0 |
| Local DB | Room 2.7 (KMP) |
| DI | Koin 4.0 |
| Async | Kotlin Coroutines + Flow |
| Date/Time | kotlinx-datetime |
| Android UI | Jetpack Compose + Material 3 |
| iOS UI | SwiftUI (consumes shared module) |
| Build | Gradle with Version Catalog |
