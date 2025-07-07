package de.syntax_institut.androidabschlussprojekt.ui.screen_profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import de.syntax_institut.androidabschlussprojekt.ui.screen_login.LoginScreen
import de.syntax_institut.androidabschlussprojekt.viewmodel.UserProfileViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Dialog
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.PaymentMethod
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.PaymentMethodType
import de.syntax_institut.androidabschlussprojekt.ui.screen_checkout.components.PaymentMethodSection
import androidx.compose.ui.res.painterResource
import de.syntax_institut.androidabschlussprojekt.R
import androidx.compose.ui.res.stringResource
import de.syntax_institut.androidabschlussprojekt.ui.components.CountryPicker


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


    val paymentMethods by userProfileViewModel.paymentMethods.collectAsState()
    val defaultPaymentMethodId by userProfileViewModel.defaultPaymentMethodId.collectAsState()
    val defaultPayment = paymentMethods.find { it.first == defaultPaymentMethodId }?.second

    val defaultShippingAddressId by userProfileViewModel.defaultShippingAddressId.collectAsState()
    val defaultBillingAddressId by userProfileViewModel.defaultBillingAddressId.collectAsState()
    var useShippingAsBilling by remember { mutableStateOf(false) }


    var showEditDialog by remember { mutableStateOf<Pair<String, Boolean>?>(null) }
    var showDeleteDialog by remember { mutableStateOf<Pair<String, Boolean>?>(null) }
    var showPaymentDialog by remember { mutableStateOf(false) }
    var newPaymentMethod by remember { mutableStateOf(PaymentMethod.none()) }
    var paypalEmail by remember { mutableStateOf("") }
    var iban by remember { mutableStateOf("") }

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
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (!isEditing) {
                            Button(onClick = { isEditing = true }, modifier = Modifier.weight(1f)) {
                                Text("Bearbeiten")
                            }
                        } else {
                            Button(
                                onClick = {
                                    val currentUser = user
                                    if (currentUser != null) {
                                        val updatedUser = currentUser.copy(
                                            firstName = firstName,
                                            lastName = lastName,
                                            email = email,
                                            phone = phone.takeIf { it.isNotBlank() },
                                            mobile = mobile.takeIf { it.isNotBlank() }
                                        )
                                        userProfileViewModel.updateUser(updatedUser)
                                        isEditing = false
                                    }
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
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Standard-Lieferadresse", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    if (shippingAddresses.isEmpty()) {
                        Text("Keine Lieferadresse vorhanden.")
                    } else {
                        shippingAddresses.forEach { (id, address) ->
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                RadioButton(
                                    selected = id == defaultShippingAddressId,
                                    onClick = {
                                        val currentUser = user
                                        val userId = currentUser?.id
                                        if (userId != null) {
                                            userProfileViewModel.setDefaultShippingAddress(userId, id)
                                        }
                                    }
                                )
                                Column(Modifier.weight(1f)) {
                                    Text("Name: ${address.recipientFirstName} ${address.recipientLastName}")
                                    Text("Straße: ${address.street} ${address.houseNumber}")
                                    if (!address.addressAddition.isNullOrBlank()) Text("Zusatz: ${address.addressAddition}")
                                    Text("PLZ: ${address.postalCode}")
                                    Text("Stadt: ${address.city}")
                                    Text("Land: ${address.country}")
                                }
                                IconButton(onClick = { showEditDialog = id to false }) { Icon(Icons.Default.Edit, contentDescription = "Bearbeiten") }
                                if (shippingAddresses.size > 1) {
                                    IconButton(onClick = { showDeleteDialog = id to false }) { Icon(Icons.Default.Delete, contentDescription = "Löschen") }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { userProfileViewModel.openAddShippingAddress() }) { Text("Hinzufügen") }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Standard-Rechnungsadresse", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    val hasBillingAddresses = billingAddresses.isNotEmpty()
                    val isCheckboxEnabled = hasBillingAddresses
                    val isCheckboxChecked = if (!hasBillingAddresses) true else useShippingAsBilling
                    Checkbox(
                        checked = isCheckboxChecked,
                        onCheckedChange = {
                            if (isCheckboxEnabled) useShippingAsBilling = it
                            val currentUser = user
                            val userId = currentUser?.id
                            val shippingId = defaultShippingAddressId
                            if (it && userId != null && shippingId != null) {
                                userProfileViewModel.setDefaultBillingAddress(userId, shippingId)
                            }
                        },
                        enabled = isCheckboxEnabled
                    )
                    if (isCheckboxChecked) {
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { userProfileViewModel.openAddBillingAddress() }) { Text("Andere Rechnungsadresse erstellen") }
                    } else {
                        if (billingAddresses.isEmpty()) {
                            Text("Keine Rechnungsadresse vorhanden.")
                        } else {
                            billingAddresses.forEach { (id, address) ->
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                    RadioButton(
                                        selected = id == defaultBillingAddressId,
                                        onClick = {
                                            val currentUser = user
                                            val userId = currentUser?.id
                                            if (userId != null) {
                                                userProfileViewModel.setDefaultBillingAddress(userId, id)
                                            }
                                        }
                                    )
                                    Column(Modifier.weight(1f)) {
                                        Text("Name: ${address.recipientFirstName} ${address.recipientLastName}")
                                        Text("Straße: ${address.street} ${address.houseNumber}")
                                        if (!address.addressAddition.isNullOrBlank()) Text("Zusatz: ${address.addressAddition}")
                                        Text("PLZ: ${address.postalCode}")
                                        Text("Stadt: ${address.city}")
                                        Text("Land: ${address.country}")
                                    }
                                    IconButton(onClick = { showEditDialog = id to true }) { Icon(Icons.Default.Edit, contentDescription = "Bearbeiten") }
                                    IconButton(onClick = { showDeleteDialog = id to true }) { Icon(Icons.Default.Delete, contentDescription = "Löschen") }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { userProfileViewModel.openAddBillingAddress() }) { Text("Hinzufügen") }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Zahlungsmethode", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    if (defaultPayment != null) {
                        Text("Typ: " + when (defaultPayment.type) {
                            de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.PaymentMethodType.PAYPAL -> "PayPal"
                            de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.PaymentMethodType.ABBUCHUNG -> "Abbuchung"
                            de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.PaymentMethodType.UEBERWEISUNG -> "Überweisung"
                            de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.PaymentMethodType.NACHNAHME -> "Nachnahme"
                            de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.PaymentMethodType.VISA -> "Visa"
                            de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.PaymentMethodType.AMAZON_PAY -> "Amazon Pay"
                        })
                        if (defaultPayment.email.isNotBlank()) Text("PayPal: ${defaultPayment.email}")
                        if (defaultPayment.iban.isNotBlank()) Text("IBAN: ${defaultPayment.iban}")
                    } else {
                        Text("Keine Standard-Zahlungsart ausgewählt.")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { showPaymentDialog = true }) {
                        Text("Zahlungsart hinzufügen")
                    }
                    if (showPaymentDialog) {
                        Dialog(onDismissRequest = { showPaymentDialog = false }) {
                            Surface(shape = MaterialTheme.shapes.medium, tonalElevation = 8.dp) {
                                Column(Modifier.padding(24.dp).widthIn(min = 320.dp, max = 480.dp)) {
                                    Text("Zahlungsart auswählen", style = MaterialTheme.typography.titleLarge)
                                    Spacer(Modifier.height(16.dp))
                                    PaymentMethodSection(
                                        selectedMethod = newPaymentMethod,
                                        onMethodSelect = { newPaymentMethod = it }
                                    )
                                    when (newPaymentMethod.type) {
                                        PaymentMethodType.PAYPAL -> {
                                            OutlinedTextField(
                                                value = paypalEmail,
                                                onValueChange = { paypalEmail = it },
                                                label = { Text("PayPal Email") },
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                        PaymentMethodType.UEBERWEISUNG -> {
                                            OutlinedTextField(
                                                value = iban,
                                                onValueChange = { iban = it },
                                                label = { Text("IBAN") },
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                        else -> {}
                                    }
                                    Spacer(Modifier.height(16.dp))
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                        Button(onClick = { showPaymentDialog = false }) { Text("Abbrechen") }
                                        Spacer(Modifier.width(8.dp))
                                        Button(onClick = {
                                            val currentUser = user
                                            val userId = currentUser?.id
                                            val paymentToSave = when (newPaymentMethod.type) {
                                                PaymentMethodType.PAYPAL -> newPaymentMethod.copy(email = paypalEmail)
                                                PaymentMethodType.UEBERWEISUNG -> newPaymentMethod.copy(iban = iban)
                                                else -> newPaymentMethod
                                            }
                                            if (userId != null) {
                                                userProfileViewModel.addPayment(userId, paymentToSave)
                                            }
                                            showPaymentDialog = false
                                            paypalEmail = ""
                                            iban = ""
                                            newPaymentMethod = PaymentMethod.none()
                                        }) { Text("Speichern") }
                                    }
                                }
                            }
                        }
                    }
                    paymentMethods.forEachIndexed { index, (id, payment) ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            RadioButton(
                                selected = id == defaultPaymentMethodId,
                                onClick = { userProfileViewModel.setDefaultPaymentMethod(id) }
                            )
                            // Icon + Name wie im Dropdown
                            val iconMap = mapOf(
                                PaymentMethodType.ABBUCHUNG to Icons.Default.AccountBalance,
                                PaymentMethodType.UEBERWEISUNG to Icons.Default.SyncAlt,
                                PaymentMethodType.NACHNAHME to Icons.Default.AttachMoney
                            )
                            val vectorIconMap = mapOf(
                                PaymentMethodType.PAYPAL to R.drawable.ic_paypal,
                                PaymentMethodType.VISA to R.drawable.ic_visa,
                                PaymentMethodType.AMAZON_PAY to R.drawable.ic_amazon_pay
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                when (payment.type) {
                                    PaymentMethodType.PAYPAL, PaymentMethodType.VISA, PaymentMethodType.AMAZON_PAY ->
                                        Image(painter = painterResource(vectorIconMap[payment.type]!!), contentDescription = null, modifier = Modifier.size(32.dp))
                                    else ->
                                        Icon(iconMap[payment.type] ?: Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(32.dp))
                                }
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    when (payment.type) {
                                        PaymentMethodType.PAYPAL -> "PayPal"
                                        PaymentMethodType.ABBUCHUNG -> "Abbuchung"
                                        PaymentMethodType.UEBERWEISUNG -> "Überweisung"
                                        PaymentMethodType.NACHNAHME -> "Nachnahme"
                                        PaymentMethodType.VISA -> "Visa"
                                        PaymentMethodType.AMAZON_PAY -> "Amazon Pay"
                                    },
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                            Spacer(Modifier.weight(1f))

                            if (paymentMethods.size > 1) {
                                IconButton(onClick = {
                                    userProfileViewModel.deletePayment(user!!.id!!, id)

                                    if (id == defaultPaymentMethodId) {
                                        val next = paymentMethods.filter { it.first != id }.firstOrNull()?.first
                                        if (next != null) userProfileViewModel.setDefaultPaymentMethod(next)
                                    }
                                }) { Icon(Icons.Default.Delete, contentDescription = "Löschen") }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (addressDialogType != UserProfileViewModel.AddressDialogType.NONE) {
                val isEdit = addressDialogType == UserProfileViewModel.AddressDialogType.EDIT_SHIPPING || addressDialogType == UserProfileViewModel.AddressDialogType.EDIT_BILLING
                val isBilling = addressDialogType == UserProfileViewModel.AddressDialogType.ADD_BILLING || addressDialogType == UserProfileViewModel.AddressDialogType.EDIT_BILLING
                Dialog(onDismissRequest = { userProfileViewModel.closeAddressDialog() }) {
                    Surface(shape = MaterialTheme.shapes.medium, tonalElevation = 8.dp) {
                        Column(Modifier.padding(24.dp).widthIn(min = 320.dp, max = 480.dp)) {
                            Text(if (isEdit) (if (isBilling) "Rechnungsadresse bearbeiten" else "Lieferadresse bearbeiten") else (if (isBilling) "Rechnungsadresse hinzufügen" else "Lieferadresse hinzufügen"), style = MaterialTheme.typography.titleLarge)
                            Spacer(Modifier.height(16.dp))
                            OutlinedTextField(value = addressForm.recipientFirstName, onValueChange = { userProfileViewModel.setAddressFormField(recipientFirstName = it) }, label = { Text("Vorname") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = addressForm.recipientLastName, onValueChange = { userProfileViewModel.setAddressFormField(recipientLastName = it) }, label = { Text("Nachname") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = addressForm.street, onValueChange = { userProfileViewModel.setAddressFormField(street = it) }, label = { Text("Straße") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = addressForm.houseNumber, onValueChange = { userProfileViewModel.setAddressFormField(houseNumber = it) }, label = { Text("Hausnummer") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = addressForm.addressAddition ?: "", onValueChange = { userProfileViewModel.setAddressFormField(addressAddition = it) }, label = { Text("Adresszusatz") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = addressForm.postalCode, onValueChange = { userProfileViewModel.setAddressFormField(postalCode = it) }, label = { Text("PLZ") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = addressForm.city, onValueChange = { userProfileViewModel.setAddressFormField(city = it) }, label = { Text("Stadt") }, modifier = Modifier.fillMaxWidth())
                            CountryPicker(
                                value = addressForm.country,
                                onValueChange = { userProfileViewModel.setAddressFormField(country = it) },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(16.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                Button(onClick = { userProfileViewModel.closeAddressDialog() }) { Text("Abbrechen") }
                                Spacer(Modifier.width(8.dp))
                                Button(onClick = {
                                    val currentUser = user
                                    val userId = currentUser?.id
                                    if (userId != null) {
                                        userProfileViewModel.saveAddress(userId)
                                    }
                                }, enabled = userProfileViewModel.validateAddress()) { Text("Speichern") }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            if (errorMessage != null) {
                Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
            }
        }
    }


    if (showEditDialog != null) {
        val (addressId, isBilling) = showEditDialog!!
        AlertDialog(
            onDismissRequest = { showEditDialog = null },
            title = { Text("Adresse bearbeiten") },
            text = { Text("Willst du diese Adresse wirklich bearbeiten?") },
            confirmButton = {
                Button(onClick = {
                    if (isBilling) {
                        val address = billingAddresses.find { it.first == addressId }?.second
                        if (address != null) userProfileViewModel.openEditBillingAddress(addressId, address)
                    } else {
                        val address = shippingAddresses.find { it.first == addressId }?.second
                        if (address != null) userProfileViewModel.openEditShippingAddress(addressId, address)
                    }
                    showEditDialog = null
                }) { Text("Bearbeiten") }
            },
            dismissButton = {
                Button(onClick = { showEditDialog = null }) { Text("Abbrechen") }
            }
        )
    }
    if (showDeleteDialog != null) {
        val (addressId, isBilling) = showDeleteDialog!!
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Adresse löschen") },
            text = { Text("Willst du diese Adresse wirklich löschen?") },
            confirmButton = {
                Button(onClick = {
                    val currentUser = user
                    val userId = currentUser?.id
                    if (userId != null) {
                        if (isBilling) {
                            userProfileViewModel.deleteBillingAddress(userId, addressId)
                        } else {
                            userProfileViewModel.deleteShippingAddress(userId, addressId)
                        }
                    }
                    showDeleteDialog = null
                }) { Text("Löschen") }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog = null }) { Text("Abbrechen") }
            }
        )
    }
}