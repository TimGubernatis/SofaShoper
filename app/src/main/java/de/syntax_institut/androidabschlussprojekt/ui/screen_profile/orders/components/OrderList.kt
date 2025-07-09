package de.syntax_institut.androidabschlussprojekt.ui.screen_profile.orders.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.syntax_institut.androidabschlussprojekt.data.model.OrderFirestoreModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OrderList(orders: List<OrderFirestoreModel>, modifier: Modifier = Modifier) {
    if (orders.isEmpty()) {
        Text("Keine Bestellungen vorhanden.", style = MaterialTheme.typography.bodyMedium)
    } else {
        LazyColumn(modifier = modifier.fillMaxWidth()) {
            items(orders) { order ->
                OrderCard(order)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun OrderCard(order: OrderFirestoreModel) {
    val date = rememberFormattedDate(order.timestamp)
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Bestellung vom $date", style = MaterialTheme.typography.titleMedium)
            Text("Status: ${order.status}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Artikel:", style = MaterialTheme.typography.bodyMedium)
            order.items.forEach { item ->
                Text("- ${item.title} x${item.quantity} (${String.format("%.2f €", item.price)})", style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("Gesamt: ${String.format("%.2f €", order.total)}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun rememberFormattedDate(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    return format.format(date)
} 