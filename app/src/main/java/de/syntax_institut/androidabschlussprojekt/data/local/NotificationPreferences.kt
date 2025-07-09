package de.syntax_institut.androidabschlussprojekt.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.notificationDataStore by preferencesDataStore(name = "notification_prefs")

object NotificationPrefsKeys {
    val OPT_IN = booleanPreferencesKey("notification_opt_in")
    val ASK_AGAIN = booleanPreferencesKey("notification_ask_again")
}

class NotificationPreferences(private val context: Context) {
    val optIn: Flow<Boolean> = context.notificationDataStore.data.map { it[NotificationPrefsKeys.OPT_IN] ?: false }
    val askAgain: Flow<Boolean> = context.notificationDataStore.data.map { it[NotificationPrefsKeys.ASK_AGAIN] ?: true }

    suspend fun setOptIn(value: Boolean) {
        context.notificationDataStore.edit { it[NotificationPrefsKeys.OPT_IN] = value }
    }
    suspend fun setAskAgain(value: Boolean) {
        context.notificationDataStore.edit { it[NotificationPrefsKeys.ASK_AGAIN] = value }
    }
} 