package com.mready.mtgtreasury.services

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mready.mtgtreasury.R
import com.mready.mtgtreasury.ui.MainActivity
import java.util.Random

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class NotificationService : FirebaseMessagingService() {
    init {

    }
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val notificationManagerCompat = NotificationManagerCompat.from(this)

        NotificationChannel(
            "test_channel_id",
            "test",
            NotificationManager.IMPORTANCE_DEFAULT
        ).also {
            it.description = "This is a test channel"
            notificationManagerCompat.createNotificationChannel(it)
        }

        val notificationIntent = Intent(this, MainActivity::class.java).apply {
            addFlags(FLAG_ACTIVITY_CLEAR_TOP or FLAG_ACTIVITY_SINGLE_TOP)
        }

        val pendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        val builder =
            NotificationCompat.Builder(this, "test_channel_id")
                .setSmallIcon(R.drawable.ic_bnav_deck)
                .setContentTitle(remoteMessage.notification?.title ?: "")
                .setContentText(remoteMessage.notification?.body ?: "")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notificationPermissionGranted =
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

            if (notificationPermissionGranted) {
                notificationManagerCompat.notify(Random().nextInt(), builder.build())
            }
        } else {
            notificationManagerCompat.notify(Random().nextInt(), builder.build())
        }
    }



}