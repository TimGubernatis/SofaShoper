package de.syntax_institut.androidabschlussprojekt.ui.screen_checkout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.syntax_institut.androidabschlussprojekt.data.model.*
import de.syntax_institut.androidabschlussprojekt.ui.components.PrimaryButton
import de.syntax_institut.androidabschlussprojekt.util.formatPrice
import de.syntax_institut.androidabschlussprojekt.viewmodel.CheckoutState
import de.syntax_institut.androidabschlussprojekt.viewmodel.CheckoutViewModel
import de.syntax_institut.androidabschlussprojekt.viewmodel.AuthViewModel
import org.koin.androidx.compose.koinViewModel
import de.syntax_institut.androidabschlussprojekt.R
import androidx.compose.ui.res.stringResource
import de.syntax_institut.androidabschlussprojekt.ui.screen_login.components.GoogleSignInButton
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button


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
    var email by remember { mutableStateOf("") }
    var createAccount by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var passwordRepeat by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val user by authViewModel.user.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var showPasswordDialog by remember { mutableStateOf(false) }

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.checkout_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.checkout_back))
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
                                authViewModel.register(email, password, shippingAddress.firstName, shippingAddress.lastName)
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
                    onForgotPassword = { if (email.isNotBlank()) authViewModel.sendPasswordResetEmail(email) }
                )
            }
        }
    }
}

@Composable
private fun CheckoutContent(
    cartItems: List<CartItem>,
    cartTotal: Double,
    shippingAddress: ShippingAddress,
    selectedPaymentMethod: PaymentMethod?,
    onAddressChange: (ShippingAddress) -> Unit,
    onPaymentMethodSelect: (PaymentMethod) -> Unit,
    onPlaceOrder: () -> Unit,
    modifier: Modifier = Modifier,
    email: String,
    onEmailChange: (String) -> Unit,
    createAccount: Boolean,
    onCreateAccountChange: (Boolean) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordRepeat: String,
    onPasswordRepeatChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibleChange: (Boolean) -> Unit,
    user: de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.User?,
    onGoogleSignIn: () -> Unit,
    onForgotPassword: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Card {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Bestellübersicht",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                cartItems.forEach { cartItem ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${cartItem.quantity}x ${cartItem.product.title}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = formatPrice(cartItem.totalPrice),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Gesamt",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formatPrice(cartTotal),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        

        AddressSection(
            address = shippingAddress,
            onAddressChange = onAddressChange
        )
        

        PaymentMethodSection(
            selectedMethod = selectedPaymentMethod,
            onMethodSelect = onPaymentMethodSelect
        )
        

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text(stringResource(R.string.checkout_email_label)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = user == null
        )


        if (user == null) {
            GoogleSignInButton(onClick = onGoogleSignIn)
        }


        if (user == null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = createAccount,
                    onCheckedChange = onCreateAccountChange
                )
                Text(stringResource(R.string.checkout_create_account))
            }
            if (createAccount) {
                OutlinedTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = { Text(stringResource(R.string.checkout_password_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { onPasswordVisibleChange(!passwordVisible) }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (passwordVisible) "Verbergen" else "Anzeigen"
                            )
                        }
                    }
                )
                OutlinedTextField(
                    value = passwordRepeat,
                    onValueChange = onPasswordRepeatChange,
                    label = { Text("Passwort wiederholen") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
                )
                TextButton(onClick = onForgotPassword, enabled = email.isNotBlank()) {
                    Text("Passwort vergessen?")
                }
            }
        }


        PrimaryButton(
            text = "Bestellung aufgeben",
            onClick = onPlaceOrder,
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedPaymentMethod != null && 
                     shippingAddress.firstName.isNotBlank() &&
                     shippingAddress.lastName.isNotBlank() &&
                     shippingAddress.street.isNotBlank() &&
                     shippingAddress.city.isNotBlank() &&
                     shippingAddress.postalCode.isNotBlank() &&
                     email.isNotBlank() &&
                     (!createAccount || password.isNotBlank())
        )
    }
}

@Composable
private fun AddressSection(
    address: ShippingAddress,
    onAddressChange: (ShippingAddress) -> Unit
) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Lieferadresse",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = address.firstName,
                    onValueChange = { onAddressChange(address.copy(firstName = it)) },
                    label = { Text("Vorname") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = address.lastName,
                    onValueChange = { onAddressChange(address.copy(lastName = it)) },
                    label = { Text("Nachname") },
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = address.street,
                onValueChange = { onAddressChange(address.copy(street = it)) },
                label = { Text("Straße & Hausnummer") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = address.postalCode,
                    onValueChange = { onAddressChange(address.copy(postalCode = it)) },
                    label = { Text("PLZ") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = address.city,
                    onValueChange = { onAddressChange(address.copy(city = it)) },
                    label = { Text("Stadt") },
                    modifier = Modifier.weight(2f)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = address.phone ?: "",
                onValueChange = { onAddressChange(address.copy(phone = it)) },
                label = { Text("Telefon (optional)") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun PaymentMethodSection(
    selectedMethod: PaymentMethod?,
    onMethodSelect: (PaymentMethod) -> Unit
) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Zahlungsmethode",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            PaymentMethod.values().forEach { method ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedMethod == method,
                        onClick = { onMethodSelect(method) }
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Icon(
                        imageVector = Icons.Default.Payment,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = when (method) {
                            PaymentMethod.CREDIT_CARD -> "Kreditkarte"
                            PaymentMethod.PAYPAL -> "PayPal"
                            PaymentMethod.BANK_TRANSFER -> "Banküberweisung"
                            PaymentMethod.CASH_ON_DELIVERY -> "Nachnahme"
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderSuccessScreen(
    order: Order,
    onBackToHome: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Erfolg",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = "Bestellung erfolgreich!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "Bestellnummer: ${order.id}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = "Vielen Dank für Ihre Bestellung. Sie erhalten eine Bestätigung per E-Mail.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            PrimaryButton(
                text = "Zurück zur Startseite",
                onClick = onBackToHome,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
} 