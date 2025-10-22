package com.example.androidappfilmproject.utils

import android.animation.Animator
import android.app.Activity
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateDecelerateInterpolator
import java.util.concurrent.Executors
import kotlin.math.hypot
import kotlin.math.roundToInt
import kotlin.time.Duration

// Вариант без биндингов:
 class AnimationHelper {

    companion object {
        //Переменная для того, что бы круг проявления расходился именно от иконки меню навигации
        private const val menuItems = 4

        //В метод у нас приходит 3 параметра:
        //1 - наше rootView, которое одновременно является и контейнером и объектом анимации
        //2 - активити, для того чтобы вернуть выполнение нового треда в UI поток
        //3 - позиция в меню навигации, что бы круг проявления расходился именно от иконки меню навигации
        fun performFragmentCircularRevealAnimation(rootView: View, activity: Activity, position: Int) {
            Executors.newSingleThreadExecutor().execute {
                while (true) {
                    if (rootView.isAttachedToWindow) {
                        activity.runOnUiThread {
                            val itemCenter = rootView.width / (menuItems * 2)
                            val step = (itemCenter * 2) * (position - 1) + itemCenter

                            val x = step
                            val y = rootView.y.roundToInt() + rootView.height

                            val startRadius = 0
                            val endRadius =
                                hypot(rootView.width.toDouble(), rootView.height.toDouble())

                            ViewAnimationUtils.createCircularReveal(
                                rootView, x, y, startRadius.toFloat(), endRadius.toFloat()
                            ).apply {
                                // Вместо Animator.setDuration = 500 (modul_32)
                                // используем this.duration = 500 или просто duration = 500
                                duration = 500L // Длительность всегда должна быть Long

                                interpolator = AccelerateDecelerateInterpolator()
                                start()
                            }
                            rootView.visibility = View.VISIBLE
                        }
                        return@execute
                    }
                }
            }
        }
    }
}