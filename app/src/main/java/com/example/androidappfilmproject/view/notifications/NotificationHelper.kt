package com.example.androidappfilmproject.view.notifications

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.androidappfilmproject.MainActivity
import com.example.androidappfilmproject.R
import com.example.database_module.entity.Film
import com.example.remote_module.entity.ApiConstants

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
            // Если разрешения нет, мы не можем отправить уведомление.
            // В реальном приложении здесь стоит запросить разрешение у пользователя.
            return
        }

        val mIntent = Intent(context, MainActivity::class.java).apply {
            // Кладем фильм в интент для задания со звездочкой
            putExtra("film", film)
        }

        val pendingIntent =
            PendingIntent.getActivity(
                context,
                0,
                mIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        val builder = NotificationCompat.Builder(context, NotificationConstants.CHANNEL_ID).apply {
            setSmallIcon(R.drawable.baseline_watch_later_24)
            setContentTitle("Не забудьте посмотреть!")
            setContentText(film.title)
            priority = NotificationCompat.PRIORITY_DEFAULT
            setContentIntent(pendingIntent)
            setAutoCancel(true)
        }

        val notificationManager = NotificationManagerCompat.from(context)

        Glide.with(context)
            .asBitmap()
            .load(ApiConstants.IMAGES_URL + "w500" + film.poster)
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {
                }
                @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    builder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(resource))
                    notificationManager.notify(film.id, builder.build())
                }
            })
        
        notificationManager.notify(film.id, builder.build())
    }
}
