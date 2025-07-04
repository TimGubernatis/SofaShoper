package de.syntax_institut.androidabschlussprojekt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.syntax_institut.androidabschlussprojekt.data.model.*
import de.syntax_institut.androidabschlussprojekt.data.repository.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.User
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.Address
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.PaymentMethod as FirebasePaymentMethod
import de.syntax_institut.androidabschlussprojekt.data.firebase.repositories.UserRepository
import de.syntax_institut.androidabschlussprojekt.viewmodel.AuthViewModel

class CheckoutViewModel(
    private val cartRepository: CartRepository,
    private val userRepository: UserRepository? = null,
    private val authViewModel: AuthViewModel? = null
) : ViewModel() {
    
    private val _checkoutState = MutableStateFlow<CheckoutState>(CheckoutState.Initial)
    val checkoutState: StateFlow<CheckoutState> = _checkoutState.asStateFlow()
    
    private val _shippingAddress = MutableStateFlow(ShippingAddress("", "", "", "", ""))
    val shippingAddress: StateFlow<ShippingAddress> = _shippingAddress.asStateFlow()
    
    private val _selectedPaymentMethod = MutableStateFlow<PaymentMethod?>(null)
    val selectedPaymentMethod: StateFlow<PaymentMethod?> = _selectedPaymentMethod.asStateFlow()
    
    private val _billingAddress = MutableStateFlow<ShippingAddress?>(null)
    val billingAddress: StateFlow<ShippingAddress?> = _billingAddress.asStateFlow()
    
    val cartItems = cartRepository.cartItems
    val cartTotal = cartRepository.cartTotal
    
    fun updateShippingAddress(address: ShippingAddress) {
        _shippingAddress.value = address
    }
    
    fun selectPaymentMethod(method: PaymentMethod) {
        _selectedPaymentMethod.value = method
    }
    
    fun updateBillingAddress(address: ShippingAddress?) {
        _billingAddress.value = address
    }
    
    fun useBillingAddress(use: Boolean) {
        if (!use) _billingAddress.value = null
    }
    
    fun validateCheckout(): Boolean {
        val address = _shippingAddress.value
        val paymentMethod = _selectedPaymentMethod.value
        
        return address.firstName.isNotBlank() &&
                address.lastName.isNotBlank() &&
                address.street.isNotBlank() &&
                address.city.isNotBlank() &&
                address.postalCode.isNotBlank() &&
                paymentMethod != null
    }
    
    fun placeOrder() {
        viewModelScope.launch {
            _checkoutState.value = CheckoutState.Loading
            
            try {
                kotlinx.coroutines.delay(2000)
                
                val order = Order(
                    items = cartRepository.cartItems.value,
                    total = cartRepository.cartTotal.value,
                    shippingAddress = _shippingAddress.value,
                    paymentMethod = _selectedPaymentMethod.value
                )
                
                // User-Daten in Firebase speichern, falls eingeloggt
                val user = authViewModel?.user?.value
                if (user != null && userRepository != null) {
                    // Versandadresse speichern
                    userRepository.addAddress(user.id!!, Address(
                        street = _shippingAddress.value.street,
                        houseNumber = _shippingAddress.value.houseNumber ?: "",
                        addressAddition = _shippingAddress.value.addressAddition,
                        postalCode = _shippingAddress.value.postalCode,
                        city = _shippingAddress.value.city,
                        country = _shippingAddress.value.country
                    ))
                    // Rechnungsadresse speichern, falls vorhanden
                    billingAddress.value?.let { billing ->
                        userRepository.addAddress(user.id!!, Address(
                            street = billing.street,
                            houseNumber = billing.houseNumber ?: "",
                            addressAddition = billing.addressAddition,
                            postalCode = billing.postalCode,
                            city = billing.city,
                            country = billing.country
                        ))
                    }
                    // Zahlungsart speichern
                    _selectedPaymentMethod.value?.let { pm ->
                        val payment = when (pm) {
                            PaymentMethod.PAYPAL -> FirebasePaymentMethod.paypal("")
                            PaymentMethod.BANK_TRANSFER -> FirebasePaymentMethod.iban("")
                            PaymentMethod.CREDIT_CARD -> FirebasePaymentMethod(type = "CREDIT_CARD")
                            PaymentMethod.CASH_ON_DELIVERY -> FirebasePaymentMethod(type = "CASH_ON_DELIVERY")
                        }
                        userRepository.addPayment(user.id!!, payment)
                    }
                }
                
                cartRepository.clearCart()
                
                _checkoutState.value = CheckoutState.Success(order)
            } catch (e: Exception) {
                _checkoutState.value = CheckoutState.Error("Fehler beim Bestellen: ${e.message}")
            }
        }
    }
    
    fun resetCheckout() {
        _checkoutState.value = CheckoutState.Initial
        _shippingAddress.value = ShippingAddress("", "", "", "", "")
        _selectedPaymentMethod.value = null
    }
    
    fun prefillFromUser(user: User) {
        _shippingAddress.value = ShippingAddress(
            firstName = user.firstName,
            lastName = user.lastName,
            street = user.shippingAddress.street,
            city = user.shippingAddress.city,
            postalCode = user.shippingAddress.postalCode,
            country = user.shippingAddress.country,
            phone = user.phone
        )
        _selectedPaymentMethod.value = when (user.paymentMethod?.type) {
            "PayPal" -> PaymentMethod.PAYPAL
            "IBAN" -> PaymentMethod.BANK_TRANSFER
            "CREDIT_CARD" -> PaymentMethod.CREDIT_CARD
            "CASH_ON_DELIVERY" -> PaymentMethod.CASH_ON_DELIVERY
            else -> null
        }
    }
}

sealed class CheckoutState {
    object Initial : CheckoutState()
    object Loading : CheckoutState()
    data class Success(val order: Order) : CheckoutState()
    data class Error(val message: String) : CheckoutState()
}

