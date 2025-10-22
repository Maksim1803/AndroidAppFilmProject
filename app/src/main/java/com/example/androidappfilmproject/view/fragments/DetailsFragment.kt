package com.example.androidappfilmproject.view.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.androidappfilmproject.R
import com.example.androidappfilmproject.databinding.FragmentDetailsBinding
import com.example.androidappfilmproject.domain.Film
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class DetailsFragment : Fragment() {
    // View Binding для доступа к элементам разметки фрагмента
    private var _binding: FragmentDetailsBinding? = null
    // Не-null доступ к binding между onCreateView и onDestroyView
    private val binding get() = _binding!!

    // Переменная для хранения объекта фильма, который будет отображаться
    private var film: Film? = null


    //Создаем и возвращаем иерархию представлений, связанную с фрагментом.
    //Инициализируем View Binding.

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root // Возвращаем корневую View, полученную из binding
    }

    //Вызываем onCreatedView(), когда иерархия представлений фрагмента была создана.
    //Инициализируем UI-элементы и обработчики событий.

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получаем объект фильма из аргументов фрагмента
        val args = arguments
        film = args?.getParcelable<Film>("film")
        // Если фильм не был передан, показываем ошибку и закрываем Activity
        if (film == null) {
            Snackbar.make(binding.root, "Ошибка: Фильм не найден", Snackbar.LENGTH_SHORT).show()
            activity?.finish()
            return
        }

        // Настраиваем Toolbar для отображения навигации "назад"
        (activity as? AppCompatActivity)?.setSupportActionBar(binding.detailsToolbar)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Обновляем детали фильма на UI
        setFilmsDetails()

        // Устанавливаем обработчик кликов для кнопки "Избранное"
        binding.favoriteButton.setOnClickListener {
            film?.let {
                if (!it.isInFavorites) {
                    // Добавляем фильм в избранное
                    it.isInFavorites = true
                    binding.favoriteButton.setImageResource(R.drawable.baseline_favorite_24)
                    Snackbar.make(binding.root, "Добавлено в избранное", Snackbar.LENGTH_SHORT).show()
                } else {
                    // Удаляем фильм из избранного
                    it.isInFavorites = false
                    binding.favoriteButton.setImageResource(R.drawable.baseline_favorite_border_24)
                    Snackbar.make(binding.root, "Удалено из избранного", Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        // Устанавливаем обработчик кликов для кнопки "Посмотреть позже"
        binding.watchLaterButton.setOnClickListener {
            Snackbar.make(binding.root, "Добавлено в список 'Посмотреть позже'", Snackbar.LENGTH_SHORT).show()
        }

        // Применяем scaleType для постера фильма
        binding.detailsPoster.apply {
            this.scaleType = ImageView.ScaleType.CENTER_CROP
        }

        // Устанавливаем обработчик кликов для кнопки "Поделиться"
        binding.detailsFab.setOnClickListener {
            film?.let {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "Check out this film: ${it.title}\n\n${it.description}"
                    )
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(intent, "Share To:"))
            }
        }
    }


    //Обновляем UI-элементы фрагмента данными текущего фильма.
    //Запускаем в корутине жизненного цикла View для безопасного доступа к UI.

    private fun setFilmsDetails() {
        viewLifecycleOwner.lifecycleScope.launch {
            film?.let { // Проверяем, что фильм не null
                binding.apply { // Применяем операции к элементам через binding
                    detailsToolbar.title = it.title // Устанавливаем заголовок Toolbar
                    detailsPoster.setImageResource(it.poster) // Устанавливаем изображение постера
                    detailsDescription.text = it.description // Устанавливаем описание фильма

                    // Устанавливаем иконку избранного в зависимости от статуса фильма
                    favoriteButton.setImageResource(
                        if (it.isInFavorites) R.drawable.baseline_favorite_24
                        else R.drawable.baseline_favorite_border_24
                    )

                    // Вычисляем и анимируем прогресс рейтинга
                    val progress = (it.rating * 10).toInt().coerceIn(0, 100)
                    ratingDonut.setProgressAnimated(progress, 2500L)
                }
            }
        }
    }

    //Вызываем при уничтожении иерархии представлений фрагмента.
    //Обнуляем _binding для предотвращения утечек памяти.

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

