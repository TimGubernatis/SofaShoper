package de.syntax_institut.androidabschlussprojekt.ui.screen_profile.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import de.syntax_institut.androidabschlussprojekt.ui.components.CountryPicker

@Composable
fun AddressFields(
    street: String,
    onStreetChange: (String) -> Unit,
    houseNumber: String,
    onHouseNumberChange: (String) -> Unit,
    addressAddition: String,
    onAddressAdditionChange: (String) -> Unit,
    postalCode: String,
    onPostalCodeChange: (String) -> Unit,
    city: String,
    onCityChange: (String) -> Unit,
    country: String,
    onCountryChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = street,
        onValueChange = onStreetChange,
        label = { Text("Straße") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(
        value = houseNumber,
        onValueChange = onHouseNumberChange,
        label = { Text("Hausnummer") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(
        value = addressAddition,
        onValueChange = onAddressAdditionChange,
        label = { Text("Adresszusatz") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(
        value = postalCode,
        onValueChange = onPostalCodeChange,
        label = { Text("PLZ") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(
        value = city,
        onValueChange = onCityChange,
        label = { Text("Stadt") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(8.dp))
    CountryPicker(
        value = country,
        onValueChange = onCountryChange,
        modifier = Modifier.fillMaxWidth()
    )
}