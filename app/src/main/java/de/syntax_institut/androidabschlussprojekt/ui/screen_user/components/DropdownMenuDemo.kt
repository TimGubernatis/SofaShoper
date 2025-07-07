package de.syntax_institut.androidabschlussprojekt.ui.screen_user.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.PaymentMethodType
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.PaymentMethod

@Composable
fun DropdownMenuDemo(
    selectedMethod: PaymentMethod,
    onMethodSelected: (PaymentMethod) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Text(
            text = when (selectedMethod.type) {
                PaymentMethodType.PAYPAL -> "PayPal"
                PaymentMethodType.ABBUCHUNG -> "Abbuchung"
                PaymentMethodType.UEBERWEISUNG -> "Überweisung"
                PaymentMethodType.NACHNAHME -> "Nachnahme"
                PaymentMethodType.VISA -> "Visa"
                PaymentMethodType.AMAZON_PAY -> "Amazon Pay"
            },
            modifier = Modifier
                .clickable { expanded = true }
                .padding(8.dp),
            style = MaterialTheme.typography.bodyLarge
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            PaymentMethodType.values().forEach { methodType ->
                DropdownMenuItem(
                    text = { 
                        Text(when (methodType) {
                            PaymentMethodType.PAYPAL -> "PayPal"
                            PaymentMethodType.ABBUCHUNG -> "Abbuchung"
                            PaymentMethodType.UEBERWEISUNG -> "Überweisung"
                            PaymentMethodType.NACHNAHME -> "Nachnahme"
                            PaymentMethodType.VISA -> "Visa"
                            PaymentMethodType.AMAZON_PAY -> "Amazon Pay"
                        })
                    },
                    onClick = {
                        val paymentMethod = when (methodType) {
                            PaymentMethodType.PAYPAL -> PaymentMethod.paypal("")
                            PaymentMethodType.ABBUCHUNG -> PaymentMethod.none()
                            PaymentMethodType.NACHNAHME -> PaymentMethod.cashOnDelivery()
                            PaymentMethodType.VISA -> PaymentMethod.visa()
                            PaymentMethodType.AMAZON_PAY -> PaymentMethod.amazonPay()
                            PaymentMethodType.UEBERWEISUNG -> PaymentMethod.bankTransfer()
                        }
                        onMethodSelected(paymentMethod)
                        expanded = false
                    }
                )
            }
        }
    }
}