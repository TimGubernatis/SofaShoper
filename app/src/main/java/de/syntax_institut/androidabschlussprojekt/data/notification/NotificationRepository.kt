package de.syntax_institut.androidabschlussprojekt.data.notification

import android.content.Context
import de.syntax_institut.androidabschlussprojekt.data.local.NotificationPreferences
import kotlinx.coroutines.flow.Flow

class NotificationRepository(context: Context) {
    private val prefs = NotificationPreferences(context)
    val optIn: Flow<Boolean> = prefs.optIn
    val askAgain: Flow<Boolean> = prefs.askAgain
    suspend fun setOptIn(value: Boolean) = prefs.setOptIn(value)
    suspend fun setAskAgain(value: Boolean) = prefs.setAskAgain(value)
} 