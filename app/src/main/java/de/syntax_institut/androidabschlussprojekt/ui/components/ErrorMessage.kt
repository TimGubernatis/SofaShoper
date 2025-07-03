package de.syntax_institut.androidabschlussprojekt.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@Composable
fun ErrorMessage(
    message: String,
    showBackButton: Boolean = false,
    showRetryButton: Boolean = false,
    onBackClick: () -> Unit = {},
    onRetryClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "⚠️",
                style = MaterialTheme.typography.headlineLarge
            )
            
            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (showRetryButton) {
                    OutlinedButton(
                        onClick = onRetryClick,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("🔄 Wiederholen")
                    }
                }
                
                if (showBackButton) {
                    PrimaryButton(
                        text = "Zurück",
                        onClick = onBackClick
                    )
                }
            }
        }
    }
}