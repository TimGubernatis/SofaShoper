package de.syntax_institut.androidabschlussprojekt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.Address
import de.syntax_institut.androidabschlussprojekt.data.model.*
import de.syntax_institut.androidabschlussprojekt.data.repository.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.User
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.PaymentMethod
import de.syntax_institut.androidabschlussprojekt.data.firebase.repositories.UserRepository
import de.syntax_institut.androidabschlussprojekt.viewmodel.AuthViewModel

class CheckoutViewModel(
    private val cartRepository: CartRepository,
    private val userRepository: UserRepository? = null,
    private val authViewModel: AuthViewModel? = null
) : ViewModel() {
    
    private val _checkoutState = MutableStateFlow<CheckoutState>(CheckoutState.Initial)
    val checkoutState: StateFlow<CheckoutState> = _checkoutState.asStateFlow()
    
    private val _shippingAddress = MutableStateFlow(Address())
    val shippingAddress: StateFlow<Address> = _shippingAddress.asStateFlow()
    
    private val _selectedPaymentMethod = MutableStateFlow<PaymentMethod?>(null)
    val selectedPaymentMethod: StateFlow<PaymentMethod?> = _selectedPaymentMethod.asStateFlow()
    
    private val _billingAddress = MutableStateFlow<Address?>(null)
    val billingAddress: StateFlow<Address?> = _billingAddress.asStateFlow()
    
    val cartItems = cartRepository.cartItems
    val cartTotal = cartRepository.cartTotal
    
    private val _shippingAddresses = MutableStateFlow<List<Pair<String, Address>>>(emptyList())
    val shippingAddresses: StateFlow<List<Pair<String, Address>>> = _shippingAddresses

    private val _billingAddresses = MutableStateFlow<List<Pair<String, Address>>>(emptyList())
    val billingAddresses: StateFlow<List<Pair<String, Address>>> = _billingAddresses

    private val _selectedShippingAddressId = MutableStateFlow<String?>(null)
    val selectedShippingAddressId: StateFlow<String?> = _selectedShippingAddressId

    private val _selectedBillingAddressId = MutableStateFlow<String?>(null)
    val selectedBillingAddressId: StateFlow<String?> = _selectedBillingAddressId
    
    fun updateShippingAddress(address: Address) {
        _shippingAddress.value = address
    }
    
    fun selectPaymentMethod(method: PaymentMethod) {
        _selectedPaymentMethod.value = method
    }
    
    fun updateBillingAddress(address: Address?) {
        _billingAddress.value = address
    }
    
    fun useBillingAddress(use: Boolean) {
        if (!use) _billingAddress.value = null
    }
    
    fun validateCheckout(): Boolean {
        val address = _shippingAddress.value
        val paymentMethod = _selectedPaymentMethod.value
        return address.recipientFirstName.isNotBlank() &&
               address.recipientLastName.isNotBlank() &&
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
                
                val user = authViewModel?.user?.value
                if (user != null && userRepository != null) {

                    val shippingId = userRepository.addShippingAddress(user.id!!, Address(
                        recipientFirstName = _shippingAddress.value.recipientFirstName,
                        recipientLastName = _shippingAddress.value.recipientLastName,
                        street = _shippingAddress.value.street,
                        houseNumber = _shippingAddress.value.houseNumber,
                        addressAddition = _shippingAddress.value.addressAddition,
                        postalCode = _shippingAddress.value.postalCode,
                        city = _shippingAddress.value.city,
                        country = _shippingAddress.value.country,
                        phone = _shippingAddress.value.phone,
                        mobile = _shippingAddress.value.mobile
                    ))

                    val billing = billingAddress.value
                    val billingId = if (billing != null) {
                        userRepository.addBillingAddress(user.id!!, Address(
                            recipientFirstName = billing.recipientFirstName,
                            recipientLastName = billing.recipientLastName,
                            street = billing.street,
                            houseNumber = billing.houseNumber,
                            addressAddition = billing.addressAddition,
                            postalCode = billing.postalCode,
                            city = billing.city,
                            country = billing.country,
                            phone = billing.phone,
                            mobile = billing.mobile
                        ))
                    } else {
                        userRepository.addBillingAddress(user.id!!, Address(
                            recipientFirstName = _shippingAddress.value.recipientFirstName,
                            recipientLastName = _shippingAddress.value.recipientLastName,
                            street = _shippingAddress.value.street,
                            houseNumber = _shippingAddress.value.houseNumber,
                            addressAddition = _shippingAddress.value.addressAddition,
                            postalCode = _shippingAddress.value.postalCode,
                            city = _shippingAddress.value.city,
                            country = _shippingAddress.value.country,
                            phone = _shippingAddress.value.phone,
                            mobile = _shippingAddress.value.mobile
                        ))
                    }

                    _selectedPaymentMethod.value?.let { pm ->
                        userRepository.addPayment(user.id!!, pm)
                    }

                    // Bestellung in Firestore speichern
                    userRepository.addOrder(
                        userId = user.id!!,
                        items = cartRepository.cartItems.value,
                        total = cartRepository.cartTotal.value,
                        shippingAddressId = shippingId,
                        billingAddressId = billingId,
                        status = order.status.name,
                        timestamp = order.createdAt
                    )
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
        _shippingAddress.value = Address()
        _selectedPaymentMethod.value = null
    }
    
    fun loadAddresses(userId: String) {
        viewModelScope.launch {
            _shippingAddresses.value = userRepository?.getShippingAddresses(userId) ?: emptyList()
            _billingAddresses.value = userRepository?.getBillingAddresses(userId) ?: emptyList()
        }
    }

    fun selectShippingAddress(addressId: String) {
        _selectedShippingAddressId.value = addressId
        val address = _shippingAddresses.value.find { it.first == addressId }?.second
        address?.let { _shippingAddress.value = it.toShippingAddress(authViewModel?.user?.value) }
    }

    fun selectBillingAddress(addressId: String) {
        _selectedBillingAddressId.value = addressId
        val address = _billingAddresses.value.find { it.first == addressId }?.second
        address?.let { _billingAddress.value = it.toShippingAddress(authViewModel?.user?.value) }
    }

    fun saveAndSelectShippingAddress(userId: String, address: Address) {
        viewModelScope.launch {
            val newId = userRepository?.addShippingAddress(userId, Address(
                street = address.street,
                houseNumber = address.houseNumber ?: "",
                addressAddition = address.addressAddition,
                postalCode = address.postalCode,
                city = address.city,
                country = address.country
            ))
            if (newId != null) {
                loadAddresses(userId)
                selectShippingAddress(newId)
            }
        }
    }

    fun saveAndSelectBillingAddress(userId: String, address: Address) {
        viewModelScope.launch {
            val newId = userRepository?.addBillingAddress(userId, Address(
                street = address.street,
                houseNumber = address.houseNumber ?: "",
                addressAddition = address.addressAddition,
                postalCode = address.postalCode,
                city = address.city,
                country = address.country
            ))
            if (newId != null) {
                loadAddresses(userId)
                selectBillingAddress(newId)
            }
        }
    }


    private fun Address.toShippingAddress(user: User?): Address {
        return Address(
            recipientFirstName = user?.firstName ?: "",
            recipientLastName = user?.lastName ?: "",
            street = this.street,
            city = this.city,
            postalCode = this.postalCode,
            country = this.country,
            phone = user?.phone,
            houseNumber = this.houseNumber,
            addressAddition = this.addressAddition
        )
    }
}

sealed class CheckoutState {
    object Initial : CheckoutState()
    object Loading : CheckoutState()
    data class Success(val order: Order) : CheckoutState()
    data class Error(val message: String) : CheckoutState()
}

