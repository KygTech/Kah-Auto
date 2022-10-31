package com.jey.kahauto

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.jey.kahauto.model.Car
import com.jey.kahauto.model.Repository
import com.jey.kahauto.ui.RegistrationActivity


object NotificationManager {

    private const val CHANNEL_ID = "CHANNEL_ID"


    private fun createNotificationChannel(context: Context) {
        val name = "Notification Channel"
        val descriptionText = "Notification Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance)
        channel.description = descriptionText
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun display(context: Context, car: Car) {
        val appIntent = Intent(context, RegistrationActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, appIntent, FLAG_IMMUTABLE)

        createNotificationChannel(context)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Storage update")
            .setSmallIcon(R.drawable.car_icon)
            .setContentText("Hey! ~${car.company} ${car.model}~ has been added to storage")
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManagerCompat = NotificationManagerCompat.from(context)
        notificationManagerCompat.notify(1, builder.build())

    }

    fun getServiceNotification(context: Context): Notification {
        createNotificationChannel(context)
        val appIntent = Intent(context, RegistrationActivity::class.java)

      val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivity(
                context,
                0,
                appIntent,
                FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getActivity(
                context,
                0, appIntent, FLAG_UPDATE_CURRENT
            )
        }

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("My Service Notification")
            .setSmallIcon(R.drawable.car_icon)
            .setContentText("App still running")
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

    }


//    fun displayOver24h(context: Context): Notification {
//        createNotificationChannel(context)
//        val appIntent = Intent(context, RegistrationActivity::class.java)
//        val pendingIntent = PendingIntent.getActivity(context, 0, appIntent, FLAG_IMMUTABLE)
//
//            val carList = Repository.getInstance(context).getAllCarsAsLiveData().value
//
//        var counter = 0
//
//        if (counter == 0) {
//            return NotificationCompat.Builder(context, CHANNEL_ID)
//                .setContentTitle("Storage update")
//                .setSmallIcon(R.drawable.car_icon)
//                .setContentText(" one has been added 24h ago")
//                .setAutoCancel(true)
//                .setContentIntent(pendingIntent)
//                .build()
//        } else {
//            return NotificationCompat.Builder(context, CHANNEL_ID)
//                .setContentTitle("Storage update")
//                .setSmallIcon(R.drawable.car_icon)
//                .setContentText(" $counter has been added 24h ago")
//                .setAutoCancel(true)
//                .setContentIntent(pendingIntent)
//                .build()
//        }
//    }
}




