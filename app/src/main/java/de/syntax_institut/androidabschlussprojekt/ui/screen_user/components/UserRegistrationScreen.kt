package de.syntax_institut.androidabschlussprojekt.ui.screen_user.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.Address
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.PaymentMethod

@Composable
fun UserRegistrationScreen(
    onRegisterClick: (UserInputData) -> Unit,
    modifier: Modifier = Modifier
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }

    var street by remember { mutableStateOf("") }
    var houseNumber by remember { mutableStateOf("") }
    var postalCode by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("DE") }

    var billingSameAsShipping by remember { mutableStateOf(true) }
    var billingStreet by remember { mutableStateOf("") }
    var billingHouseNumber by remember { mutableStateOf("") }
    var billingPostalCode by remember { mutableStateOf("") }
    var billingCity by remember { mutableStateOf("") }
    var billingCountry by remember { mutableStateOf("DE") }

    var paymentMethodType by remember { mutableStateOf("None") }
    var paypalEmail by remember { mutableStateOf("") }
    var iban by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Benutzer Registrierung", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("Vorname") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Nachname") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Telefon") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = mobile,
            onValueChange = { mobile = it },
            label = { Text("Handy") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )

        Text("Versandadresse", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = street,
            onValueChange = { street = it },
            label = { Text("Straße") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = houseNumber,
            onValueChange = { houseNumber = it },
            label = { Text("Hausnummer") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = postalCode,
            onValueChange = { postalCode = it },
            label = { Text("Postleitzahl") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("Stadt") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = country,
            onValueChange = { country = it },
            label = { Text("Land") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Checkbox(
                checked = billingSameAsShipping,
                onCheckedChange = { billingSameAsShipping = it }
            )
            Text("Rechnungsadresse ist gleich Versandadresse")
        }

        if (!billingSameAsShipping) {
            Text("Rechnungsadresse", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = billingStreet,
                onValueChange = { billingStreet = it },
                label = { Text("Straße") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = billingHouseNumber,
                onValueChange = { billingHouseNumber = it },
                label = { Text("Hausnummer") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = billingPostalCode,
                onValueChange = { billingPostalCode = it },
                label = { Text("Postleitzahl") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = billingCity,
                onValueChange = { billingCity = it },
                label = { Text("Stadt") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = billingCountry,
                onValueChange = { billingCountry = it },
                label = { Text("Land") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Text("Zahlungsmethode", style = MaterialTheme.typography.titleMedium)

        DropdownMenuDemo(
            selectedMethod = paymentMethodType,
            onMethodSelected = { paymentMethodType = it }
        )

        when (paymentMethodType) {
            "PayPal" -> {
                OutlinedTextField(
                    value = paypalEmail,
                    onValueChange = { paypalEmail = it },
                    label = { Text("PayPal Email") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            "IBAN" -> {
                OutlinedTextField(
                    value = iban,
                    onValueChange = { iban = it },
                    label = { Text("IBAN") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Button(
            onClick = {
                val userInput = UserInputData(
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    phone = phone.takeIf { it.isNotBlank() },
                    mobile = mobile.takeIf { it.isNotBlank() },
                    shippingAddress = Address(
                        street = street,
                        houseNumber = houseNumber,
                        postalCode = postalCode,
                        city = city,
                        country = country
                    ),
                    billingAddress = if (billingSameAsShipping) {
                        Address(
                            street = street,
                            houseNumber = houseNumber,
                            postalCode = postalCode,
                            city = city,
                            country = country
                        )
                    } else {
                        Address(
                            street = billingStreet,
                            houseNumber = billingHouseNumber,
                            postalCode = billingPostalCode,
                            city = billingCity,
                            country = billingCountry
                        )
                    },
                    paymentMethod = when (paymentMethodType) {
                        "PayPal" -> PaymentMethod.paypal(paypalEmail)
                        "IBAN" -> PaymentMethod.iban(iban)
                        else -> PaymentMethod.none()
                    }
                )
                onRegisterClick(userInput)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrieren")
        }
    }
}

data class UserInputData(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String?,
    val mobile: String?,
    val shippingAddress: Address,
    val billingAddress: Address,
    val paymentMethod: PaymentMethod
)