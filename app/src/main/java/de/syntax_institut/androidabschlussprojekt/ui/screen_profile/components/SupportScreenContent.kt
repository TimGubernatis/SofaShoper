package de.syntax_institut.androidabschlussprojekt.ui.screen_profile.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import de.syntax_institut.androidabschlussprojekt.ui.screen_profile.components.NotificationSettingsSection
import androidx.lifecycle.viewmodel.compose.viewModel
import de.syntax_institut.androidabschlussprojekt.viewmodel.NotificationViewModel

@Composable
fun SupportScreenContent(
    navController: NavController
) {
    val notificationViewModel: NotificationViewModel = viewModel()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text("Support-Bereich", style = MaterialTheme.typography.titleMedium)
        NotificationSettingsSection(viewModel = notificationViewModel)
    }
} 