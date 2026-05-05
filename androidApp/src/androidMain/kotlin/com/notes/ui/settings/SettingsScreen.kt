package com.notes.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.notes.ui.theme.ThemeViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    themeViewModel: ThemeViewModel = koinViewModel()
) {
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Section: Appearance
            SettingsSection(title = "Appearance") {
                SettingsToggleRow(
                    label = "Dark Mode",
                    checked = isDarkTheme,
                    onCheckedChange = { themeViewModel.toggleTheme() }
                )
                SettingsRow(
                    label = "Language",
                    value = "English (US)",
                    onClick = {}
                )
            }

            Spacer(Modifier.height(16.dp))

            // Section: Account
            SettingsSection(title = "Account") {
                SettingsRow(
                    label = "Profile",
                    value = "Emmanuel tt",
                    onClick = {}
                )
                SettingsRow(
                    label = "Subscription",
                    value = "Pro Plan",
                    onClick = {}
                )
            }

            Spacer(Modifier.height(16.dp))

            // Section: Security
            SettingsSection(title = "Security") {
                SettingsToggleRow(
                    label = "Biometric Lock",
                    checked = false,
                    onCheckedChange = {}
                )
                SettingsRow(
                    label = "Data & Privacy",
                    onClick = {}
                )
            }

            Spacer(Modifier.height(16.dp))

            // Section: Storage
            SettingsSection(title = "Storage") {
                SettingsRow(
                    label = "Cloud Backup",
                    value = "Enabled",
                    onClick = {}
                )
                SettingsRow(
                    label = "Clear Cache",
                    onClick = {}
                )
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun SettingsRow(
    label: String,
    value: String? = null,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodyLarge)
            if (value != null) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun SettingsToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
