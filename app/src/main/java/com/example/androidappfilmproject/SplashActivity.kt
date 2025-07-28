package com.example.androidappfilmproject

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

// Модуль 29. Класс-активити для Splash Screen (экран приветствия) с векторной
// анимацией (задание под звездочкой).

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Устанавливаем макет активности
        setContentView(R.layout.activity_splash)
        // Находим ImageView по идентификатору
        val imageView = findViewById<ImageView>(R.id.splash_view)
        imageView.setImageResource(R.drawable.logo)
        // Загружаем анимацию масштабирования
        val scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        imageView.startAnimation(scaleAnimation)
        // Запускаем задержку, после которой переходим к MainActivity
        Handler(Looper.getMainLooper()).postDelayed({
            // Создаем интент для перехода на MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }
}

