package de.syntax_institut.androidabschlussprojekt


import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import org.koin.core.context.startKoin
import org.koin.android.ext.koin.androidContext
import de.syntax_institut.androidabschlussprojekt.di.appModule

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Firebase initialisieren
        try {
            FirebaseApp.initializeApp(this)
            Log.d("MainApplication", "Firebase erfolgreich initialisiert")
        } catch (e: Exception) {
            Log.e("MainApplication", "Fehler bei Firebase-Initialisierung: ${e.message}")
        }

        startKoin {
            androidContext(this@MainApplication)
            modules(appModule)
        }
    }
}