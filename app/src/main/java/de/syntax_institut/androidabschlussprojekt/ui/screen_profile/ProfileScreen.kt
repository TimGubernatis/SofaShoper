package de.syntax_institut.androidabschlussprojekt.ui.screen_profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.*
import de.syntax_institut.androidabschlussprojekt.ui.screen_profile.components.AddressFields
import de.syntax_institut.androidabschlussprojekt.ui.screen_profile.components.PaymentMethodSelector
import de.syntax_institut.androidabschlussprojekt.ui.screen_profile.components.UserInfoSection
import de.syntax_institut.androidabschlussprojekt.viewmodel.UserProfileViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.navigation.NavController
import de.syntax_institut.androidabschlussprojekt.ui.screen_login.LoginScreen

@Composable
fun ProfileScreen(
    navController: NavController,
    userProfileViewModel: UserProfileViewModel = koinViewModel(),
    onLogout: () -> Unit = {}
) {
    val user by userProfileViewModel.user.collectAsState()
    val isSigningOut by userProfileViewModel.authViewModel.isSigningOut.collectAsState()
    var userLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(user, isSigningOut) {
        if (user != null) {
            userLoaded = true
        }
        if (!isSigningOut && userLoaded && user == null) {
            navController.navigate("home") {
                popUpTo("profile") { inclusive = true }
            }
        }
    }

    if (user == null) {
        LoginScreen(
            onLoginSuccess = {},
            onCancel = {
                navController.navigate("home") {
                    popUpTo("profile") { inclusive = true }
                }
            }
        )
        return
    }

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

    var paymentMethodType by remember { mutableStateOf("None") }
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

            val pm = it.paymentMethod
            if (pm != null) {
                paymentMethodType = pm.type
                paypalEmail = pm.email
                ibanNumber = pm.iban
            } else {
                paymentMethodType = "None"
                paypalEmail = ""
                ibanNumber = ""
            }
        }
    }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                WindowInsets.safeDrawing
                    .only(WindowInsetsSides.Top + WindowInsetsSides.Bottom)
                    .asPaddingValues()
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(start = 16.dp, end = 16.dp)
                .padding(end = (LocalConfiguration.current.screenWidthDp * 0.1f).dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            UserInfoSection(
                firstName = firstName,
                onFirstNameChange = { firstName = it },
                lastName = lastName,
                onLastNameChange = { lastName = it },
                email = email,
                onEmailChange = { email = it },
                phone = phone,
                onPhoneChange = { phone = it },
                mobile = mobile,
                onMobileChange = { mobile = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Lieferadresse", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

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

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Rechnungsadresse", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

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

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Zahlungsmethode", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            PaymentMethodSelector(
                selectedMethod = paymentMethodType,
                onMethodSelected = { paymentMethodType = it },
                paypalEmail = paypalEmail,
                onPaypalEmailChange = { paypalEmail = it },
                ibanNumber = ibanNumber,
                onIbanNumberChange = { ibanNumber = it }
            )

            Spacer(modifier = Modifier.height(72.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            if (!errorMessage.isNullOrEmpty()) {
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        userProfileViewModel.signOut()
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !isSigningOut
                ) {
                    Text(if (isSigningOut) "Logging out..." else "Logout")
                }

                Spacer(modifier = Modifier.width(16.dp))

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
                            "PayPal" -> PaymentMethod.paypal(email = paypalEmail)
                            "IBAN" -> PaymentMethod.iban(iban = ibanNumber)
                            else -> PaymentMethod.none()
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
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (isSaving) "Speichern..." else "Speichern")
                }
            }
        }
    }
}