package de.syntax_institut.androidabschlussprojekt.ui.screen_checkout.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.syntax_institut.androidabschlussprojekt.util.responsiveCardPadding
import de.syntax_institut.androidabschlussprojekt.util.responsiveTextFieldSpacing
import de.syntax_institut.androidabschlussprojekt.util.responsiveSpacing
import de.syntax_institut.androidabschlussprojekt.util.isTablet
import de.syntax_institut.androidabschlussprojekt.util.responsiveSmallTextSize
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.Address
import de.syntax_institut.androidabschlussprojekt.ui.components.CountryPicker

@Composable
fun AddressSection(
    address: Address,
    onAddressChange: (Address) -> Unit,
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
                    value = address.recipientFirstName,
                    onValueChange = { onAddressChange(address.copy(recipientFirstName = it)) },
                    label = { Text("Vorname") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = address.recipientLastName,
                    onValueChange = { onAddressChange(address.copy(recipientLastName = it)) },
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
                    modifier = Modifier.weight(1.2f)
                )
                OutlinedTextField(
                    value = address.houseNumber,
                    onValueChange = { onAddressChange(address.copy(houseNumber = it)) },
                    label = { Text("Hausnr.") },
                    modifier = Modifier.weight(0.8f)
                )
            }
            
            Spacer(modifier = Modifier.height(responsiveTextFieldSpacing()))
            
            OutlinedTextField(
                value = address.addressAddition ?: "",
                onValueChange = { onAddressChange(address.copy(addressAddition = it)) },
                label = { Text(responsiveLabel("Adresszusatz", "Zusatz")) },
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
                label = { Text(responsiveLabel("Telefon (optional)", "Tel.")) },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(responsiveTextFieldSpacing()))
            
            CountryPicker(
                value = address.country,
                onValueChange = { onAddressChange(address.copy(country = it)) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun responsiveLabel(long: String, short: String, minWidthForLong: Dp = 400.dp): String {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    return if (screenWidth > minWidthForLong) long else short
}



