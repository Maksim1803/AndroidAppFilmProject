package com.example.androidappfilmproject

import android.app.Activity
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateDecelerateInterpolator
import java.util.concurrent.Executors
import kotlin.math.hypot
import kotlin.math.roundToInt

class AnimationHelper {

    companion object {
        //Переменная для того, что бы круг проявления расходился именно от иконки меню навигации
        private const val menuItems = 4

        //В метод у нас приходит 3 параметра:
        //1 - наше rootView, которое одновременно является и контейнером и объектом анимации
        //2 - активити, для того чтобы вернуть выполнение нового треда в UI поток
        //3 - позиция в меню навигации, что бы круг проявления расходился именно от иконки меню навигации
        fun performFragmentCircularRevealAnimation(rootView: View, activity: Activity, position: Int) {
            //Создаем новый тред
            Executors.newSingleThreadExecutor().execute {
                //В бесконечном цикле проверям, когда наше анимируемое view будет "прикреплено" к экрану
                while (true) {
                    //Когда оно будет прикреплено выполним код
                    if (rootView.isAttachedToWindow) {
                        //Возвращаемся в главный тред, чтобы выполнить анимацию
                        activity.runOnUiThread {
                            //Cупер сложная математика вычесления старта анимации
                            val itemCenter = rootView.width / (menuItems * 2)
                            val step = (itemCenter * 2) * (position - 1) + itemCenter

                            val x = step
                            val y = rootView.y.roundToInt() + rootView.height

                            val startRadius = 0
                            val endRadius =
                                hypot(rootView.width.toDouble(), rootView.height.toDouble())
                            //Создаем саму анимацию
                            ViewAnimationUtils.createCircularReveal(
                                rootView, x, y, startRadius.toFloat(), endRadius.toFloat()
                            ).apply {
                                //Устанавливаем время анимации
                                duration = 500
                                //Интерполятор для более естесcтвенной анимации
                                interpolator = AccelerateDecelerateInterpolator()
                                //Запускаем
                                start()
                            }
                            //Выставляем видимость нашего элемента
                            rootView.visibility = View.VISIBLE
                        }
                        return@execute
                    }
                }
            }
        }
    }
}





