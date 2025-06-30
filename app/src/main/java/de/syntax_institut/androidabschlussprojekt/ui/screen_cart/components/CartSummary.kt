package de.syntax_institut.androidabschlussprojekt.ui.screen_cart.components

import androidx.compose.runtime.Composable
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import de.syntax_institut.androidabschlussprojekt.ui.components.PrimaryButton
import de.syntax_institut.androidabschlussprojekt.util.formatPrice

@Composable
fun CartSummary(
    itemCount: Int,
    total: Double,
    onCheckoutClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Gesamt ($itemCount Artikel):",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = formatPrice(total),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            PrimaryButton(
                text = "Zur Kasse",
                onClick = onCheckoutClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
} 