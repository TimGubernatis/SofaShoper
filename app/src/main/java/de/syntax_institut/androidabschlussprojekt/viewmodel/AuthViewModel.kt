package de.syntax_institut.androidabschlussprojekt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.User
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.usecases.ObserveCurrentUserUseCase
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.usecases.SignInWithGoogleUseCase
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.usecases.SignOutUseCase
import de.syntax_institut.androidabschlussprojekt.data.firebase.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel(
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    private val userRepository: UserRepository
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSigningOut = MutableStateFlow(false)
    val isSigningOut: StateFlow<Boolean> = _isSigningOut.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                observeCurrentUserUseCase().collect { currentUser ->
                    _user.value = currentUser
                }
            } catch (e: Exception) {
                // Firebase nicht verfügbar, App trotzdem funktionsfähig halten
                _errorMessage.value = "Authentifizierung nicht verfügbar"
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val firebaseUser: FirebaseUser? = signInWithGoogleUseCase(idToken)
                firebaseUser?.let {
                    val user = User(
                        id = it.uid,
                        email = it.email ?: "",
                        displayName = it.displayName
                    )
                    userRepository.saveUser(user)
                    _user.value = user
                }
            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("network") == true -> "Netzwerkfehler. Bitte überprüfen Sie Ihre Internetverbindung."
                    e.message?.contains("invalid") == true -> "Ungültige Anmeldedaten. Bitte versuchen Sie es erneut."
                    else -> "Fehler bei der Google-Anmeldung: ${e.localizedMessage ?: "Unbekannter Fehler"}"
                }
                _errorMessage.value = errorMessage
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
                val currentUser = auth.currentUser
                currentUser?.let {
                    val user = userRepository.getUser(it.uid)
                    _user.value = user
                }
            } catch (e: FirebaseAuthInvalidUserException) {
                _errorMessage.value = "Benutzer nicht gefunden. Bitte überprüfen Sie Ihre E-Mail-Adresse."
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                _errorMessage.value = "Falsches Passwort. Bitte versuchen Sie es erneut."
            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("network") == true -> "Netzwerkfehler. Bitte überprüfen Sie Ihre Internetverbindung."
                    e.message?.contains("too many requests") == true -> "Zu viele Anmeldeversuche. Bitte warten Sie einen Moment."
                    else -> "Fehler beim Login: ${e.localizedMessage ?: "Unbekannter Fehler"}"
                }
                _errorMessage.value = errorMessage
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(email: String, password: String, firstName: String = "", lastName: String = "", displayName: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                auth.signInWithEmailAndPassword(email, password).await()
                val currentUser = auth.currentUser
                currentUser?.let {
                    val user = User(
                        id = it.uid,
                        email = email,
                        firstName = firstName,
                        lastName = lastName,
                        displayName = displayName
                    )
                    userRepository.saveUser(user)
                    _user.value = user
                }
            } catch (e: FirebaseAuthWeakPasswordException) {
                _errorMessage.value = "Passwort zu schwach. Bitte verwenden Sie mindestens 6 Zeichen."
            } catch (e: FirebaseAuthUserCollisionException) {
                _errorMessage.value = "Ein Konto mit dieser E-Mail-Adresse existiert bereits."
            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("network") == true -> "Netzwerkfehler. Bitte überprüfen Sie Ihre Internetverbindung."
                    e.message?.contains("invalid email") == true -> "Ungültige E-Mail-Adresse."
                    else -> "Fehler bei der Registrierung: ${e.localizedMessage ?: "Unbekannter Fehler"}"
                }
                _errorMessage.value = errorMessage
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            println("AuthViewModel: Logout gestartet")
            _isSigningOut.value = true
            try {
                signOutUseCase()
                _user.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Fehler beim Abmelden: ${e.localizedMessage ?: "Unbekannter Fehler"}"
            } finally {
                _isSigningOut.value = false
                println("AuthViewModel: Logout fertig, User=null")
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}