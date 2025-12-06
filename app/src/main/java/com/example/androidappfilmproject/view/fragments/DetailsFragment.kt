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

// Создаем класс DetailsFragment, который отвечает за отображение подробной информации о фильме.
class DetailsFragment : Fragment() {
    // Переменная для хранения экземпляра биндинга (nullable)
    private var _binding: FragmentDetailsBinding? = null
    // Свойство для доступа к биндингу, которое гарантирует, что он не будет null после onCreateView
    private val binding get() = _binding!!

    // Поле для хранения текущего фильма, который отображается на экране
    private var currentFilm: Film? = null

    // Инициализация ViewModel с помощью делегата viewModels и кастомной фабрики
    private val viewModel: DetailsFragmentViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(DetailsFragmentViewModel::class.java)) {
                    // Получаем Interactor из Dagger-компонента
                    val interactor = App.instance.dagger.filmInteractor()
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

        // Запускаем корутину для наблюдения за потоком фильма из ViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getFilmById(filmFromArgs.id).collectLatest { film ->
                // Обновляем текущий фильм и UI
                currentFilm = film
                setFilmsDetails(film)
            }
        }

        // Устанавливаем обработчик нажатия на кнопку "избранное"
        binding.favoriteButton.setOnClickListener {
            currentFilm?.let { film ->
                val wasInFavorites = film.isInFavorites
                viewModel.onFavoriteClicked(film)

                // Сразу обновляем UI для лучшего UX
                if (!wasInFavorites) {
                    binding.favoriteButton.setImageResource(R.drawable.baseline_favorite_24)
                    Snackbar.make(binding.root, "Добавлено в избранное", Snackbar.LENGTH_SHORT).show()
                } else {
                    binding.favoriteButton.setImageResource(R.drawable.baseline_favorite_border_24)
                    Snackbar.make(binding.root, "Удалено из избранного", Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        // Устанавливаем обработчик нажатия на кнопку "посмотреть позже"
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
            // Устанавливаем заголовок
            detailsToolbar.title = film.title

            // Загружаем постер фильма
            film.poster?.let { posterPath ->
                try {
                    // Пытаемся загрузить из ресурсов, если это локальный фильм
                    val resourceId = posterPath.toInt()
                    Glide.with(this@DetailsFragment)
                        .load(resourceId)
                        .error(R.drawable.no_poster)
                        .centerCrop()
                        .into(detailsPoster)
                } catch (e: NumberFormatException) {
                    // Загружаем из сети, если это фильм из API
                    Glide.with(this@DetailsFragment)
                        .load(ApiConstants.IMAGES_URL + "w780" + posterPath)
                        .error(R.drawable.no_poster)
                        .centerCrop()
                        .into(detailsPoster)
                }
            } ?: Glide.with(this@DetailsFragment) // Если постера нет, ставим заглушку
                .load(R.drawable.no_poster)
                .centerCrop()
                .into(detailsPoster)

            // Устанавливаем описание
            detailsDescription.text = film.description

            // Устанавливаем иконку "избранное" в зависимости от статуса фильма
            favoriteButton.setImageResource(
                if (film.isInFavorites) R.drawable.baseline_favorite_24
                else R.drawable.baseline_favorite_border_24
            )

            // Устанавливаем прогресс для кольцевого индикатора рейтинга
            val progress = (film.rating * 10).toInt().coerceIn(0, 100)
            ratingDonut.setProgress(progress)

            // Устанавливаем обработчик для кнопки "поделиться"
            detailsFab.setOnClickListener {
                val intent = Intent()
                intent.action = Intent.ACTION_SEND
                intent.putExtra(
                    Intent.EXTRA_TEXT,
                    "Check out this film: ${film.title}\n\n${film.description}"
                )
                intent.type = "text/plain"
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
