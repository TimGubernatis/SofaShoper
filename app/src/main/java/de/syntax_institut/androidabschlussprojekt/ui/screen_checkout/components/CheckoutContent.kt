package de.syntax_institut.androidabschlussprojekt.ui.screen_checkout.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import de.syntax_institut.androidabschlussprojekt.data.model.CartItem
import de.syntax_institut.androidabschlussprojekt.data.model.PaymentMethod
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.Address
import de.syntax_institut.androidabschlussprojekt.ui.components.PrimaryButton
import de.syntax_institut.androidabschlussprojekt.util.formatPrice
import de.syntax_institut.androidabschlussprojekt.util.responsivePadding
import de.syntax_institut.androidabschlussprojekt.util.responsiveSpacing
import de.syntax_institut.androidabschlussprojekt.util.responsiveCardPadding
import de.syntax_institut.androidabschlussprojekt.util.responsiveButtonHeight
import de.syntax_institut.androidabschlussprojekt.util.responsiveMaxWidth
import de.syntax_institut.androidabschlussprojekt.util.isTablet
import androidx.compose.ui.res.stringResource
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.ui.screen_login.components.GoogleSignInButton
import androidx.compose.material3.HorizontalDivider
import de.syntax_institut.androidabschlussprojekt.util.responsiveTextFieldSpacing
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp

@Composable
fun CheckoutContent(
    cartItems: List<CartItem>,
    cartTotal: Double,
    shippingAddress: Address,
    selectedPaymentMethod: PaymentMethod?,
    onAddressChange: (Address) -> Unit,
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
    onForgotPassword: () -> Unit,
    useBillingAddress: Boolean,
    onUseBillingAddressChange: (Boolean) -> Unit,
    billingAddress: Address?,
    onBillingAddressChange: (Address) -> Unit,
    shippingAddresses: List<Pair<String, Address>> = emptyList(),
    billingAddresses: List<Pair<String, Address>> = emptyList(),
    selectedShippingAddressId: String? = null,
    selectedBillingAddressId: String? = null,
    onSelectShippingAddress: (String) -> Unit = {},
    onSelectBillingAddress: (String) -> Unit = {},
    adoptShippingChanges: Boolean = false,
    onAdoptShippingChangesChange: (Boolean) -> Unit = {},
    adoptBillingChanges: Boolean = false,
    onAdoptBillingChangesChange: (Boolean) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(responsivePadding())
            .widthIn(max = responsiveMaxWidth()),
        verticalArrangement = Arrangement.spacedBy(responsiveSpacing()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Card {
            Column(
                modifier = Modifier.padding(responsiveCardPadding())
            ) {
                Text(
                    text = "Bestellübersicht",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(responsiveTextFieldSpacing()))
                
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
                
                HorizontalDivider(modifier = Modifier.padding(vertical = responsiveTextFieldSpacing()))
                
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
        

        if (user != null && shippingAddresses.isNotEmpty()) {
            var expanded by remember { mutableStateOf(false) }
            Box {
                OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                    val selected = shippingAddresses.find { it.first == selectedShippingAddressId }?.second
                    Text(selected?.let { "${it.street} ${it.houseNumber}, ${it.postalCode} ${it.city}" } ?: "Lieferadresse wählen")
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    shippingAddresses.forEach { (id, address) ->
                        DropdownMenuItem(
                            text = { Text("${address.street} ${address.houseNumber}, ${address.postalCode} ${address.city}") },
                            onClick = {
                                onSelectShippingAddress(id)
                                expanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(responsiveTextFieldSpacing()))
        }
        AddressSection(
            address = shippingAddress,
            onAddressChange = onAddressChange,
            title = "Lieferadresse"
        )
        if (user != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = adoptShippingChanges,
                    onCheckedChange = onAdoptShippingChangesChange
                )
                Text("Geänderte Lieferadresse übernehmen und speichern")
            }
        }
        if (useBillingAddress) {
            if (user != null && billingAddresses.isNotEmpty()) {
                var expanded by remember { mutableStateOf(false) }
                Box {
                    OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                        val selected = billingAddresses.find { it.first == selectedBillingAddressId }?.second
                        Text(selected?.let { "${it.street} ${it.houseNumber}, ${it.postalCode} ${it.city}" } ?: "Rechnungsadresse wählen")
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        billingAddresses.forEach { (id, address) ->
                            DropdownMenuItem(
                                text = { Text("${address.street} ${address.houseNumber}, ${address.postalCode} ${address.city}") },
                                onClick = {
                                    onSelectBillingAddress(id)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(responsiveTextFieldSpacing()))
            }
            AddressSection(
                address = billingAddress ?: Address("", "", "", "", "", "", "", "", "", ""),
                onAddressChange = onBillingAddressChange,
                title = "Rechnungsadresse"
            )
            if (user != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = adoptBillingChanges,
                        onCheckedChange = onAdoptBillingChangesChange
                    )
                    Text("Geänderte Rechnungsadresse übernehmen und speichern")
                }
            }
        }
        

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

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = useBillingAddress,
                onCheckedChange = onUseBillingAddressChange
            )
            Text("Abweichende Rechnungsadresse")
        }

        PrimaryButton(
            text = "Bestellung aufgeben",
            onClick = onPlaceOrder,
            modifier = Modifier
                .fillMaxWidth()
                .height(responsiveButtonHeight()),
            enabled = selectedPaymentMethod != null && 
                     shippingAddress.recipientFirstName.isNotBlank() &&
                     shippingAddress.recipientLastName.isNotBlank() &&
                     shippingAddress.street.isNotBlank() &&
                     shippingAddress.city.isNotBlank() &&
                     shippingAddress.postalCode.isNotBlank() &&
                     email.isNotBlank() &&
                     (!createAccount || password.isNotBlank())
        )
    }
}