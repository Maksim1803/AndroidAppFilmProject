package com.example.androidappfilmproject.utils

import android.app.Activity
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateDecelerateInterpolator
import java.util.concurrent.Executors
import kotlin.math.hypot
import kotlin.math.roundToInt

// Создаем класс-помощник для работы с анимациями.
class AnimationHelper {

    // Создаем companion object, чтобы методы были доступны без создания экземпляра класса.
    companion object {
        // Константа, определяющая количество элементов в нижнем меню навигации.
        private const val menuItems = 4


         // Метод для выполнения анимации кругового появления (Circular Reveal) для фрагмента:

         // @param rootView Корневое представление (View) фрагмента, которое будет анимировано.
         // @param activity Текущая активность, необходимая для выполнения кода в UI-потоке.
         // @param position Позиция элемента в меню навигации, от которого начнется анимация.

        fun performFragmentCircularRevealAnimation(rootView: View, activity: Activity, position: Int) {
            // Создаем и запускаем новый поток, чтобы дождаться, пока View будет присоединено к окну.
            Executors.newSingleThreadExecutor().execute {
                while (true) {
                    // Проверяем, присоединено ли View к окну.
                    if (rootView.isAttachedToWindow) {
                        // Если да, выполняем код в UI-потоке.
                        activity.runOnUiThread {
                            // Рассчитываем центральную точку элемента меню, от которого начнется анимация.
                            val itemCenter = rootView.width / (menuItems * 2)
                            val step = (itemCenter * 2) * (position - 1) + itemCenter

                            // Координаты центра анимации.
                            val x = step
                            val y = rootView.y.roundToInt() + rootView.height

                            // Начальный и конечный радиусы для анимации.
                            val startRadius = 0
                            val endRadius = hypot(rootView.width.toDouble(), rootView.height.toDouble())

                            // Создаем и настраиваем анимацию кругового появления.
                            ViewAnimationUtils.createCircularReveal(
                                rootView, x, y, startRadius.toFloat(), endRadius.toFloat()
                            ).apply {
                                duration = 500L // Длительность анимации.
                                interpolator = AccelerateDecelerateInterpolator() // Интерполятор для плавности.
                                start() // Запускаем анимацию.
                            }
                            // Делаем View видимым.
                            rootView.visibility = View.VISIBLE
                        }
                        // Выходим из цикла и завершаем поток.
                        return@execute
                    }
                }
            }
        }
    }
}
