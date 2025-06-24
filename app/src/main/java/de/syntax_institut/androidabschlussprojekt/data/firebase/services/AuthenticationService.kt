package de.syntax_institut.androidabschlussprojekt.data.firebase.services

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.User
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.mappers.FirebaseAuthUserMapper
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class AuthenticationService {

    private val auth = FirebaseAuth.getInstance()

    val userId: Flow<String?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.uid)
        }
        auth.addAuthStateListener(authStateListener)
        awaitClose { auth.removeAuthStateListener(authStateListener) }
    }.distinctUntilChanged()

    val isSignedIn: Flow<Boolean> = userId.map { it != null }.distinctUntilChanged()

    suspend fun signInWithGoogle(token: String): FirebaseUser? {
        val credential = GoogleAuthProvider.getCredential(token, null)
        val authResult = auth.signInWithCredential(credential).await()
        return authResult.user
    }

    fun signOut() {
        auth.signOut()
    }
}