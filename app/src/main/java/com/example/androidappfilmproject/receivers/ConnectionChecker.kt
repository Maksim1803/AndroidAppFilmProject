package com.example.androidappfilmproject.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate

// Класс ConnectionChecker для отслеживания состояния батареи и зарядки
class ConnectionChecker : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        // Если интента или контекста нет, выходим
        if (intent == null || context == null) return

        // Определяем текст сообщения на основе события
        val message = when (intent.action) {
            Intent.ACTION_BATTERY_LOW -> "Критический уровень заряда! Ночная тема включена"
            Intent.ACTION_POWER_CONNECTED -> "Зарядка подключена! Дневная тема включена"
            else -> return
        }

        // Показываем стандартный Toast через ApplicationContext.
        // Он гарантированно отобразится и переживет перезапуск Activity.
        Toast.makeText(context.applicationContext, message, Toast.LENGTH_LONG).show()

        // Переключаем тему и сохраняем настройку
        when (intent.action) {
            Intent.ACTION_BATTERY_LOW -> {
                (context.applicationContext as? com.example.androidappfilmproject.App)?.dagger?.getInteractor()?.saveTheme(AppCompatDelegate.MODE_NIGHT_YES)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            Intent.ACTION_POWER_CONNECTED -> {
                (context.applicationContext as? com.example.androidappfilmproject.App)?.dagger?.getInteractor()?.saveTheme(AppCompatDelegate.MODE_NIGHT_NO)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }
}
