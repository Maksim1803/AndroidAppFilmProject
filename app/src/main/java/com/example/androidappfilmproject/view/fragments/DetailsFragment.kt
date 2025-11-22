package com.example.androidappfilmproject.view.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.androidappfilmproject.App
import com.example.androidappfilmproject.R
import com.example.androidappfilmproject.data.ApiConstants
import com.example.androidappfilmproject.databinding.FragmentDetailsBinding
import com.example.androidappfilmproject.domain.Film
import com.example.androidappfilmproject.viewmodel.DetailsFragmentViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// Создаем класс DetailsFragment, который отвечает за отображение
// подробной информации о фильме.
class DetailsFragment : Fragment() {
    // Переменная для хранения экземпляра биндинга (nullable)
    private var _binding: FragmentDetailsBinding? = null
    // Свойство для доступа к биндингу, которое гарантирует,
    // что он не будет null после onCreateView
    private val binding get() = _binding!!

    // Инициализация ViewModel с помощью делегата viewModels и кастомной фабрики
    private val viewModel: DetailsFragmentViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(DetailsFragmentViewModel::class.java)) {
                    // Получаем Interactor из синглтона App
                    val interactor = App.instance.interactor
                    @Suppress("UNCHECKED_CAST")
                    // Создаем экземпляр DetailsFragmentViewModel
                    return DetailsFragmentViewModel(interactor) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

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

        // Запускаем корутину для наблюдения за данными фильма из ViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getFilmById(filmFromArgs.id).collectLatest { film ->
                if (film != null) {
                    // Обновляем UI с деталями фильма
                    setFilmsDetails(film)
                }
            }
        }

        // Слушатель для кнопки "Посмотреть позже"
        binding.watchLaterButton.setOnClickListener {
            Snackbar.make(binding.root, "Добавлено в список 'Посмотреть позже'", Snackbar.LENGTH_SHORT).show()
        }

        // Настраиваем отображение постера
        binding.detailsPoster.apply {
            this.scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }

    // Метод для установки деталей фильма в элементы UI
    private fun setFilmsDetails(film: Film) {
        binding.apply {
            // Устанавливаем заголовок Toolbar
            detailsToolbar.title = film.title
            // Загружаем постер фильма с помощью Glide
            try {
                // Пытаемся загрузить как ресурс
                val resourceId = film.poster.toInt()
                Glide.with(this@DetailsFragment)
                    .load(resourceId)
                    .centerCrop()
                    .into(detailsPoster)
            } catch (e: NumberFormatException) {
                // Если не получилось как ресурс, загружаем по URL
                Glide.with(this@DetailsFragment)
                    .load(ApiConstants.IMAGES_URL + "w780" + film.poster)
                    .centerCrop()
                    .into(detailsPoster)
            }

            // Устанавливаем описание фильма
            detailsDescription.text = film.description

            // Устанавливаем иконку для кнопки "избранное" в зависимости от статуса фильма
            favoriteButton.setImageResource(
                if (film.isInFavorites) R.drawable.baseline_favorite_24
                else R.drawable.baseline_favorite_border_24
            )

            // Устанавливаем прогресс для кольцевого индикатора рейтинга
            val progress = (film.rating * 10).toInt().coerceIn(0, 100)
            ratingDonut.setProgress(progress) // Анимация убрана

            // Слушатель для кнопки "избранное"
            favoriteButton.setOnClickListener {
                // Сообщаем ViewModel о клике
                viewModel.onFavoriteClicked(film)
                // Показываем сообщение о добавлении/удалении из избранного
                val message = if (!film.isInFavorites) "Добавлено в избранное" else "Удалено из избранного"
                Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
            }

            // Слушатель для кнопки "поделиться" (FAB)
            detailsFab.setOnClickListener {
                // Создаем Intent для отправки данных
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "Check out this film: ${film.title}\n\n${film.description}"
                    )
                    type = "text/plain"
                }
                // Запускаем chooser для выбора приложения
                startActivity(Intent.createChooser(intent, "Share To:"))
            }
        }
    }

    // Метод, вызываемый при уничтожении View фрагмента
    override fun onDestroyView() {
        super.onDestroyView()
        // Очищаем ссылку на биндинг, чтобы избежать утечек памяти
        _binding = null
    }
}
