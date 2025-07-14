package de.syntax_institut.androidabschlussprojekt.ui.screen_profile.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import de.syntax_institut.androidabschlussprojekt.viewmodel.UserProfileViewModel
import org.koin.androidx.compose.koinViewModel
import de.syntax_institut.androidabschlussprojekt.ui.screen_profile.profile.components.*
import de.syntax_institut.androidabschlussprojekt.ui.components.PrimaryButton

@Composable
fun ProfileScreenContent(
    navController: NavController,
    userProfileViewModel: UserProfileViewModel = koinViewModel()
) {
    val user by userProfileViewModel.user.collectAsState()
    val currentUser = user
    var isEditing by remember { mutableStateOf(false) }
    val isSaving by userProfileViewModel.isSaving.collectAsState()
    val addressDialogType by userProfileViewModel.addressDialogType.collectAsState()
    val addressForm by userProfileViewModel.addressForm.collectAsState()
    val editAddressId by userProfileViewModel.editAddressId.collectAsState()
    val paymentMethods by userProfileViewModel.paymentMethods.collectAsState()
    val defaultPaymentMethodId by userProfileViewModel.defaultPaymentMethodId.collectAsState()
    val defaultShippingAddressId by userProfileViewModel.defaultShippingAddressId.collectAsState()
    val defaultBillingAddressId by userProfileViewModel.defaultBillingAddressId.collectAsState()
    val shippingAddresses by userProfileViewModel.shippingAddresses.collectAsState()
    val billingAddresses by userProfileViewModel.billingAddresses.collectAsState()
    var showDeleteDialog by remember { mutableStateOf<Pair<String, Boolean>?>(null) }
    var showPaymentDialog by remember { mutableStateOf(false) }
    var firstName by remember { mutableStateOf(currentUser?.firstName ?: "") }
    var lastName by remember { mutableStateOf(currentUser?.lastName ?: "") }
    var email by remember { mutableStateOf(currentUser?.email ?: "") }
    var phone by remember { mutableStateOf(currentUser?.phone ?: "") }
    var mobile by remember { mutableStateOf(currentUser?.mobile ?: "") }
    val scrollState = rememberScrollState()
    val isLoading by userProfileViewModel.isLoading.collectAsState()
    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
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
                val updatedUser = currentUser?.copy(
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    phone = phone.takeIf { it.isNotBlank() },
                    mobile = mobile.takeIf { it.isNotBlank() }
                )
                if (updatedUser != null) {
                    userProfileViewModel.updateUser(updatedUser)
                }
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
            onSetDefault = { userProfileViewModel.setDefaultShippingAddress(currentUser?.id ?: "", it) },
            onAdd = { userProfileViewModel.openAddShippingAddress() },
            onEdit = { id, address -> userProfileViewModel.openEditShippingAddress(id, address) },
            onDelete = { userProfileViewModel.deleteShippingAddress(currentUser?.id ?: "", it) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        ProfileAddressSection(
            title = "Standard-Rechnungsadresse",
            addresses = billingAddresses,
            defaultAddressId = defaultBillingAddressId,
            onSetDefault = { userProfileViewModel.setDefaultBillingAddress(currentUser?.id ?: "", it) },
            onAdd = { userProfileViewModel.openAddBillingAddress() },
            onEdit = { id, address -> userProfileViewModel.openEditBillingAddress(id, address) },
            onDelete = { userProfileViewModel.deleteBillingAddress(currentUser?.id ?: "", it) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        ProfilePaymentSection(
            paymentMethods = paymentMethods,
            defaultPaymentMethodId = defaultPaymentMethodId,
            onSetDefault = { userProfileViewModel.setDefaultPaymentMethod(it) },
            onAdd = { showPaymentDialog = true },
            onDelete = { userProfileViewModel.deletePayment(currentUser?.id ?: "", it) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        AddressDialog(
            dialogType = addressDialogType,
            addressForm = addressForm,
            onDismiss = { userProfileViewModel.closeAddressDialog() },
            onSave = { userProfileViewModel.saveAddress(currentUser?.id ?: "") },
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
                    val userId = currentUser?.id ?: ""
                    if (userId.isNotBlank()) {
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
                val userId = currentUser?.id ?: ""
                if (userId.isNotBlank()) {
                    userProfileViewModel.addPayment(userId, paymentMethod)
                }
                showPaymentDialog = false
            }
        )
        Spacer(modifier = Modifier.height(32.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            PrimaryButton(
                text = "Logout",
                onClick = { userProfileViewModel.signOut() },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            var showDeleteDialog by remember { mutableStateOf(false) }
            PrimaryButton(
                text = "Account löschen",
                onClick = { showDeleteDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.weight(1f)
            )
            if (showDeleteDialog) {
                var input by remember { mutableStateOf("") }
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Account wirklich löschen?") },
                    text = {
                        Column {
                            Text("Gib 'del' ein, um das Löschen zu bestätigen.")
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = input,
                                onValueChange = { input = it },
                                label = { Text("Bestätigung") }
                            )
                        }
                    },
                    confirmButton = {
                        PrimaryButton(
                            text = "Löschen",
                            onClick = {
                                if (input == "del") {
                                    userProfileViewModel.deleteUserAccount()
                                    showDeleteDialog = false
                                }
                            },
                            enabled = input == "del",
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        )
                    },
                    dismissButton = {
                        PrimaryButton(
                            text = "Abbrechen",
                            onClick = { showDeleteDialog = false }
                        )
                    }
                )
            }
        }
    }
} 