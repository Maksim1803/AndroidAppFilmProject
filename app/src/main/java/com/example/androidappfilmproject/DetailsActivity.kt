package com.example.androidappfilmproject

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidappfilmproject.databinding.ActivityDetailsBinding

class DetailsActivity : AppCompatActivity() {

        private lateinit var binding: ActivityDetailsBinding // Объявление переменной для View Binding

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityDetailsBinding.inflate(layoutInflater) // Инфлейтим layout с использованием binding
            setContentView(binding.root) // Устанавливаем contentView через binding

            setFilmsDetails()

            // Настройка Toolbar
            setSupportActionBar(binding.detailsToolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            // Обработчики нажатий на кнопки
            binding.favoriteButton.setOnClickListener {
                // Добавить логику добавления в избранное
                Snackbar.make(binding.root, "Добавлено в избранное", Snackbar.LENGTH_SHORT).show()
            }

            binding.watchLaterButton.setOnClickListener {
                // Добавить логику "Посмотреть позже"
                Snackbar.make(binding.root, "Добавлено в список 'Посмотреть позже'", Snackbar.LENGTH_SHORT).show()
            }

            binding.detailsFab.setOnClickListener {
                // Добавить логику шаринга
                Snackbar.make(binding.root, "Функция 'Поделиться' еще не реализована", Snackbar.LENGTH_SHORT).show()
            }
        }

        private fun setFilmsDetails() {
            // Получаем наш фильм из переданного бандла
            val film = intent.extras?.get("film") as? Film // Используем as? для безопасного приведения типов

            // Проверка, что film не null
            if (film != null) {
                // Используем binding для доступа к View
                binding.detailsToolbar.title = film.title
                binding.detailsPoster.setImageResource(film.poster)
                binding.detailsDescription.text = film.description
            } else {
                // Обработка случая, когда film == null
                // Например, вывести сообщение об ошибке или закрыть Activity
                finish() // Закрываем Activity, если film не был передан
                // Или показать Toast:
                // Toast.makeText(this, "Ошибка: Фильм не найден", Toast.LENGTH_SHORT).show()
            }
        }
    }
