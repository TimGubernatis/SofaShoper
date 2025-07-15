package de.syntax_institut.androidabschlussprojekt.data.firebase.domain.usecases

import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.User
import de.syntax_institut.androidabschlussprojekt.data.firebase.repositories.UserRepository
import de.syntax_institut.androidabschlussprojekt.data.firebase.services.AuthenticationService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.catch

class ObserveCurrentUserUseCase(
    private val authenticationService: AuthenticationService,
    private val userRepository: UserRepository
) {

    companion object {
        const val TAG = "ObserveCurrentUserUseCase"
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke() : Flow<User?> {
        return try {
            authenticationService.userId.flatMapConcat { userId ->
                if (userId == null) {
                    flowOf(null)
                } else {
                    userRepository.observeUser(userId)
                }
            }.catch { e ->
                emit(null)
            }
        } catch (e: Exception) {
            flowOf(null)
        }
    }
}