package de.syntax_institut.androidabschlussprojekt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.User
import de.syntax_institut.androidabschlussprojekt.data.firebase.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.Address

class UserProfileViewModel(
    val authViewModel: AuthViewModel,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _accountDeleted = MutableStateFlow(false)
    val accountDeleted: StateFlow<Boolean> = _accountDeleted

    private val _shippingAddresses = MutableStateFlow<List<Pair<String, de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.Address>>>(emptyList())
    val shippingAddresses: StateFlow<List<Pair<String, de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.Address>>> = _shippingAddresses

    private val _billingAddresses = MutableStateFlow<List<Pair<String, de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.Address>>>(emptyList())
    val billingAddresses: StateFlow<List<Pair<String, de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.Address>>> = _billingAddresses

    private val _defaultShippingAddressId = MutableStateFlow<String?>(null)
    val defaultShippingAddressId: StateFlow<String?> = _defaultShippingAddressId

    private val _defaultBillingAddressId = MutableStateFlow<String?>(null)
    val defaultBillingAddressId: StateFlow<String?> = _defaultBillingAddressId

    // --- Dialog- und Formular-Logik für Adressen (MVVM) ---
    enum class AddressDialogType { NONE, ADD_SHIPPING, EDIT_SHIPPING, ADD_BILLING, EDIT_BILLING }
    private val _addressDialogType = MutableStateFlow(AddressDialogType.NONE)
    val addressDialogType: StateFlow<AddressDialogType> = _addressDialogType

    private val _addressForm = MutableStateFlow(Address())
    val addressForm: StateFlow<Address> = _addressForm

    private val _editAddressId = MutableStateFlow<String?>(null)
    val editAddressId: StateFlow<String?> = _editAddressId

    init {
        viewModelScope.launch {
            authViewModel.user.collect { currentUser ->
                if (currentUser != null) {
                    val loadedUser = userRepository.getUser(currentUser.id ?: "")
                    _user.value = loadedUser
                } else {
                    _user.value = null
                }
            }
        }
    }

    fun updateUser(updatedUser: User) {
        viewModelScope.launch {
            _isSaving.value = true
            _errorMessage.value = null
            try {
                userRepository.saveUser(updatedUser)
                _user.value = updatedUser
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Fehler beim Speichern"
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun signOut() {
        println("UserProfileViewModel: signOut aufgerufen")
        authViewModel.signOut()
    }

    fun deleteUserAccount() {
        viewModelScope.launch {
            val userId = user.value?.id ?: return@launch
            try {
                userRepository.deleteUserCompletely(userId)
                val firebaseUser = FirebaseAuth.getInstance().currentUser
                firebaseUser?.delete()?.addOnCompleteListener { task ->
                    _accountDeleted.value = task.isSuccessful
                }
            } catch (e: Exception) {
                _accountDeleted.value = false
            }
        }
    }

    fun loadAddresses(userId: String) {
        viewModelScope.launch {
            _shippingAddresses.value = userRepository.getShippingAddresses(userId)
            _billingAddresses.value = userRepository.getBillingAddresses(userId)
            val user = userRepository.getUser(userId)
            _defaultShippingAddressId.value = user?.defaultShippingAddressId
            _defaultBillingAddressId.value = user?.defaultBillingAddressId
        }
    }

    fun addShippingAddress(userId: String, address: de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.Address) {
        viewModelScope.launch {
            val id = userRepository.addShippingAddress(userId, address)
            loadAddresses(userId)
            setDefaultShippingAddress(userId, id)
        }
    }

    fun updateShippingAddress(userId: String, addressId: String, address: de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.Address) {
        viewModelScope.launch {
            userRepository.updateShippingAddress(userId, addressId, address)
            loadAddresses(userId)
        }
    }

    fun deleteShippingAddress(userId: String, addressId: String) {
        viewModelScope.launch {
            userRepository.deleteShippingAddress(userId, addressId)
            loadAddresses(userId)
        }
    }

    fun setDefaultShippingAddress(userId: String, addressId: String) {
        viewModelScope.launch {
            userRepository.setDefaultShippingAddress(userId, addressId)
            _defaultShippingAddressId.value = addressId
        }
    }

    fun addBillingAddress(userId: String, address: de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.Address) {
        viewModelScope.launch {
            val id = userRepository.addBillingAddress(userId, address)
            loadAddresses(userId)
            setDefaultBillingAddress(userId, id)
        }
    }

    fun updateBillingAddress(userId: String, addressId: String, address: de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.Address) {
        viewModelScope.launch {
            userRepository.updateBillingAddress(userId, addressId, address)
            loadAddresses(userId)
        }
    }

    fun deleteBillingAddress(userId: String, addressId: String) {
        viewModelScope.launch {
            userRepository.deleteBillingAddress(userId, addressId)
            loadAddresses(userId)
        }
    }

    fun setDefaultBillingAddress(userId: String, addressId: String) {
        viewModelScope.launch {
            userRepository.setDefaultBillingAddress(userId, addressId)
            _defaultBillingAddressId.value = addressId
        }
    }

    fun openAddShippingAddress() {
        _addressDialogType.value = AddressDialogType.ADD_SHIPPING
        _addressForm.value = Address()
        _editAddressId.value = null
    }
    fun openEditShippingAddress(id: String, address: Address) {
        _addressDialogType.value = AddressDialogType.EDIT_SHIPPING
        _addressForm.value = address
        _editAddressId.value = id
    }
    fun openAddBillingAddress() {
        _addressDialogType.value = AddressDialogType.ADD_BILLING
        _addressForm.value = Address()
        _editAddressId.value = null
    }
    fun openEditBillingAddress(id: String, address: Address) {
        _addressDialogType.value = AddressDialogType.EDIT_BILLING
        _addressForm.value = address
        _editAddressId.value = id
    }
    fun closeAddressDialog() {
        _addressDialogType.value = AddressDialogType.NONE
        _editAddressId.value = null
    }
    fun setAddressFormField(
        street: String? = null,
        houseNumber: String? = null,
        addressAddition: String? = null,
        postalCode: String? = null,
        city: String? = null,
        country: String? = null
    ) {
        _addressForm.value = _addressForm.value.copy(
            street = street ?: _addressForm.value.street,
            houseNumber = houseNumber ?: _addressForm.value.houseNumber,
            addressAddition = addressAddition ?: _addressForm.value.addressAddition,
            postalCode = postalCode ?: _addressForm.value.postalCode,
            city = city ?: _addressForm.value.city,
            country = country ?: _addressForm.value.country
        )
    }
    fun saveAddress(userId: String) {
        when (_addressDialogType.value) {
            AddressDialogType.ADD_SHIPPING -> addShippingAddress(userId, _addressForm.value)
            AddressDialogType.EDIT_SHIPPING -> {
                _editAddressId.value?.let { updateShippingAddress(userId, it, _addressForm.value) }
            }
            AddressDialogType.ADD_BILLING -> addBillingAddress(userId, _addressForm.value)
            AddressDialogType.EDIT_BILLING -> {
                _editAddressId.value?.let { updateBillingAddress(userId, it, _addressForm.value) }
            }
            else -> {}
        }
        closeAddressDialog()
    }
    fun validateAddress(): Boolean {
        val a = _addressForm.value
        return a.street.isNotBlank() && a.houseNumber.isNotBlank() && a.postalCode.isNotBlank() && a.city.isNotBlank() && a.country.isNotBlank()
    }
}