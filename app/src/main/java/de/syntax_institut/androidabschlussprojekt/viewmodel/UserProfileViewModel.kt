package de.syntax_institut.androidabschlussprojekt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.User
import de.syntax_institut.androidabschlussprojekt.data.firebase.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth

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
}