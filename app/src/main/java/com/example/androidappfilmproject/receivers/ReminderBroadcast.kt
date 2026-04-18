package com.example.androidappfilmproject.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.androidappfilmproject.App
import com.example.androidappfilmproject.view.notifications.NotificationConstants
import com.example.androidappfilmproject.view.notifications.NotificationHelper
import com.example.database_module.entity.Film
import io.reactivex.rxjava3.schedulers.Schedulers

// Класс для уведомлений
class ReminderBroadcast: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        val bundle = intent?.getBundleExtra(NotificationConstants.FILM_BUNDLE_KEY)
        val film: Film? = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            bundle?.getParcelable(NotificationConstants.FILM_KEY, Film::class.java)
        } else {
            @Suppress("DEPRECATION")
            bundle?.getParcelable(NotificationConstants.FILM_KEY)
        }

        if (context != null && film != null) {
            NotificationHelper.createNotification(context, film)
            
            // Сбрасываем флаг "Посмотреть позже", так как напоминание сработало.
            // Это автоматически уберет фильм из списка "Посмотреть позже".
            val updatedFilm = film.copy(isInWatchLater = false, watchLaterTime = 0)
            (context.applicationContext as App).dagger.getInteractor().updateFilmInDb(updatedFilm)
                .subscribeOn(Schedulers.io())
                .subscribe({}, { it.printStackTrace() })
        }
    }
}
