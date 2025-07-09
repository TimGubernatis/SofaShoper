package de.syntax_institut.androidabschlussprojekt.ui.screen_profile.profile.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.PaymentMethod
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.PaymentMethodType
import de.syntax_institut.androidabschlussprojekt.ui.screen_checkout.components.PaymentMethodSection

@Composable
fun AddPaymentDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onSave: (PaymentMethod) -> Unit
) {
    if (!showDialog) return

    var newPaymentMethod by remember { mutableStateOf(PaymentMethod.none()) }
    var paypalEmail by remember { mutableStateOf("") }
    var iban by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.medium, tonalElevation = 8.dp) {
            Column(Modifier.padding(24.dp).widthIn(min = 320.dp, max = 480.dp)) {
                Text("Zahlungsart auswählen", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(16.dp))
                PaymentMethodSection(
                    selectedMethod = newPaymentMethod,
                    onMethodSelect = { newPaymentMethod = it }
                )
                when (newPaymentMethod.type) {
                    PaymentMethodType.PAYPAL -> {
                        OutlinedTextField(
                            value = paypalEmail,
                            onValueChange = { paypalEmail = it },
                            label = { Text("PayPal Email") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    PaymentMethodType.UEBERWEISUNG -> {
                        OutlinedTextField(
                            value = iban,
                            onValueChange = { iban = it },
                            label = { Text("IBAN") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    else -> {}
                }
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(onClick = onDismiss) { Text("Abbrechen") }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = {
                        val paymentToSave = when (newPaymentMethod.type) {
                            PaymentMethodType.PAYPAL -> newPaymentMethod.copy(email = paypalEmail)
                            PaymentMethodType.UEBERWEISUNG -> newPaymentMethod.copy(iban = iban)
                            else -> newPaymentMethod
                        }
                        onSave(paymentToSave)
                        // Reset form
                        paypalEmail = ""
                        iban = ""
                        newPaymentMethod = PaymentMethod.none()
                    }) { Text("Speichern") }
                }
            }
        }
    }
} 