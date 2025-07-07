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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import de.syntax_institut.androidabschlussprojekt.ui.screen_profile.components.ProfilePersonalSection
import de.syntax_institut.androidabschlussprojekt.ui.screen_profile.components.ProfileAddressSection
import de.syntax_institut.androidabschlussprojekt.ui.screen_profile.components.ProfilePaymentSection
import de.syntax_institut.androidabschlussprojekt.ui.screen_profile.components.AddPaymentDialog
import de.syntax_institut.androidabschlussprojekt.ui.screen_profile.components.AddressDialog
import de.syntax_institut.androidabschlussprojekt.ui.screen_profile.components.ConfirmationDialog


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

    ConfirmationDialog(
        showDialog = openDialog.value,
        title = "Account wirklich löschen?",
        message = "Dein Account und alle Daten werden unwiderruflich gelöscht.",
        confirmText = "Ja, löschen",
        onConfirm = { userProfileViewModel.deleteUserAccount() },
        onDismiss = { openDialog.value = false },
        isDestructive = true
    )

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
                onCancelClick = { isEditing = false },
                isSaving = isSaving
            )
            Spacer(modifier = Modifier.height(16.dp))

            ProfileAddressSection(
                title = "Standard-Lieferadresse",
                addresses = shippingAddresses,
                defaultAddressId = defaultShippingAddressId,
                onSetDefault = { userProfileViewModel.setDefaultShippingAddress(user!!.id!!, it) },
                onAdd = { userProfileViewModel.openAddShippingAddress() },
                onEdit = { id, address -> userProfileViewModel.openEditShippingAddress(id, address) },
                onDelete = { userProfileViewModel.deleteShippingAddress(user!!.id!!, it) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            ProfileAddressSection(
                title = "Standard-Rechnungsadresse",
                addresses = billingAddresses,
                defaultAddressId = defaultBillingAddressId,
                onSetDefault = { userProfileViewModel.setDefaultBillingAddress(user!!.id!!, it) },
                onAdd = { userProfileViewModel.openAddBillingAddress() },
                onEdit = { id, address -> userProfileViewModel.openEditBillingAddress(id, address) },
                onDelete = { userProfileViewModel.deleteBillingAddress(user!!.id!!, it) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            ProfilePaymentSection(
                paymentMethods = paymentMethods,
                defaultPaymentMethodId = defaultPaymentMethodId,
                onSetDefault = { userProfileViewModel.setDefaultPaymentMethod(it) },
                onAdd = { showPaymentDialog = true },
                onDelete = { userProfileViewModel.deletePayment(user!!.id!!, it) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            AddressDialog(
                dialogType = addressDialogType,
                addressForm = addressForm,
                onDismiss = { userProfileViewModel.closeAddressDialog() },
                onSave = {
                    val currentUser = user
                    val userId = currentUser?.id
                    if (userId != null) {
                        userProfileViewModel.saveAddress(userId)
                    }
                },
                onFieldChange = { newAddress ->
                    userProfileViewModel.setAddressFormField(
                        recipientFirstName = newAddress.recipientFirstName,
                        recipientLastName = newAddress.recipientLastName,
                        street = newAddress.street,
                        houseNumber = newAddress.houseNumber,
                        addressAddition = newAddress.addressAddition,
                        postalCode = newAddress.postalCode,
                        city = newAddress.city,
                        country = newAddress.country
                    )
                },
                isValid = userProfileViewModel.validateAddress()
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (errorMessage != null) {
                Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
            }
        }
    }


    ConfirmationDialog(
        showDialog = showEditDialog != null,
        title = "Adresse bearbeiten",
        message = "Willst du diese Adresse wirklich bearbeiten?",
        confirmText = "Bearbeiten",
        onConfirm = {
            val (addressId, isBilling) = showEditDialog!!
            if (isBilling) {
                val address = billingAddresses.find { it.first == addressId }?.second
                if (address != null) userProfileViewModel.openEditBillingAddress(addressId, address)
            } else {
                val address = shippingAddresses.find { it.first == addressId }?.second
                if (address != null) userProfileViewModel.openEditShippingAddress(addressId, address)
            }
        },
        onDismiss = { showEditDialog = null }
    )

    ConfirmationDialog(
        showDialog = showDeleteDialog != null,
        title = "Adresse löschen",
        message = "Willst du diese Adresse wirklich löschen?",
        confirmText = "Löschen",
        onConfirm = {
            val (addressId, isBilling) = showDeleteDialog!!
            val currentUser = user
            val userId = currentUser?.id
            if (userId != null) {
                if (isBilling) {
                    userProfileViewModel.deleteBillingAddress(userId, addressId)
                } else {
                    userProfileViewModel.deleteShippingAddress(userId, addressId)
                }
            }
        },
        onDismiss = { showDeleteDialog = null },
        isDestructive = true
    )

    AddPaymentDialog(
        showDialog = showPaymentDialog,
        onDismiss = { showPaymentDialog = false },
        onSave = { paymentMethod ->
            val currentUser = user
            val userId = currentUser?.id
            if (userId != null) {
                userProfileViewModel.addPayment(userId, paymentMethod)
            }
            showPaymentDialog = false
        }
    )
}