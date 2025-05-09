package com.example.androidappfilmproject

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.androidappfilmproject.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding // Объявление переменной для View Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Инициализация View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavigation()

        //Запуск с кнопки анимации для poster2
        binding.poster2?.setOnClickListener() {
            startPoster2Animation()
        }

        //Запуск с кнопки анимации для poster3
        val animation_Poster3 = AnimationUtils.loadAnimation(this, R.anim.poster3_anim)

        binding.poster3?.setOnClickListener {
            binding.poster3!!.startAnimation(animation_Poster3)

        }

        //Запуск с кнопки анимации для poster4


        binding.poster4?.setOnClickListener {
            startPoster4Animation()
        }
    }

    private fun initNavigation() {
        //Надпись для верхней иконки "Навигация"
        binding.topAppBar?.setNavigationOnClickListener {
            Toast.makeText(this, "Когда-нибудь здесь будет навигация...", Toast.LENGTH_SHORT)
                .show()
        }

        //Надпись для верхней иконки "Настройки"
        binding.topAppBar?.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.settings -> {
                    Toast.makeText(this, "Настройки", Toast.LENGTH_SHORT).show()
                    true
                }

                else -> false
            }
        }

        //Надписи для иконок нижнего меню
        binding.bottomNavigation?.setOnNavigationItemSelectedListener {

            when (it.itemId) {
                R.id.favorites -> {
                    Toast.makeText(this, "Избранное", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.watch_later -> {
                    Toast.makeText(this, "Посмотреть позже", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.selections -> {
                    Toast.makeText(this, "Подборки", Toast.LENGTH_SHORT).show()
                    true
                }

                else -> false
            }
        }
    }

    private fun startPoster2Animation() {
        // Исчезновение постера
        binding.poster2?.animate()
            ?.setDuration(4000)
            ?.setInterpolator(DecelerateInterpolator())
            ?.alpha(0f)
            ?.withStartAction {
                Log.d("Animation", "Исчезновение постера 2")
                Toast.makeText(this@MainActivity, "Исчезновение постера 2", Toast.LENGTH_SHORT)
                    .show()
            }
            ?.withEndAction {
                // Появление ракеты (после исчезновения)
                binding.poster2?.animate()
                    ?.setDuration(4000)
                    ?.alpha(1f)
                    ?.withStartAction {
                        Log.d("Animation", "Появление постера 2")
                        Toast.makeText(this@MainActivity, "Появление постера 2", Toast.LENGTH_SHORT)
                            .show()
                    }
                    ?.start()
            }
            ?.start()
    }

    private fun startPoster4Animation() {

        val justAnim = ObjectAnimator.ofFloat(binding.poster4, View.TRANSLATION_Y, -1000f)
        justAnim.duration = 4500

        val nightAnim = ObjectAnimator.ofFloat(binding.night, View.ALPHA, 1f)
        nightAnim.duration = 4500

        val animatorPoster4 = AnimatorSet()
        animatorPoster4.playTogether(justAnim, nightAnim)

        animatorPoster4.addListener(object : AnimatorListenerAdapter() {


            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                // Возвращаем элементы в исходное состояние после окончания анимации
                binding.poster4?.translationY  = 0f
                binding.night?.alpha = 0f
            }
        })
        animatorPoster4.start()
    }
}


//Может пригодится...

//    fun onClickToast(view: View) {
//        Toast.makeText(this, "Меню кинофильмов", Toast.LENGTH_SHORT).show()
//    }
//
//    fun onClickToast2(view: View) {
//        Toast.makeText(this, "Избранные кинофильмы", Toast.LENGTH_SHORT).show()
//    }
//
//    fun onClickToast3(view: View) {
//        Toast.makeText(this, "Кинофильмы для просмотра попозже", Toast.LENGTH_SHORT).show()
//    }
//
//    fun onClickToast4(view: View) {
//        Toast.makeText(this, "Кинофильмы в подборке", Toast.LENGTH_SHORT).show()
//    }
//
//    fun onClickToast5(view: View) {
//        Toast.makeText(this, "Настройки для корректной работы", Toast.LENGTH_SHORT).show()
//    }








