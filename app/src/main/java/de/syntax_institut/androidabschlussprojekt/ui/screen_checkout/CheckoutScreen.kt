package de.syntax_institut.androidabschlussprojekt.ui.screen_checkout

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.syntax_institut.androidabschlussprojekt.ui.components.PrimaryButton
import de.syntax_institut.androidabschlussprojekt.viewmodel.CheckoutState
import de.syntax_institut.androidabschlussprojekt.viewmodel.CheckoutViewModel
import de.syntax_institut.androidabschlussprojekt.viewmodel.AuthViewModel
import org.koin.androidx.compose.koinViewModel
import de.syntax_institut.androidabschlussprojekt.R
import androidx.compose.ui.res.stringResource
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import de.syntax_institut.androidabschlussprojekt.ui.screen_checkout.components.CheckoutContent
import de.syntax_institut.androidabschlussprojekt.ui.screen_checkout.components.OrderSuccessScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    checkoutViewModel: CheckoutViewModel = koinViewModel(),
    onBackClick: () -> Unit = {},
    onOrderSuccess: () -> Unit = {},
    authViewModel: AuthViewModel = koinViewModel()
) {
    val checkoutState by checkoutViewModel.checkoutState.collectAsState()
    val cartItems by checkoutViewModel.cartItems.collectAsState()
    val cartTotal by checkoutViewModel.cartTotal.collectAsState()
    val shippingAddress by checkoutViewModel.shippingAddress.collectAsState()
    val selectedPaymentMethod by checkoutViewModel.selectedPaymentMethod.collectAsState()
    val billingAddress by checkoutViewModel.billingAddress.collectAsState()
    var email by remember { mutableStateOf("") }
    var createAccount by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var passwordRepeat by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var useBillingAddress by remember { mutableStateOf(false) }
    val user by authViewModel.user.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var showPasswordDialog by remember { mutableStateOf(false) }
    val shippingAddresses by checkoutViewModel.shippingAddresses.collectAsState()
    val billingAddresses by checkoutViewModel.billingAddresses.collectAsState()
    val selectedShippingAddressId by checkoutViewModel.selectedShippingAddressId.collectAsState()
    val selectedBillingAddressId by checkoutViewModel.selectedBillingAddressId.collectAsState()
    var adoptShippingChanges by remember { mutableStateOf(false) }
    var adoptBillingChanges by remember { mutableStateOf(false) }

    val googleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    authViewModel.signInWithGoogle(account.idToken ?: "")
                }
            } catch (_: ApiException) {}
        }
    }

    LaunchedEffect(checkoutState) {
        if (checkoutState is CheckoutState.Success) {
            onOrderSuccess()
        }
    }

    LaunchedEffect(user) {
        user?.id?.let { checkoutViewModel.loadAddresses(it) }
        email = user?.email ?: ""
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.checkout_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.checkout_back))
                    }
                }
            )
        }
    ) { padding ->
        if (showPasswordDialog) {
            AlertDialog(
                onDismissRequest = { showPasswordDialog = false },
                title = { Text("Fehler") },
                text = { Text(stringResource(R.string.checkout_passwords_not_matching)) },
                confirmButton = {
                    Button(onClick = { showPasswordDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }
        when (val state = checkoutState) {
            is CheckoutState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator()
                        Text(stringResource(R.string.checkout_processing))
                    }
                }
            }
            is CheckoutState.Success -> {
                OrderSuccessScreen(
                    order = state.order,
                    onBackToHome = onOrderSuccess
                )
            }
            is CheckoutState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "❌",
                            style = MaterialTheme.typography.headlineLarge
                        )
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        PrimaryButton(
                            text = stringResource(R.string.checkout_retry),
                            onClick = { checkoutViewModel.resetCheckout() }
                        )
                    }
                }
            }
            else -> {
                CheckoutContent(
                    cartItems = cartItems,
                    cartTotal = cartTotal,
                    shippingAddress = shippingAddress,
                    selectedPaymentMethod = selectedPaymentMethod,
                    onAddressChange = { checkoutViewModel.updateShippingAddress(it) },
                    onPaymentMethodSelect = { checkoutViewModel.selectPaymentMethod(it) },
                    onPlaceOrder = {
                        if (createAccount) {
                            if (password != passwordRepeat) {
                                showPasswordDialog = true
                                return@CheckoutContent
                            }
                            if (email.isNotBlank() && password.isNotBlank()) {
                                authViewModel.register(
                                    email,
                                    password,
                                    shippingAddress.firstName,
                                    shippingAddress.lastName
                                ) { newUser ->
                                    if (adoptShippingChanges) {
                                        checkoutViewModel.saveAndSelectShippingAddress(newUser.id!!, shippingAddress)
                                    }
                                    if (adoptBillingChanges && useBillingAddress) {
                                        billingAddress?.let { safeBillingAddress ->
                                            checkoutViewModel.saveAndSelectBillingAddress(newUser.id!!, safeBillingAddress)
                                        }
                                    }
                                    checkoutViewModel.placeOrder()
                                }
                                return@CheckoutContent
                            }
                        }
                        user?.let { user ->
                            if (adoptShippingChanges) {
                                checkoutViewModel.saveAndSelectShippingAddress(user.id!!, shippingAddress)
                            }
                            if (adoptBillingChanges && useBillingAddress) {
                                billingAddress?.let { safeBillingAddress ->
                                    checkoutViewModel.saveAndSelectBillingAddress(user.id!!, safeBillingAddress)
                                }
                            }
                        }
                        checkoutViewModel.placeOrder()
                    },
                    modifier = Modifier.padding(padding),
                    email = email,
                    onEmailChange = { email = it },
                    createAccount = createAccount,
                    onCreateAccountChange = { createAccount = it },
                    password = password,
                    onPasswordChange = { password = it },
                    passwordRepeat = passwordRepeat,
                    onPasswordRepeatChange = { passwordRepeat = it },
                    passwordVisible = passwordVisible,
                    onPasswordVisibleChange = { passwordVisible = it },
                    user = user,
                    onGoogleSignIn = { launcher.launch(googleSignInClient.signInIntent) },
                    onForgotPassword = { if (email.isNotBlank()) authViewModel.sendPasswordResetEmail(email) },
                    useBillingAddress = useBillingAddress,
                    onUseBillingAddressChange = {
                        useBillingAddress = it
                        checkoutViewModel.useBillingAddress(it)
                    },
                    billingAddress = billingAddress,
                    onBillingAddressChange = { checkoutViewModel.updateBillingAddress(it) },
                    shippingAddresses = shippingAddresses,
                    billingAddresses = billingAddresses,
                    selectedShippingAddressId = selectedShippingAddressId,
                    selectedBillingAddressId = selectedBillingAddressId,
                    onSelectShippingAddress = { checkoutViewModel.selectShippingAddress(it) },
                    onSelectBillingAddress = { checkoutViewModel.selectBillingAddress(it) },
                    adoptShippingChanges = adoptShippingChanges,
                    onAdoptShippingChangesChange = { adoptShippingChanges = it },
                    adoptBillingChanges = adoptBillingChanges,
                    onAdoptBillingChangesChange = { adoptBillingChanges = it }
                )
            }
        }
    }
}



