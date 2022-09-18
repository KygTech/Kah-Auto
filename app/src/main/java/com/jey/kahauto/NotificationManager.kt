package com.jey.kahauto

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object NotificationManager {

    val CHANNEL_ID = "CHANNEL_ID"

    private fun createNotificationChannel(context: Context){
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
        val appIntent = Intent(context, MainActivity::class.java)
        val pendingIntent =PendingIntent.getActivity(context, 0, appIntent, 0)

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


}