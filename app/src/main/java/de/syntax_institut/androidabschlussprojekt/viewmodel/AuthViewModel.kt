package de.syntax_institut.androidabschlussprojekt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.User
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.usecases.ObserveCurrentUserUseCase
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.usecases.SignInWithGoogleUseCase
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.usecases.SignOutUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthViewModel(
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val observeCurrentUserUseCase: ObserveCurrentUserUseCase
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {

        viewModelScope.launch {
            observeCurrentUserUseCase().collect { currentUser ->
                _user.value = currentUser
            }
        }
    }


    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                signInWithGoogleUseCase(idToken)

            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Unbekannter Fehler"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                auth.signInWithEmailAndPassword(email, password).await()
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Fehler beim Login"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            signOutUseCase()
            _user.value = null
        }
    }
}