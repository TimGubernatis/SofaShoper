package de.syntax_institut.androidabschlussprojekt.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.syntax_institut.androidabschlussprojekt.data.notification.NotificationRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotificationViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = NotificationRepository(app)
    val optIn: StateFlow<Boolean> = repo.optIn.stateIn(viewModelScope, SharingStarted.Lazily, false)
    val askAgain: StateFlow<Boolean> = repo.askAgain.stateIn(viewModelScope, SharingStarted.Lazily, true)
    var showOptInDialog = askAgain
    fun setOptIn(value: Boolean) {
        viewModelScope.launch { repo.setOptIn(value) }
    }
    fun setAskAgain(value: Boolean) {
        viewModelScope.launch { repo.setAskAgain(value) }
    }
} 