package de.syntax_institut.androidabschlussprojekt.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import de.syntax_institut.androidabschlussprojekt.viewmodel.NotificationViewModel
import androidx.compose.foundation.layout.*

@Composable
fun NotificationOptInDialog(
    viewModel: NotificationViewModel,
    onDismiss: () -> Unit
) {
    var denyCount by remember { mutableStateOf(0) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Benachrichtigungen aktivieren?") },
        text = { Text("Möchtest du Angebote und Warenkorb-Benachrichtigungen erhalten?") },
        confirmButton = {
            PrimaryButton(
                text = "Ja",
                onClick = {
                    viewModel.setOptIn(true)
                    viewModel.setAskAgain(false)
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth()
            )
        },
        dismissButton = {
            PrimaryButton(
                text = "Nein",
                onClick = {
                    denyCount++
                    if (denyCount >= 2) {
                        viewModel.setOptIn(false)
                        viewModel.setAskAgain(false)
                        onDismiss()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    )
} 