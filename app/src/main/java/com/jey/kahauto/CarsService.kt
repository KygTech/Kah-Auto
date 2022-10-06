package com.jey.kahauto

import android.app.Service
import android.content.Intent
import android.os.IBinder


class CarsService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationManager.getServiceNotification(this)
        startForeground(1, notification)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

}