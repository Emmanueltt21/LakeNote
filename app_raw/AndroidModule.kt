package com.notes.di

import android.content.Context
import com.notes.data.local.DatabaseFactory
import com.notes.presentation.notedetail.NoteDetailViewModel
import com.notes.presentation.notelist.NoteListViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Android-specific Koin module.
 *
 * Provides:
 * 1. DatabaseFactory with Android Context (expect/actual — see DatabaseFactory.android.kt)
 * 2. ViewModels — Koin's viewModelOf() handles SavedStateHandle injection automatically
 *
 * Note: ViewModels are NOT in sharedModules because:
 * - They extend AndroidX ViewModel
 * - They depend on Compose/Android-specific UI contracts
 * - iOS uses a different presentation pattern (SwiftUI + ObservableObject or TCA)
 */
val androidModule = module {
    single<DatabaseFactory> { DatabaseFactory(androidContext()) }
    viewModelOf(::NoteListViewModel)
    viewModelOf(::NoteDetailViewModel)
}
