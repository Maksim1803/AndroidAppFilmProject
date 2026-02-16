package com.example.androidappfilmproject.utils

import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateDecelerateInterpolator
import kotlin.math.hypot

// Создаем класс-помощник для работы с анимациями.
class AnimationHelper {

    companion object {
        // Константа, определяющая количество элементов в нижнем меню навигации.
        private const val menuItems = 5

        // Метод для выполнения анимации кругового появления (Circular Reveal) для фрагмента
        fun performFragmentCircularRevealAnimation(rootView: View, position: Int) {
            // Используем rootView.post (безопасная замена фоновым потокам).
            // Код выполнится в UI-потоке ровно тогда, когда View будет прикреплена и измерена.
            rootView.post {
                // Проверяем, не отсоединилась ли View (защита от крашей)
                if (!rootView.isAttachedToWindow) return@post

                // Рассчитываем центральную точку элемента меню, от которого начнется анимация.
                val itemCenter = rootView.width / (menuItems * 2)
                val step = (itemCenter * 2) * (position - 1) + itemCenter

                // Координаты центра анимации (снизу от кнопок меню).
                val x = step
                val y = rootView.height

                // Начальный и конечный радиусы для анимации.
                val startRadius = 0
                val endRadius = hypot(rootView.width.toDouble(), rootView.height.toDouble())

                // Создаем и настраиваем анимацию кругового появления.
                try {
                    ViewAnimationUtils.createCircularReveal(
                        rootView, x, y, startRadius.toFloat(), endRadius.toFloat()
                    ).apply {
                        duration = 500L
                        interpolator = AccelerateDecelerateInterpolator()
                        start()
                    }
                    // Делаем View видимым (так как в XML стоит invisible).
                    rootView.visibility = View.VISIBLE
                } catch (_: Exception) {
                    // Если анимация не удалась, просто показываем экран.
                    rootView.visibility = View.VISIBLE
                }
            }
        }
    }
}
