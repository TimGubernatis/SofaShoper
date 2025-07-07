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
import de.syntax_institut.androidabschlussprojekt.ui.screen_login.LoginScreen
import de.syntax_institut.androidabschlussprojekt.ui.screen_profile.components.AddressFields
import de.syntax_institut.androidabschlussprojekt.viewmodel.UserProfileViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.style.TextOverflow


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

    // Zahlungsarten laden
    val paymentMethods by userProfileViewModel.paymentMethods.collectAsState()
    val defaultPaymentMethodId by userProfileViewModel.defaultPaymentMethodId.collectAsState()
    val defaultPayment = paymentMethods.find { it.first == defaultPaymentMethodId }?.second

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


    LaunchedEffect(user) {
        user?.id?.let {
            userProfileViewModel.loadAddresses(it)
            userProfileViewModel.loadPayments(it)
        }
    }
    val shippingAddresses by userProfileViewModel.shippingAddresses.collectAsState()
    val billingAddresses by userProfileViewModel.billingAddresses.collectAsState()
    val defaultShippingAddress = null
    val defaultBillingAddress = null

    val scrollState = rememberScrollState()

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
                    Text("Keine Standard-Lieferadresse ausgewählt.")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Rechnungsadresse", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Keine Standard-Rechnungsadresse ausgewählt.")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Zahlungsmethode", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    if (defaultPayment != null) {
                        Text("Typ: ${defaultPayment.type}")
                        when (defaultPayment.type) {
                            "PayPal" -> Text("PayPal: ${defaultPayment.email}")
                            "IBAN" -> Text("IBAN: ${defaultPayment.iban}")
                            "CREDIT_CARD" -> Text("Kreditkarte")
                            "CASH_ON_DELIVERY" -> Text("Nachnahme")
                        }
                    } else {
                        Text("Keine Standard-Zahlungsart ausgewählt.")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { userProfileViewModel.addPayment(user!!.id!!, de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.PaymentMethod()) }) {
                        Text("Zahlungsart hinzufügen")
                    }
                    paymentMethods.forEach { (id, payment) ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            RadioButton(
                                selected = id == defaultPaymentMethodId,
                                onClick = { userProfileViewModel.setDefaultPaymentMethod(id) }
                            )
                            Column(Modifier.weight(1f)) {
                                Text("Typ: ${payment.type}")
                                when (payment.type) {
                                    "PayPal" -> Text("PayPal: ${payment.email}")
                                    "IBAN" -> Text("IBAN: ${payment.iban}")
                                    "CREDIT_CARD" -> Text("Kreditkarte")
                                    "CASH_ON_DELIVERY" -> Text("Nachnahme")
                                }
                            }
                            IconButton(onClick = { userProfileViewModel.deletePayment(user!!.id!!, id) }) { Icon(Icons.Default.Delete, contentDescription = "Löschen") }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))


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
                                selected = false,
                                onClick = { }
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
                                selected = false,
                                onClick = { }
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
                                mobile = mobile.takeIf { it.isNotBlank() }
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