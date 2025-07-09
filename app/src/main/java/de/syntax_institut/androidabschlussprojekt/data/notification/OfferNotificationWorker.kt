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
        NotificationHelper.createChannel(applicationContext)
        for (i in 0..100 step 10) {
            NotificationHelper.showProgress(applicationContext, i)
            delay(200)
        }
        NotificationHelper.showSuccess(applicationContext)
        delay(2000)
        NotificationHelper.cancel(applicationContext)
        return Result.success()
    }
} 