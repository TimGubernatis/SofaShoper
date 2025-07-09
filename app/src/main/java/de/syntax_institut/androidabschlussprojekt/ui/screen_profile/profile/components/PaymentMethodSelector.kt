package de.syntax_institut.androidabschlussprojekt.ui.screen_profile.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.PaymentMethodType
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material.icons.filled.Payment
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.PaymentMethod

@Composable
fun PaymentMethodSelector(
    selectedMethod: PaymentMethod?,
    onMethodSelected: (PaymentMethod) -> Unit
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
            PaymentMethodType.values().forEach { methodType ->
                DropdownMenuItem(
                    text = {},
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
                    },
                    leadingIcon = {
                        when (methodType) {
                            PaymentMethodType.PAYPAL, PaymentMethodType.VISA, PaymentMethodType.AMAZON_PAY ->
                                Image(painter = painterResource(vectorIconMap[methodType]!!), contentDescription = null, modifier = Modifier.size(32.dp))
                            else ->
                                Icon(iconMap[methodType] ?: Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(32.dp))
                        }
                    }
                )
            }
        }
    }
}