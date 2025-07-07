package de.syntax_institut.androidabschlussprojekt.ui.screen_profile.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.PaymentMethod
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.PaymentMethodType
import de.syntax_institut.androidabschlussprojekt.R

@Composable
fun ProfilePaymentSection(
    paymentMethods: List<Pair<String, PaymentMethod>>,
    defaultPaymentMethodId: String?,
    onSetDefault: (String) -> Unit,
    onAdd: () -> Unit,
    onDelete: (String) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Zahlungsmethode", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            if (paymentMethods.isEmpty()) {
                Text("Keine Zahlungsart vorhanden.")
            } else {
                paymentMethods.forEach { (id, payment) ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        RadioButton(
                            selected = id == defaultPaymentMethodId,
                            onClick = { onSetDefault(id) }
                        )
                        val iconMap = mapOf(
                            PaymentMethodType.ABBUCHUNG to Icons.Default.AccountBalance,
                            PaymentMethodType.UEBERWEISUNG to Icons.Default.SyncAlt,
                            PaymentMethodType.NACHNAHME to Icons.Default.AttachMoney
                        )
                        val vectorIconMap = mapOf(
                            PaymentMethodType.PAYPAL to R.drawable.ic_paypal,
                            PaymentMethodType.VISA to R.drawable.ic_visa,
                            PaymentMethodType.AMAZON_PAY to R.drawable.ic_amazon_pay
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            when (payment.type) {
                                PaymentMethodType.PAYPAL, PaymentMethodType.VISA, PaymentMethodType.AMAZON_PAY ->
                                    Image(painter = painterResource(vectorIconMap[payment.type]!!), contentDescription = null, modifier = Modifier.size(32.dp))
                                else ->
                                    Icon(iconMap[payment.type] ?: Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(32.dp))
                            }
                            Spacer(Modifier.width(8.dp))
                            Text(
                                when (payment.type) {
                                    PaymentMethodType.PAYPAL -> "PayPal"
                                    PaymentMethodType.ABBUCHUNG -> "Abbuchung"
                                    PaymentMethodType.UEBERWEISUNG -> "Überweisung"
                                    PaymentMethodType.NACHNAHME -> "Nachnahme"
                                    PaymentMethodType.VISA -> "Visa"
                                    PaymentMethodType.AMAZON_PAY -> "Amazon Pay"
                                },
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        Spacer(Modifier.weight(1f))
                        if (paymentMethods.size > 1) {
                            IconButton(onClick = { onDelete(id) }) { Icon(Icons.Default.Delete, contentDescription = "Löschen") }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onAdd) { Text("Zahlungsart hinzufügen") }
        }
    }
} 