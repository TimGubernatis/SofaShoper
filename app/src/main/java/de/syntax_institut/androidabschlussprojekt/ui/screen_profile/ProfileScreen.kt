package de.syntax_institut.androidabschlussprojekt.ui.screen_profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.*
import de.syntax_institut.androidabschlussprojekt.ui.screen_profile.components.AddressFields
import de.syntax_institut.androidabschlussprojekt.ui.screen_profile.components.PaymentMethodSelector
import de.syntax_institut.androidabschlussprojekt.viewmodel.UserProfileViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    userProfileViewModel: UserProfileViewModel = koinViewModel(),
    onLogout: () -> Unit
) {
    val user by userProfileViewModel.user.collectAsState()
    val isSaving by userProfileViewModel.isSaving.collectAsState()
    val errorMessage by userProfileViewModel.errorMessage.collectAsState()

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }

    var street by remember { mutableStateOf("") }
    var houseNumber by remember { mutableStateOf("") }
    var addressAddition by remember { mutableStateOf("") }
    var postalCode by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("DE") }

    var billingStreet by remember { mutableStateOf("") }
    var billingHouseNumber by remember { mutableStateOf("") }
    var billingAddressAddition by remember { mutableStateOf("") }
    var billingPostalCode by remember { mutableStateOf("") }
    var billingCity by remember { mutableStateOf("") }
    var billingCountry by remember { mutableStateOf("DE") }

    var paymentMethodType by remember { mutableStateOf("None") } // "PayPal", "IBAN", "None"
    var paypalEmail by remember { mutableStateOf("") }
    var ibanNumber by remember { mutableStateOf("") }

    LaunchedEffect(user) {
        user?.let {
            firstName = it.firstName
            lastName = it.lastName
            email = it.email
            phone = it.phone ?: ""
            mobile = it.mobile ?: ""

            street = it.shippingAddress.street
            houseNumber = it.shippingAddress.houseNumber
            addressAddition = it.shippingAddress.addressAddition ?: ""
            postalCode = it.shippingAddress.postalCode
            city = it.shippingAddress.city
            country = it.shippingAddress.country

            billingStreet = it.billingAddress.street
            billingHouseNumber = it.billingAddress.houseNumber
            billingAddressAddition = it.billingAddress.addressAddition ?: ""
            billingPostalCode = it.billingAddress.postalCode
            billingCity = it.billingAddress.city
            billingCountry = it.billingAddress.country

            when (val pm = it.paymentMethod) {
                is PaymentMethod.PayPal -> {
                    paymentMethodType = "PayPal"
                    paypalEmail = pm.email
                }
                is PaymentMethod.IBAN -> {
                    paymentMethodType = "IBAN"
                    ibanNumber = pm.iban
                }
                PaymentMethod.None, null -> {
                    paymentMethodType = "None"
                    paypalEmail = ""
                    ibanNumber = ""
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Profil bearbeiten", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("Vorname") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Nachname") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Telefon") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = mobile,
                onValueChange = { mobile = it },
                label = { Text("Handy") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            Spacer(Modifier.height(16.dp))

            Text(text = "Lieferadresse", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            AddressFields(
                street = street,
                onStreetChange = { street = it },
                houseNumber = houseNumber,
                onHouseNumberChange = { houseNumber = it },
                addressAddition = addressAddition,
                onAddressAdditionChange = { addressAddition = it },
                postalCode = postalCode,
                onPostalCodeChange = { postalCode = it },
                city = city,
                onCityChange = { city = it },
                country = country,
                onCountryChange = { country = it }
            )
            Spacer(Modifier.height(16.dp))

            Text(text = "Rechnungsadresse", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            AddressFields(
                street = billingStreet,
                onStreetChange = { billingStreet = it },
                houseNumber = billingHouseNumber,
                onHouseNumberChange = { billingHouseNumber = it },
                addressAddition = billingAddressAddition,
                onAddressAdditionChange = { billingAddressAddition = it },
                postalCode = billingPostalCode,
                onPostalCodeChange = { billingPostalCode = it },
                city = billingCity,
                onCityChange = { billingCity = it },
                country = billingCountry,
                onCountryChange = { billingCountry = it }
            )
            Spacer(Modifier.height(16.dp))

            Text(text = "Zahlungsmethode", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            PaymentMethodSelector(
                selectedMethod = paymentMethodType,
                onMethodSelected = { paymentMethodType = it },
                paypalEmail = paypalEmail,
                onPaypalEmailChange = { paypalEmail = it },
                ibanNumber = ibanNumber,
                onIbanNumberChange = { ibanNumber = it }
            )
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    val shippingAddress = Address(
                        street = street,
                        houseNumber = houseNumber,
                        addressAddition = if (addressAddition.isBlank()) null else addressAddition,
                        postalCode = postalCode,
                        city = city,
                        country = country
                    )
                    val billingAddress = Address(
                        street = billingStreet,
                        houseNumber = billingHouseNumber,
                        addressAddition = if (billingAddressAddition.isBlank()) null else billingAddressAddition,
                        postalCode = billingPostalCode,
                        city = billingCity,
                        country = billingCountry
                    )
                    val paymentMethod = when (paymentMethodType) {
                        "PayPal" -> PaymentMethod.PayPal(email = paypalEmail)
                        "IBAN" -> PaymentMethod.IBAN(iban = ibanNumber)
                        else -> PaymentMethod.None
                    }
                    user?.let {
                        val updatedUser = it.copy(
                            firstName = firstName,
                            lastName = lastName,
                            email = email,
                            phone = phone.takeIf { it.isNotBlank() },
                            mobile = mobile.takeIf { it.isNotBlank() },
                            shippingAddress = shippingAddress,
                            billingAddress = billingAddress,
                            paymentMethod = paymentMethod
                        )
                        userProfileViewModel.updateUser(updatedUser)
                    }
                },
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isSaving) "Speichern..." else "Speichern")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Logout")
            }

            errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}