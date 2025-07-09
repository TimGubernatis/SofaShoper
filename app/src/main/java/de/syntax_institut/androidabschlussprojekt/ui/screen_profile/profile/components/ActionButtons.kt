package de.syntax_institut.androidabschlussprojekt.ui.screen_profile.profile.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ActionButtons(
    onSaveClick: () -> Unit,
    onLogoutClick: () -> Unit,
    isSaving: Boolean,
    errorMessage: String?
) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Button(
            onClick = onSaveClick,
            enabled = !isSaving,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            Text(if (isSaving) "Speichern..." else "Speichern")
        }

        Button(
            onClick = onLogoutClick,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(end = 16.dp)
        ) {
            Text("Logout")
        }

        if (!errorMessage.isNullOrEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(bottom = 48.dp)
            )
        }
    }
}