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
    MainScreen()
}
