package de.syntax_institut.androidabschlussprojekt.ui.screen_profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.*
import de.syntax_institut.androidabschlussprojekt.ui.screen_login.LoginScreen
import de.syntax_institut.androidabschlussprojekt.ui.screen_profile.components.AddressFields
import de.syntax_institut.androidabschlussprojekt.ui.screen_profile.components.PaymentMethodSelector
import de.syntax_institut.androidabschlussprojekt.viewmodel.UserProfileViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun ProfileScreen(
    navController: NavController,
    userProfileViewModel: UserProfileViewModel = koinViewModel(),
    onLogout: () -> Unit = {}
) {
    val user by userProfileViewModel.user.collectAsState()
    val isSigningOut by userProfileViewModel.authViewModel.isSigningOut.collectAsState()
    var userLoaded by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }
    val isSaving by userProfileViewModel.isSaving.collectAsState()
    val errorMessage by userProfileViewModel.errorMessage.collectAsState()
    val accountDeleted by userProfileViewModel.accountDeleted.collectAsState()
    val context = LocalContext.current
    val openDialog = remember { mutableStateOf(false) }
    val addressDialogType by userProfileViewModel.addressDialogType.collectAsState()
    val addressForm by userProfileViewModel.addressForm.collectAsState()
    val editAddressId by userProfileViewModel.editAddressId.collectAsState()

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

    if (accountDeleted) {
        LaunchedEffect(Unit) {
            navController.navigate("home") {
                popUpTo("profile") { inclusive = true }
            }
        }
    }

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = { openDialog.value = false },
            title = { Text("Account wirklich löschen?") },
            text = { Text("Dein Account und alle Daten werden unwiderruflich gelöscht.") },
            confirmButton = {
                Button(
                    onClick = {
                        openDialog.value = false
                        userProfileViewModel.deleteUserAccount()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Ja, löschen") }
            },
            dismissButton = {
                Button(onClick = { openDialog.value = false }) { Text("Abbrechen") }
            }
        )
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

    var firstName by remember { mutableStateOf(user?.firstName ?: "") }
    var lastName by remember { mutableStateOf(user?.lastName ?: "") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var phone by remember { mutableStateOf(user?.phone ?: "") }
    var mobile by remember { mutableStateOf(user?.mobile ?: "") }

    var street by remember { mutableStateOf(user?.shippingAddress?.street ?: "") }
    var houseNumber by remember { mutableStateOf(user?.shippingAddress?.houseNumber ?: "") }
    var addressAddition by remember { mutableStateOf(user?.shippingAddress?.addressAddition ?: "") }
    var postalCode by remember { mutableStateOf(user?.shippingAddress?.postalCode ?: "") }
    var city by remember { mutableStateOf(user?.shippingAddress?.city ?: "") }
    var country by remember { mutableStateOf(user?.shippingAddress?.country ?: "DE") }

    var billingStreet by remember { mutableStateOf(user?.billingAddress?.street ?: "") }
    var billingHouseNumber by remember { mutableStateOf(user?.billingAddress?.houseNumber ?: "") }
    var billingAddressAddition by remember { mutableStateOf(user?.billingAddress?.addressAddition ?: "") }
    var billingPostalCode by remember { mutableStateOf(user?.billingAddress?.postalCode ?: "") }
    var billingCity by remember { mutableStateOf(user?.billingAddress?.city ?: "") }
    var billingCountry by remember { mutableStateOf(user?.billingAddress?.country ?: "DE") }

    var paymentMethodType by remember { mutableStateOf(user?.paymentMethod?.type ?: "None") }
    var paypalEmail by remember { mutableStateOf(user?.paymentMethod?.email ?: "") }
    var ibanNumber by remember { mutableStateOf(user?.paymentMethod?.iban ?: "") }

    val scrollState = rememberScrollState()

    // Adresslisten laden, wenn User geladen
    LaunchedEffect(user) {
        user?.id?.let { userProfileViewModel.loadAddresses(it) }
    }

    val shippingAddresses by userProfileViewModel.shippingAddresses.collectAsState()
    val billingAddresses by userProfileViewModel.billingAddresses.collectAsState()
    val defaultShippingAddressId by userProfileViewModel.defaultShippingAddressId.collectAsState()
    val defaultBillingAddressId by userProfileViewModel.defaultBillingAddressId.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Bottom).asPaddingValues())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Persönliche Daten", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    if (isEditing) {
                        OutlinedTextField(value = firstName, onValueChange = { firstName = it }, label = { Text("Vorname") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = lastName, onValueChange = { lastName = it }, label = { Text("Nachname") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("E-Mail") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Telefon") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = mobile, onValueChange = { mobile = it }, label = { Text("Mobil") }, modifier = Modifier.fillMaxWidth())
                    } else {
                        Text("Vorname: $firstName")
                        Text("Nachname: $lastName")
                        Text("E-Mail: $email")
                        if (phone.isNotBlank()) Text("Telefon: $phone")
                        if (mobile.isNotBlank()) Text("Mobil: $mobile")
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Lieferadresse", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    if (isEditing) {
                        OutlinedTextField(value = street, onValueChange = { street = it }, label = { Text("Straße") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = houseNumber, onValueChange = { houseNumber = it }, label = { Text("Hausnummer") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = addressAddition, onValueChange = { addressAddition = it }, label = { Text("Adresszusatz") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = postalCode, onValueChange = { postalCode = it }, label = { Text("PLZ") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("Stadt") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = country, onValueChange = { country = it }, label = { Text("Land") }, modifier = Modifier.fillMaxWidth())
                    } else {
                        Text("Straße: $street")
                        Text("Hausnummer: $houseNumber")
                        if (addressAddition.isNotBlank()) Text("Adresszusatz: $addressAddition")
                        Text("PLZ: $postalCode")
                        Text("Stadt: $city")
                        Text("Land: $country")
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Rechnungsadresse", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    if (isEditing) {
                        OutlinedTextField(value = billingStreet, onValueChange = { billingStreet = it }, label = { Text("Straße") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = billingHouseNumber, onValueChange = { billingHouseNumber = it }, label = { Text("Hausnummer") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = billingAddressAddition, onValueChange = { billingAddressAddition = it }, label = { Text("Adresszusatz") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = billingPostalCode, onValueChange = { billingPostalCode = it }, label = { Text("PLZ") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = billingCity, onValueChange = { billingCity = it }, label = { Text("Stadt") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = billingCountry, onValueChange = { billingCountry = it }, label = { Text("Land") }, modifier = Modifier.fillMaxWidth())
                    } else {
                        Text("Straße: $billingStreet")
                        Text("Hausnummer: $billingHouseNumber")
                        if (billingAddressAddition.isNotBlank()) Text("Adresszusatz: $billingAddressAddition")
                        Text("PLZ: $billingPostalCode")
                        Text("Stadt: $billingCity")
                        Text("Land: $billingCountry")
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Zahlungsmethode", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    if (isEditing) {
                        PaymentMethodSelector(
                            selectedMethod = paymentMethodType,
                            onMethodSelected = { paymentMethodType = it },
                            paypalEmail = paypalEmail,
                            onPaypalEmailChange = { paypalEmail = it },
                            ibanNumber = ibanNumber,
                            onIbanNumberChange = { ibanNumber = it }
                        )
                    } else {
                        Text("Typ: $paymentMethodType")
                        if (paymentMethodType == "PayPal") Text("PayPal: $paypalEmail")
                        if (paymentMethodType == "IBAN") Text("IBAN: $ibanNumber")
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Lieferadressen-Liste
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Lieferadressen", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.weight(1f))
                        Button(onClick = { userProfileViewModel.openAddShippingAddress() }) { Text("Hinzufügen") }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    shippingAddresses.forEach { (id, address) ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            RadioButton(
                                selected = id == defaultShippingAddressId,
                                onClick = { user?.id?.let { userProfileViewModel.setDefaultShippingAddress(it, id) } }
                            )
                            Column(Modifier.weight(1f)) {
                                Text("${address.street} ${address.houseNumber}, ${address.postalCode} ${address.city}")
                                if (!address.addressAddition.isNullOrBlank()) Text(address.addressAddition)
                            }
                            IconButton(onClick = { userProfileViewModel.openEditShippingAddress(id, address) }) { Icon(Icons.Default.Edit, contentDescription = "Bearbeiten") }
                            IconButton(onClick = { user?.id?.let { userProfileViewModel.deleteShippingAddress(it, id) } }) { Icon(Icons.Default.Delete, contentDescription = "Löschen") }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Rechnungsadressen-Liste
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Rechnungsadressen", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.weight(1f))
                        Button(onClick = { userProfileViewModel.openAddBillingAddress() }) { Text("Hinzufügen") }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    billingAddresses.forEach { (id, address) ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            RadioButton(
                                selected = id == defaultBillingAddressId,
                                onClick = { user?.id?.let { userProfileViewModel.setDefaultBillingAddress(it, id) } }
                            )
                            Column(Modifier.weight(1f)) {
                                Text("${address.street} ${address.houseNumber}, ${address.postalCode} ${address.city}")
                                if (!address.addressAddition.isNullOrBlank()) Text(address.addressAddition)
                            }
                            IconButton(onClick = { userProfileViewModel.openEditBillingAddress(id, address) }) { Icon(Icons.Default.Edit, contentDescription = "Bearbeiten") }
                            IconButton(onClick = { user?.id?.let { userProfileViewModel.deleteBillingAddress(it, id) } }) { Icon(Icons.Default.Delete, contentDescription = "Löschen") }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Dialog für Hinzufügen/Bearbeiten von Adressen (MVVM)
            if (addressDialogType != UserProfileViewModel.AddressDialogType.NONE) {
                val isEdit = addressDialogType == UserProfileViewModel.AddressDialogType.EDIT_SHIPPING || addressDialogType == UserProfileViewModel.AddressDialogType.EDIT_BILLING
                val isBilling = addressDialogType == UserProfileViewModel.AddressDialogType.ADD_BILLING || addressDialogType == UserProfileViewModel.AddressDialogType.EDIT_BILLING
                AlertDialog(
                    onDismissRequest = { userProfileViewModel.closeAddressDialog() },
                    title = { Text(if (isEdit) (if (isBilling) "Rechnungsadresse bearbeiten" else "Lieferadresse bearbeiten") else (if (isBilling) "Rechnungsadresse hinzufügen" else "Lieferadresse hinzufügen")) },
                    text = {
                        AddressFields(
                            street = addressForm.street,
                            onStreetChange = { userProfileViewModel.setAddressFormField(street = it) },
                            houseNumber = addressForm.houseNumber,
                            onHouseNumberChange = { userProfileViewModel.setAddressFormField(houseNumber = it) },
                            addressAddition = addressForm.addressAddition ?: "",
                            onAddressAdditionChange = { userProfileViewModel.setAddressFormField(addressAddition = it) },
                            postalCode = addressForm.postalCode,
                            onPostalCodeChange = { userProfileViewModel.setAddressFormField(postalCode = it) },
                            city = addressForm.city,
                            onCityChange = { userProfileViewModel.setAddressFormField(city = it) },
                            country = addressForm.country,
                            onCountryChange = { userProfileViewModel.setAddressFormField(country = it) }
                        )
                    },
                    confirmButton = {
                        Button(onClick = {
                            user?.id?.let { userProfileViewModel.saveAddress(it) }
                        }, enabled = userProfileViewModel.validateAddress()) { Text("Speichern") }
                    },
                    dismissButton = {
                        Button(onClick = { userProfileViewModel.closeAddressDialog() }) { Text("Abbrechen") }
                    }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (!isEditing) {
                    Button(onClick = { isEditing = true }, modifier = Modifier.weight(1f)) {
                        Text("Bearbeiten")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { openDialog.value = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Account löschen")
                    }
                } else {
                    Button(
                        onClick = {
                            val updatedUser = user!!.copy(
                                firstName = firstName,
                                lastName = lastName,
                                email = email,
                                phone = phone.takeIf { it.isNotBlank() },
                                mobile = mobile.takeIf { it.isNotBlank() },
                                shippingAddress = Address(
                                    street = street,
                                    houseNumber = houseNumber,
                                    addressAddition = addressAddition.takeIf { it.isNotBlank() },
                                    postalCode = postalCode,
                                    city = city,
                                    country = country
                                ),
                                billingAddress = Address(
                                    street = billingStreet,
                                    houseNumber = billingHouseNumber,
                                    addressAddition = billingAddressAddition.takeIf { it.isNotBlank() },
                                    postalCode = billingPostalCode,
                                    city = billingCity,
                                    country = billingCountry
                                ),
                                paymentMethod = when (paymentMethodType) {
                                    "PayPal" -> PaymentMethod.paypal(email = paypalEmail)
                                    "IBAN" -> PaymentMethod.iban(iban = ibanNumber)
                                    else -> PaymentMethod.none()
                                }
                            )
                            userProfileViewModel.updateUser(updatedUser)
                            isEditing = false
                        },
                        enabled = !isSaving,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Speichern")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { isEditing = false }, modifier = Modifier.weight(1f)) {
                        Text("Abbrechen")
                    }
                }
            }
            if (errorMessage != null) {
                Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}