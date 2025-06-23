package de.syntax_institut.androidabschlussprojekt.ui.screen_home.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DealOfTheDayBanner() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFEB02)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "🔥 Deal of the Day",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Bis zu 40% auf Top-Produkte",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}