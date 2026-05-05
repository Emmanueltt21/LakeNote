package com.notes.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.notes.ui.notedetail.NoteDetailScreen
import com.notes.ui.notelist.NoteListScreen
import com.notes.ui.placeholder.PlaceholderScreen
import com.notes.ui.settings.SettingsScreen
import com.notes.ui.tasks.TasksScreen
import com.notes.ui.theme.NotesTheme
import com.notes.ui.theme.Oswald
import com.notes.ui.theme.PrussianBlue
import com.notes.ui.theme.SandyBrown
import com.notes.ui.theme.ThemeViewModel
import com.notes.ui.theme.VanillaCustard
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    themeViewModel: ThemeViewModel = koinViewModel()
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

    NotesTheme(darkTheme = isDarkTheme) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    drawerContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "NOTES APP",
                        modifier = Modifier.padding(16.dp),
                        fontFamily = Oswald,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Divider()
                    DrawerItem(
                        icon = Icons.Default.Share,
                        label = "Share App",
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("share")
                        }
                    )
                    DrawerItem(
                        icon = Icons.Default.Star,
                        label = "Review",
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("review")
                        }
                    )
                    DrawerItem(
                        icon = Icons.Default.Info,
                        label = "About Us",
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("about")
                        }
                    )
                    DrawerItem(
                        icon = Icons.Default.Lock,
                        label = "Privacy Policy",
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("privacy")
                        }
                    )
                }
            }
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            // Show bottom bar only on main tabs
            val showBottomBar = currentDestination?.route in listOf("notes", "tasks", "archive", "settings")

            Scaffold(
                bottomBar = {
                    if (showBottomBar) {
                        AppBottomNavigation(navController = navController)
                    }
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = "notes",
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable("notes") {
                        NoteListScreen(
                            onNavigateToCreate = { navController.navigate("note") },
                            onNavigateToDetail = { id -> navController.navigate("note?noteId=$id") },
                            onOpenDrawer = { scope.launch { drawerState.open() } }
                        )
                    }
                    composable("tasks") { TasksScreen() }
                    composable("archive") { PlaceholderScreen("Archive") }
                    composable("settings") { SettingsScreen() }
                    
                    composable("share") { PlaceholderScreen("Share App") }
                    composable("review") { PlaceholderScreen("Review") }
                    composable("about") { PlaceholderScreen("About Us") }
                    composable("privacy") { PlaceholderScreen("Privacy Policy") }

                    composable(
                        route = "note?noteId={noteId}",
                        arguments = listOf(
                            navArgument("noteId") {
                                type = NavType.LongType
                                defaultValue = -1L
                            }
                        )
                    ) {
                        NoteDetailScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DrawerItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        icon = { Icon(icon, contentDescription = null) },
        label = { Text(label, fontFamily = Oswald) },
        selected = false,
        onClick = onClick,
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
        colors = NavigationDrawerItemDefaults.colors(
            unselectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
            unselectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

@Composable
private fun AppBottomNavigation(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        containerColor = PrussianBlue,
        tonalElevation = 0.dp,
        modifier = Modifier.height(80.dp)
    ) {
        val items = listOf(
            Triple("notes", "NOTES", Icons.Default.Menu),
            Triple("tasks", "TASKS", Icons.Default.CheckCircle),
            Triple("archive", "ARCHIVE", Icons.Default.Settings), // Using placeholder icon
            Triple("settings", "SETTINGS", Icons.Default.Settings)
        )

        items.forEach { (route, title, icon) ->
            val selected = currentDestination?.hierarchy?.any { it.route == route } == true
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(icon, contentDescription = title) },
                label = { Text(title, fontFamily = Oswald, fontSize = 10.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = SandyBrown,
                    selectedTextColor = SandyBrown,
                    unselectedIconColor = VanillaCustard.copy(alpha = 0.4f),
                    unselectedTextColor = VanillaCustard.copy(alpha = 0.4f),
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
