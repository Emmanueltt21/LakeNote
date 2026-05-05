package com.notes.di

import com.notes.data.local.DatabaseFactory
import com.notes.data.repository.NoteRepositoryImpl
import com.notes.domain.repository.NoteRepository
import com.notes.domain.usecase.ArchiveNoteUseCase
import com.notes.domain.usecase.DeleteNoteUseCase
import com.notes.domain.usecase.GetArchivedNotesUseCase
import com.notes.domain.usecase.GetNoteByIdUseCase
import com.notes.domain.usecase.GetNotesByPriorityUseCase
import com.notes.domain.usecase.GetNotesUseCase
import com.notes.domain.usecase.SaveNoteUseCase
import com.notes.domain.usecase.SearchNotesUseCase
import com.notes.domain.usecase.TogglePinNoteUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Koin Dependency Injection modules — shared across all platforms.
 *
 * Interview talking points:
 * - sharedModule is defined in commonMain and included by every platform
 * - Platform-specific modules (androidModule, iosModule) extend it with platform deps
 * - singleOf() is Koin's concise way to declare a singleton with constructor injection
 * - bind<Interface>() wires the interface to its implementation
 * - This is where Dependency Inversion is "wired": NoteRepository → NoteRepositoryImpl
 *
 * Module organization:
 * - databaseModule: DB infrastructure
 * - dataModule: repository implementations
 * - domainModule: use cases
 * - presentationModule: ViewModels (in androidApp module)
 */

val databaseModule = module {
    single { DatabaseFactory(get()).create() }    // get() resolves Context on Android
    single { get<com.notes.data.local.NotesDatabase>().noteDao() }
}

val dataModule = module {
    singleOf(::NoteRepositoryImpl) bind NoteRepository::class
}

val domainModule = module {
    // Use Cases — could also be factory (new instance per injection) but singleton is fine here
    singleOf(::GetNotesUseCase)
    singleOf(::SaveNoteUseCase)
    singleOf(::ArchiveNoteUseCase)
    singleOf(::DeleteNoteUseCase)
    singleOf(::GetNoteByIdUseCase)
    singleOf(::SearchNotesUseCase)
    singleOf(::TogglePinNoteUseCase)
    singleOf(::GetNotesByPriorityUseCase)
    singleOf(::GetArchivedNotesUseCase)
}

/** All shared modules bundled — platforms include this list */
val sharedModules = listOf(databaseModule, dataModule, domainModule)
