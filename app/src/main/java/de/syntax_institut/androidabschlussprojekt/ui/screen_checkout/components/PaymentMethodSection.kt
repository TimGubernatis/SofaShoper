package de.syntax_institut.androidabschlussprojekt.ui.screen_checkout.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Payment
import de.syntax_institut.androidabschlussprojekt.data.model.PaymentMethod
import de.syntax_institut.androidabschlussprojekt.util.responsiveCardPadding
import de.syntax_institut.androidabschlussprojekt.util.responsiveSpacing
import de.syntax_institut.androidabschlussprojekt.util.responsiveTextFieldSpacing

@Composable
fun PaymentMethodSection(
    selectedMethod: PaymentMethod?,
    onMethodSelect: (PaymentMethod) -> Unit
) {
    Card {
        Column(
            modifier = Modifier.padding(responsiveCardPadding())
        ) {
            Text(
                text = "Zahlungsmethode",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(responsiveSpacing()))
            
            PaymentMethod.values().forEach { method ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = responsiveTextFieldSpacing()),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedMethod == method,
                        onClick = { onMethodSelect(method) }
                    )
                    
                    Spacer(modifier = Modifier.width(responsiveTextFieldSpacing()))
                    
                    Icon(
                        imageVector = Icons.Default.Payment,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(responsiveTextFieldSpacing()))
                    
                    Text(
                        text = when (method) {
                            PaymentMethod.CREDIT_CARD -> "Kreditkarte"
                            PaymentMethod.PAYPAL -> "PayPal"
                            PaymentMethod.BANK_TRANSFER -> "Banküberweisung"
                            PaymentMethod.CASH_ON_DELIVERY -> "Nachnahme"
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}