package de.syntax_institut.androidabschlussprojekt.ui.screen_profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import de.syntax_institut.androidabschlussprojekt.ui.screen_login.LoginScreen
import de.syntax_institut.androidabschlussprojekt.viewmodel.UserProfileViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SupportAgent
import de.syntax_institut.androidabschlussprojekt.ui.screen_profile.components.ProfilePersonalSection
import de.syntax_institut.androidabschlussprojekt.ui.screen_profile.components.ProfileAddressSection
import de.syntax_institut.androidabschlussprojekt.ui.screen_profile.components.ProfilePaymentSection
import de.syntax_institut.androidabschlussprojekt.ui.screen_profile.components.AddPaymentDialog
import de.syntax_institut.androidabschlussprojekt.ui.screen_profile.components.AddressDialog
import de.syntax_institut.androidabschlussprojekt.ui.screen_profile.components.ConfirmationDialog
import de.syntax_institut.androidabschlussprojekt.ui.screen_profile.components.OrderList

@Composable
fun ProfileScreen(
    navController: NavController,
    userProfileViewModel: UserProfileViewModel = koinViewModel(),
    onLogout: () -> Unit = {}
) {
    val user by userProfileViewModel.user.collectAsState()
    val currentUser = user
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
    var selectedTab by remember { mutableStateOf(0) }
    LaunchedEffect(currentUser, isSigningOut) {
        if (currentUser != null) {
            userLoaded = true
        }
        if (!isSigningOut && userLoaded && currentUser == null) {
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
    ConfirmationDialog(
        showDialog = openDialog.value,
        title = "Account wirklich löschen?",
        message = "Dein Account und alle Daten werden unwiderruflich gelöscht.",
        confirmText = "Ja, löschen",
        onConfirm = { userProfileViewModel.deleteUserAccount() },
        onDismiss = { openDialog.value = false },
        isDestructive = true
    )
    if (currentUser == null) {
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
    var firstName by remember { mutableStateOf(currentUser.firstName ?: "") }
    var lastName by remember { mutableStateOf(currentUser.lastName ?: "") }
    var email by remember { mutableStateOf(currentUser.email ?: "") }
    var phone by remember { mutableStateOf(currentUser.phone ?: "") }
    var mobile by remember { mutableStateOf(currentUser.mobile ?: "") }
    LaunchedEffect(currentUser) {
        currentUser.id?.let {
            userProfileViewModel.loadAddresses(it)
            userProfileViewModel.loadPayments(it)
        }
    }
    val shippingAddresses by userProfileViewModel.shippingAddresses.collectAsState()
    val billingAddresses by userProfileViewModel.billingAddresses.collectAsState()
    val scrollState = rememberScrollState()
    LaunchedEffect(selectedTab, currentUser.id) {
        if (selectedTab == 1 && currentUser.id != null) {
            userProfileViewModel.loadOrders(currentUser.id!!)
        }
    }
    val orders by userProfileViewModel.orders.collectAsState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Bottom).asPaddingValues())
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Zurück",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = { selectedTab = 0 }) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Profil",
                            tint = if (selectedTab == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text("Profil", style = MaterialTheme.typography.labelSmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = { selectedTab = 1 }) {
                        Icon(
                            imageVector = Icons.Filled.ShoppingBag,
                            contentDescription = "Bestellungen",
                            tint = if (selectedTab == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text("Bestellungen", style = MaterialTheme.typography.labelSmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = { selectedTab = 2 }) {
                        Icon(
                            imageVector = Icons.Filled.SupportAgent,
                            contentDescription = "Support",
                            tint = if (selectedTab == 2) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text("Support", style = MaterialTheme.typography.labelSmall)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        when (selectedTab) {
            0 -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.Start
                ) {
                    ProfilePersonalSection(
                        firstName = firstName,
                        lastName = lastName,
                        email = email,
                        phone = phone,
                        mobile = mobile,
                        isEditing = isEditing,
                        onFirstNameChange = { firstName = it },
                        onLastNameChange = { lastName = it },
                        onEmailChange = { email = it },
                        onPhoneChange = { phone = it },
                        onMobileChange = { mobile = it },
                        onEditClick = { isEditing = true },
                        onSaveClick = {
                            val updatedUser = currentUser.copy(
                                firstName = firstName,
                                lastName = lastName,
                                email = email,
                                phone = phone.takeIf { it.isNotBlank() },
                                mobile = mobile.takeIf { it.isNotBlank() }
                            )
                            userProfileViewModel.updateUser(updatedUser)
                            isEditing = false
                        },
                        onCancelClick = { isEditing = false },
                        isSaving = isSaving
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    ProfileAddressSection(
                        title = "Standard-Lieferadresse",
                        addresses = shippingAddresses,
                        defaultAddressId = defaultShippingAddressId,
                        onSetDefault = { userProfileViewModel.setDefaultShippingAddress(currentUser.id!!, it) },
                        onAdd = { userProfileViewModel.openAddShippingAddress() },
                        onEdit = { id, address -> userProfileViewModel.openEditShippingAddress(id, address) },
                        onDelete = { userProfileViewModel.deleteShippingAddress(currentUser.id!!, it) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    ProfileAddressSection(
                        title = "Standard-Rechnungsadresse",
                        addresses = billingAddresses,
                        defaultAddressId = defaultBillingAddressId,
                        onSetDefault = { userProfileViewModel.setDefaultBillingAddress(currentUser.id!!, it) },
                        onAdd = { userProfileViewModel.openAddBillingAddress() },
                        onEdit = { id, address -> userProfileViewModel.openEditBillingAddress(id, address) },
                        onDelete = { userProfileViewModel.deleteBillingAddress(currentUser.id!!, it) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    ProfilePaymentSection(
                        paymentMethods = paymentMethods,
                        defaultPaymentMethodId = defaultPaymentMethodId,
                        onSetDefault = { userProfileViewModel.setDefaultPaymentMethod(it) },
                        onAdd = { showPaymentDialog = true },
                        onDelete = { userProfileViewModel.deletePayment(currentUser.id!!, it) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    AddressDialog(
                        dialogType = addressDialogType,
                        addressForm = addressForm,
                        onDismiss = { userProfileViewModel.closeAddressDialog() },
                        onSave = { userProfileViewModel.saveAddress(currentUser.id!!) },
                        onFieldChange = { userProfileViewModel.setAddressFormField(
                            recipientFirstName = it.recipientFirstName,
                            recipientLastName = it.recipientLastName,
                            street = it.street,
                            houseNumber = it.houseNumber,
                            addressAddition = it.addressAddition,
                            postalCode = it.postalCode,
                            city = it.city,
                            country = it.country
                        ) },
                        isValid = userProfileViewModel.validateAddress()
                    )
                    if (showDeleteDialog != null) {
                        val (addressId, isBilling) = showDeleteDialog!!
                        ConfirmationDialog(
                            showDialog = true,
                            title = if (isBilling) "Rechnungsadresse löschen?" else "Lieferadresse löschen?",
                            message = "Diese Adresse wird unwiderruflich gelöscht.",
                            confirmText = "Löschen",
                            onConfirm = {
                                val userId = currentUser.id
                                if (userId != null) {
                                    if (isBilling) userProfileViewModel.deleteBillingAddress(userId, addressId)
                                    else userProfileViewModel.deleteShippingAddress(userId, addressId)
                                }
                                showDeleteDialog = null
                            },
                            onDismiss = { showDeleteDialog = null },
                            isDestructive = true
                        )
                    }
                    AddPaymentDialog(
                        showDialog = showPaymentDialog,
                        onDismiss = { showPaymentDialog = false },
                        onSave = { paymentMethod ->
                            val userId = currentUser.id
                            if (userId != null) {
                                userProfileViewModel.addPayment(userId, paymentMethod)
                            }
                            showPaymentDialog = false
                        }
                    )
                }
            }
            1 -> {
                OrderList(orders = orders, modifier = Modifier.padding(16.dp))
            }
            2 -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text("Support-Bereich (bald verfügbar)", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}