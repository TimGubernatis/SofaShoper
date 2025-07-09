package de.syntax_institut.androidabschlussprojekt.ui.screen_profile.support

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.syntax_institut.androidabschlussprojekt.viewmodel.NotificationViewModel
import de.syntax_institut.androidabschlussprojekt.ui.components.InfoDialog
import de.syntax_institut.androidabschlussprojekt.ui.components.SupportChatDialog
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import de.syntax_institut.androidabschlussprojekt.ui.components.PrimaryButton
import de.syntax_institut.androidabschlussprojekt.ui.screen_profile.support.components.NotificationSettingsSection

@Composable
fun SupportScreenContent() {
    val notificationViewModel: NotificationViewModel = viewModel()
    var showDsgvo by remember { mutableStateOf(false) }
    var showAgb by remember { mutableStateOf(false) }
    var showChat by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text("Support-Bereich", style = MaterialTheme.typography.titleMedium)
        NotificationSettingsSection(viewModel = notificationViewModel)
        Spacer(modifier = Modifier.height(24.dp))
        PrimaryButton(
            text = "DSGVO-Erklärung ansehen",
            onClick = { showDsgvo = true },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        PrimaryButton(
            text = "AGB ansehen",
            onClick = { showAgb = true },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        PrimaryButton(
            text = "Support kontaktieren",
            onClick = { showChat = true },
            modifier = Modifier.fillMaxWidth()
        )
        if (showDsgvo) {
            InfoDialog(
                title = "DSGVO-Erklärung",
                text = "Hier steht die DSGVO-Erklärung. (Platzhaltertext)",
                onDismiss = { showDsgvo = false }
            )
        }
        if (showAgb) {
            InfoDialog(
                title = "AGB",
                text = "Hier stehen die Allgemeinen Geschäftsbedingungen. (Platzhaltertext)",
                onDismiss = { showAgb = false }
            )
        }
        if (showChat) {
            SupportChatDialog(onDismiss = { showChat = false })
        }
    }
} 