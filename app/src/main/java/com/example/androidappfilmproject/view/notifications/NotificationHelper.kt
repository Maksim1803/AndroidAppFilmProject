package com.example.androidappfilmproject.view.notifications

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.androidappfilmproject.App
import com.example.androidappfilmproject.MainActivity
import com.example.androidappfilmproject.R
import com.example.androidappfilmproject.receivers.ReminderBroadcast
import com.example.database_module.entity.Film
import com.example.remote_module.entity.ApiConstants
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.Calendar

// Класс-синглтон (объект, существующий в приложении в единственном экземпляре),
// для реализации логики уведомлений
object NotificationHelper {

    // Метод для создания уведомления (нотификации)
    fun createNotification(context: Context, film: Film) {
        // Проверка разрешения на уведомления (требуется для Android 13+)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val mIntent = Intent(context, MainActivity::class.java).apply {
            putExtra("film", film)
        }

        val pendingIntent =
            PendingIntent.getActivity(
                context,
                film.id,
                mIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        val builder = NotificationCompat.Builder(context, NotificationConstants.CHANNEL_ID).apply {
            setSmallIcon(R.drawable.baseline_watch_later_24)
            setContentTitle(context.getString(R.string.notification_title))
            setContentText(film.title)
            priority = NotificationCompat.PRIORITY_HIGH
            setContentIntent(pendingIntent)
            setAutoCancel(true)
            setOnlyAlertOnce(true) // Чтобы не было звука при обновлении картинкой
            setDefaults(NotificationCompat.DEFAULT_ALL)
        }

        val notificationManager = NotificationManagerCompat.from(context)

        Glide.with(context)
            .asBitmap()
            .load(ApiConstants.IMAGES_URL + "w500" + film.poster)
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {}
                @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    builder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(resource))
                    notificationManager.notify(film.id, builder.build())
                }
            })
    }

    // Метод, который будет вызывать DatePickerDialog и TimePickerDialog:
    fun notificationSet(context: Context, film: Film, onComplete: (() -> Unit)? = null) {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        DatePickerDialog(
            context,
            { _, dpdYear, dpdMonth, dayOfMonth ->
                val timeSetListener =
                    TimePickerDialog.OnTimeSetListener { _, hourOfDay, pickerMinute ->
                        val pickedDateTime = Calendar.getInstance()
                        pickedDateTime.set(
                            dpdYear,
                            dpdMonth,
                            dayOfMonth,
                            hourOfDay,
                            pickerMinute,
                            0
                        )
                        val dateTimeInMillis = pickedDateTime.timeInMillis
                        
                        // Обновляем состояние фильма в БД
                        val updatedFilm = film.copy(isInWatchLater = true, watchLaterTime = dateTimeInMillis)
                        (context.applicationContext as App).dagger.getInteractor().updateFilmInDb(updatedFilm)
                            .subscribeOn(Schedulers.io())
                            .subscribe({
                                createWatchLaterEvent(context, dateTimeInMillis, updatedFilm)
                                onComplete?.invoke()
                            }, { it.printStackTrace() })
                    }

                TimePickerDialog(
                    context,
                    timeSetListener,
                    currentHour,
                    currentMinute,
                    true
                ).show()

            },
            currentYear,
            currentMonth,
            currentDay
        ).show()
    }

    // Метод для отмены уведомления
    @SuppressLint("CheckResult")
    fun cancelNotification(context: Context, film: Film) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(film.title, null, context, ReminderBroadcast::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            film.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        
        // Обновляем БД
        val updatedFilm = film.copy(isInWatchLater = false, watchLaterTime = 0)
        (context.applicationContext as App).dagger.getInteractor().updateFilmInDb(updatedFilm)
            .subscribeOn(Schedulers.io())
            .subscribe({}, { it.printStackTrace() })
    }

    // Метод, устанавливающий сам Alarm
    private fun createWatchLaterEvent(context: Context, dateTimeInMillis: Long, film: Film) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(film.title, null, context, ReminderBroadcast::class.java)
        val bundle = Bundle()
        bundle.putParcelable(NotificationConstants.FILM_KEY, film)
        intent.putExtra(NotificationConstants.FILM_BUNDLE_KEY, bundle)
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            film.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            dateTimeInMillis,
            pendingIntent
        )
    }
}
