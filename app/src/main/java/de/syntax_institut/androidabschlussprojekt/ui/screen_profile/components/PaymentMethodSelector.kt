package de.syntax_institut.androidabschlussprojekt.ui.screen_profile.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun PaymentMethodSelector(
    selectedMethod: String,
    onMethodSelected: (String) -> Unit,
    paypalEmail: String,
    onPaypalEmailChange: (String) -> Unit,
    ibanNumber: String,
    onIbanNumberChange: (String) -> Unit,
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = selectedMethod == "None",
                onClick = { onMethodSelected("None") }
            )
            Text("Keine")
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = selectedMethod == "PayPal",
                onClick = { onMethodSelected("PayPal") }
            )
            Text("PayPal")
        }
        if (selectedMethod == "PayPal") {
            OutlinedTextField(
                value = paypalEmail,
                onValueChange = onPaypalEmailChange,
                label = { Text("PayPal Email") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = selectedMethod == "IBAN",
                onClick = { onMethodSelected("IBAN") }
            )
            Text("IBAN")
        }
        if (selectedMethod == "IBAN") {
            OutlinedTextField(
                value = ibanNumber,
                onValueChange = onIbanNumberChange,
                label = { Text("IBAN Nummer") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}