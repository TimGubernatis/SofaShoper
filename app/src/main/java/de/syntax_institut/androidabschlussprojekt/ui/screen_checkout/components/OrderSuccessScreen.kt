package de.syntax_institut.androidabschlussprojekt.ui.screen_checkout.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import de.syntax_institut.androidabschlussprojekt.data.model.Order
import de.syntax_institut.androidabschlussprojekt.ui.components.PrimaryButton

@Composable
fun OrderSuccessScreen(
    order: Order,
    onBackToHome: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Erfolg",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = "Bestellung erfolgreich!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "Bestellnummer: ${order.id}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = "Vielen Dank für Ihre Bestellung. Sie erhalten eine Bestätigung per E-Mail.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            PrimaryButton(
                text = "Zurück zur Startseite",
                onClick = onBackToHome,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
} 