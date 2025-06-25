package de.syntax_institut.androidabschlussprojekt


import android.app.Application
import com.google.firebase.FirebaseApp
import org.koin.core.context.startKoin
import org.koin.android.ext.koin.androidContext
import de.syntax_institut.androidabschlussprojekt.di.appModule

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        startKoin {
            androidContext(this@MainApplication)
            modules(appModule)
        }
    }
}