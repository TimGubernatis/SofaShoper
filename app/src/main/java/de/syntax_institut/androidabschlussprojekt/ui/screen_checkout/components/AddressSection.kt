package de.syntax_institut.androidabschlussprojekt.ui.screen_checkout.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.syntax_institut.androidabschlussprojekt.data.model.ShippingAddress
import de.syntax_institut.androidabschlussprojekt.util.responsiveCardPadding
import de.syntax_institut.androidabschlussprojekt.util.responsiveTextFieldSpacing
import de.syntax_institut.androidabschlussprojekt.util.responsiveSpacing
import de.syntax_institut.androidabschlussprojekt.util.isTablet
import de.syntax_institut.androidabschlussprojekt.util.responsiveSmallTextSize

@Composable
fun AddressSection(
    address: ShippingAddress,
    onAddressChange: (ShippingAddress) -> Unit,
    title: String = "Lieferadresse"
) {
    Card {
        Column(
            modifier = Modifier.padding(responsiveCardPadding())
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(responsiveSpacing()))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(responsiveTextFieldSpacing())
            ) {
                OutlinedTextField(
                    value = address.firstName,
                    onValueChange = { onAddressChange(address.copy(firstName = it)) },
                    label = { Text("Vorname") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = address.lastName,
                    onValueChange = { onAddressChange(address.copy(lastName = it)) },
                    label = { Text("Nachname") },
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(responsiveTextFieldSpacing()))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(responsiveTextFieldSpacing())
            ) {
                OutlinedTextField(
                    value = address.street,
                    onValueChange = { onAddressChange(address.copy(street = it)) },
                    label = { Text("Straße") },
                    modifier = Modifier.weight(2f),
                    textStyle = LocalTextStyle.current.copy(fontSize = responsiveSmallTextSize())
                )
                OutlinedTextField(
                    value = address.houseNumber ?: "",
                    onValueChange = { onAddressChange(address.copy(houseNumber = it)) },
                    label = { Text(
                        "Hausnummer",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    ) },
                    modifier = Modifier.weight(1f),
                    textStyle = LocalTextStyle.current.copy(fontSize = responsiveSmallTextSize())
                )
            }
            
            Spacer(modifier = Modifier.height(responsiveTextFieldSpacing()))
            
            OutlinedTextField(
                value = address.addressAddition ?: "",
                onValueChange = { onAddressChange(address.copy(addressAddition = it)) },
                label = { Text("Adresszusatz") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(responsiveTextFieldSpacing()))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(responsiveTextFieldSpacing())
            ) {
                OutlinedTextField(
                    value = address.postalCode,
                    onValueChange = { onAddressChange(address.copy(postalCode = it)) },
                    label = { Text("PLZ") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = address.city,
                    onValueChange = { onAddressChange(address.copy(city = it)) },
                    label = { Text("Stadt") },
                    modifier = Modifier.weight(2f)
                )
            }
            
            Spacer(modifier = Modifier.height(responsiveTextFieldSpacing()))
            
            OutlinedTextField(
                value = address.phone ?: "",
                onValueChange = { onAddressChange(address.copy(phone = it)) },
                label = { Text("Telefon (optional)") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}



