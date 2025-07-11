package de.syntax_institut.androidabschlussprojekt.data.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay

class OfferNotificationWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            NotificationHelper.createChannel(applicationContext)


            for (i in 0..100 step 25) {
                NotificationHelper.showProgress(applicationContext, i)
                delay(500)
            }

            NotificationHelper.showSuccess(applicationContext)
            delay(3000) 
            NotificationHelper.cancel(applicationContext)

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}