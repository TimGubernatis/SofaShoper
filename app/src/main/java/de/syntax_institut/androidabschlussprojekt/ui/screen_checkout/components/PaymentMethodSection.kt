package de.syntax_institut.androidabschlussprojekt.ui.screen_checkout.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.SyncAlt
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.PaymentMethod
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.PaymentMethodType


@Composable
fun PaymentMethodSection(
    selectedMethod: PaymentMethod?,
    onMethodSelect: (PaymentMethod) -> Unit,
    userPaymentMethods: List<PaymentMethod>
) {
    var expanded by remember { mutableStateOf(false) }
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
    Card {
        Column(
            modifier = Modifier.padding()
        ) {
            Text(
                text = "Zahlungsmethode",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Box {
                OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                    if (selectedMethod != null) {
                        when (selectedMethod.type) {
                            PaymentMethodType.PAYPAL, PaymentMethodType.VISA, PaymentMethodType.AMAZON_PAY ->
                                Image(painter = painterResource(vectorIconMap[selectedMethod.type]!!), contentDescription = null, modifier = Modifier.size(32.dp))
                            else ->
                                Icon(iconMap[selectedMethod.type] ?: Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(32.dp))
                        }
                    } else {
                        Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(32.dp))
                    }
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    userPaymentMethods.forEach { paymentMethod ->
                        DropdownMenuItem(
                            text = { Text(paymentMethod.type.name) },
                            onClick = {
                                onMethodSelect(paymentMethod)
                                expanded = false
                            },
                            leadingIcon = {
                                when (paymentMethod.type) {
                                    PaymentMethodType.PAYPAL, PaymentMethodType.VISA, PaymentMethodType.AMAZON_PAY ->
                                        Image(painter = painterResource(vectorIconMap[paymentMethod.type]!!), contentDescription = null, modifier = Modifier.size(32.dp))
                                    else ->
                                        Icon(iconMap[paymentMethod.type] ?: Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(32.dp))
                                }
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            var showNewPaymentDialog by remember { mutableStateOf(false) }
            Button(onClick = { showNewPaymentDialog = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Neue Zahlungsmethode hinzufügen")
            }
            if (showNewPaymentDialog) {
                var selectedType by remember { mutableStateOf(PaymentMethodType.PAYPAL) }
                AlertDialog(
                    onDismissRequest = { showNewPaymentDialog = false },
                    title = { Text("Neue Zahlungsmethode hinzufügen") },
                    text = {
                        Column {
                            Text("Typ auswählen:")
                            PaymentMethodType.values().forEach { type ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = selectedType == type,
                                        onClick = { selectedType = type }
                                    )
                                    Text(
                                        when (type) {
                                            PaymentMethodType.PAYPAL -> "PayPal"
                                            PaymentMethodType.ABBUCHUNG -> "Abbuchung"
                                            PaymentMethodType.UEBERWEISUNG -> "Überweisung"
                                            PaymentMethodType.NACHNAHME -> "Nachnahme"
                                            PaymentMethodType.VISA -> "Visa"
                                            PaymentMethodType.AMAZON_PAY -> "Amazon Pay"
                                        }
                                    )
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            val paymentMethod = when (selectedType) {
                                PaymentMethodType.PAYPAL -> PaymentMethod.paypal("")
                                PaymentMethodType.ABBUCHUNG -> PaymentMethod.none()
                                PaymentMethodType.NACHNAHME -> PaymentMethod.cashOnDelivery()
                                PaymentMethodType.VISA -> PaymentMethod.visa()
                                PaymentMethodType.AMAZON_PAY -> PaymentMethod.amazonPay()
                                PaymentMethodType.UEBERWEISUNG -> PaymentMethod.bankTransfer()
                            }
                            onMethodSelect(paymentMethod)
                            showNewPaymentDialog = false
                        }) {
                            Text("Speichern")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showNewPaymentDialog = false }) {
                            Text("Abbrechen")
                        }
                    }
                )
            }
        }
    }
}