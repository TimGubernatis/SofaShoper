package de.syntax_institut.androidabschlussprojekt.ui.screen_profile.support.components

import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import de.syntax_institut.androidabschlussprojekt.viewmodel.NotificationViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import android.provider.Settings
import de.syntax_institut.androidabschlussprojekt.ui.components.PrimaryButton
import android.net.Uri
import androidx.compose.material3.MaterialTheme

@Composable
fun NotificationSettingsSection(viewModel: NotificationViewModel) {
    val optIn by viewModel.optIn.collectAsState()
    val context = LocalContext.current
    val notificationManager = context.getSystemService(android.app.NotificationManager::class.java)
    val areNotificationsEnabled = notificationManager?.areNotificationsEnabled() ?: true
    if (!areNotificationsEnabled) {
        Text(
            "Benachrichtigungen sind systemweit deaktiviert. Bitte aktiviere sie in den Android-Einstellungen unter Apps > AndroidAbschlussprojekt > Benachrichtigungen.",
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        PrimaryButton(
            text = "Zu den App-Einstellungen",
            onClick = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:" + context.packageName)
                }
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        return
    }
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Benachrichtigungen für Angebote")
        Switch(
            checked = optIn,
            onCheckedChange = {
                viewModel.setOptIn(it)
                if (it) viewModel.setAskAgain(false)
            }
        )
    }
    PrimaryButton(
        text = "Benachrichtigungen aktivieren",
        onClick = {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            }
            context.startActivity(intent)
        },
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
    )
} 