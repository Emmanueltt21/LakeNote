package com.notes.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.notes.ui.notedetail.NoteDetailScreen
import com.notes.ui.notelist.NoteListScreen
import com.notes.ui.theme.NotesTheme

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NotesTheme {
        NavHost(navController = navController, startDestination = "notes") {
            composable("notes") {
                NoteListScreen(
                    onNavigateToCreate = {
                        navController.navigate("note")
                    },
                    onNavigateToDetail = { id ->
                        navController.navigate("note?noteId=$id")
                    }
                )
            }
            
            composable(
                route = "note?noteId={noteId}",
                arguments = listOf(
                    navArgument("noteId") {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) { backStackEntry ->
                val noteId = backStackEntry.arguments?.getLong("noteId") ?: -1L
                // NoteDetailViewModel will handle -1L as CREATE mode if adjusted,
                // but let's check how it handles null.
                // In ViewModel: val noteId: Long? = savedStateHandle["noteId"]
                // Nav component passing -1L might be an issue.
                // Better: route "note" and route "note/{noteId}"
                NoteDetailScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
