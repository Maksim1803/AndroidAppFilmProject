package com.example.androidappfilmproject.view.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.androidappfilmproject.data.entity.Film
import com.example.androidappfilmproject.databinding.FragmentDetailsBinding
import com.google.android.material.snackbar.Snackbar

// Создаем класс LocalDetailsFragment, который отвечает за отображение
// подробной информации о фильме из локальной базы данных (демо-режим).
class LocalDetailsFragment : Fragment() {
    // Переменная для хранения экземпляра биндинга (nullable)
    private var _binding: FragmentDetailsBinding? = null
    // Свойство для доступа к биндингу, которое гарантирует,
    // что он не будет null после onCreateView
    private val binding get() = _binding!!

    // Метод для создания и возвращения View фрагмента
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Инициализируем биндинг
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Метод, вызываемый после создания View
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получаем объект Film из аргументов фрагмента, с учетом версии Android
        val filmFromArgs = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("film", Film::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable("film")
        }

        // Если фильм не найден, показываем сообщение и закрываем активность
        if (filmFromArgs == null) {
            Snackbar.make(binding.root, "Ошибка: Фильм не найден", Snackbar.LENGTH_SHORT).show()
            activity?.finish()
            return
        }

        // Устанавливаем Toolbar
        (activity as? AppCompatActivity)?.setSupportActionBar(binding.detailsToolbar)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Устанавливаем детали фильма
        setFilmsDetails(filmFromArgs)

        // Отключаем кнопки, так как это демо-режим
        val disabledMessage = "Функция недоступна в демо-режиме"
        binding.watchLaterButton.setOnClickListener {
            Snackbar.make(binding.root, disabledMessage, Snackbar.LENGTH_SHORT).show()
        }
        binding.favoriteButton.setOnClickListener {
            Snackbar.make(binding.root, disabledMessage, Snackbar.LENGTH_SHORT).show()
        }
        binding.detailsFab.setOnClickListener {
            Snackbar.make(binding.root, disabledMessage, Snackbar.LENGTH_SHORT).show()
        }

        // Настраиваем отображение постера
        binding.detailsPoster.apply {
            this.scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }

    // Метод для установки деталей фильма в элементы UI
    private fun setFilmsDetails(film: Film) {
        binding.apply {
            detailsToolbar.title = film.title

            // Загружаем постер фильма из ресурсов
            Glide.with(this@LocalDetailsFragment)
                .load(film.poster?.toInt())
                .centerCrop()
                .into(detailsPoster)

            detailsDescription.text = film.description

            // Устанавливаем прогресс для кольцевого индикатора рейтинга
            val progress = (film.rating * 10).toInt().coerceIn(0, 100)
            ratingDonut.setProgress(progress)
        }
    }

    // Метод, вызываемый при уничтожении View фрагмента
    override fun onDestroyView() {
        super.onDestroyView()
        // Очищаем ссылку на биндинг, чтобы избежать утечек памяти
        _binding = null
    }
}
