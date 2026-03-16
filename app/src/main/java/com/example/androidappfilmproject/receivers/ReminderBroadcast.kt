package com.example.androidappfilmproject.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.androidappfilmproject.view.notifications.NotificationConstants
import com.example.androidappfilmproject.view.notifications.NotificationHelper
import com.example.database_module.entity.Film

// Класс для уведомлений
class ReminderBroadcast: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        val bundle = intent?.getBundleExtra(NotificationConstants.FILM_BUNDLE_KEY)
        val film: Film = bundle?.get(NotificationConstants.FILM_KEY) as Film

        NotificationHelper.createNotification(context!!, film)
    }
}
