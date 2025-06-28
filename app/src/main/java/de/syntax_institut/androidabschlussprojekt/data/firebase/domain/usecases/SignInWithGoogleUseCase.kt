package de.syntax_institut.androidabschlussprojekt.data.firebase.domain.usecases

import android.util.Log
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.mappers.FirebaseAuthUserMapper
import de.syntax_institut.androidabschlussprojekt.data.firebase.repositories.UserRepository
import de.syntax_institut.androidabschlussprojekt.data.firebase.services.AuthenticationService
import com.google.firebase.auth.FirebaseUser

class SignInWithGoogleUseCase(
    private val authenticationService: AuthenticationService,
    private val userRepository: UserRepository
) {

    companion object {
        const val TAG = "SignInWithGoogleUseCase"
    }

    suspend operator fun invoke(authToken: String): FirebaseUser? {
        return try {
            Log.i(TAG, "invoke: google sign-in with authToken=$authToken")

            val firebaseGoogleUser = authenticationService.signInWithGoogle(authToken)
            if (firebaseGoogleUser == null) {
                Log.e(TAG, "Google sign-in failed: user is null")
                return null
            }

            val user = FirebaseAuthUserMapper.toDomain(firebaseGoogleUser)
            userRepository.saveUser(user)

            firebaseGoogleUser
        } catch (e: Exception) {
            Log.e(TAG, "Error during Google sign-in: ${e.message}")
            null
        }
    }
}