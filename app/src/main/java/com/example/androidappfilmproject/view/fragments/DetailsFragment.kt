package com.example.androidappfilmproject.view.fragments

import android.content.Context
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
import javax.inject.Inject

// Создаем класс DetailsFragment, который отвечает за отображение подробной информации о фильме.
class DetailsFragment : Fragment() {
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private var currentFilm: Film? = null

    // Внедряем нашу единую фабрику для ViewModel
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    // Получаем ViewModel с помощью Dagger-фабрики
    private val viewModel: DetailsFragmentViewModel by viewModels { viewModelFactory }

    // Вызывается при присоединении фрагмента к контексту.
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Выполняем Dagger-инъекцию, чтобы получить viewModelFactory
        (requireActivity().application as App).dagger.inject(this)
    }

    // Вызывается для создания иерархии представлений, связанной с фрагментом.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Вызывается сразу после того, как onCreateView() завершил свою работу.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получаем фильм из аргументов фрагмента.
        val filmFromArgs = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("film", Film::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable("film")
        }

        // Если фильм не найден, показываем ошибку и закрываем экран.
        if (filmFromArgs == null) {
            Snackbar.make(binding.root, "Ошибка: Фильм не найден", Snackbar.LENGTH_SHORT).show()
            activity?.finish()
            return
        }

        // Настраиваем Toolbar.
        (activity as? AppCompatActivity)?.setSupportActionBar(binding.detailsToolbar)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Устанавливаем текущий фильм и отображаем его детали.
        currentFilm = filmFromArgs
        setFilmsDetails(filmFromArgs)

        // Подписываемся на изменения фильма в базе данных.
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getFilmById(filmFromArgs.id).collectLatest { film ->
                currentFilm = film
                film.let { setFilmsDetails(it) }
            }
        }

        // Обрабатываем нажатие на кнопку "Избранное".
        binding.favoriteButton.setOnClickListener {
            currentFilm?.let { film ->
                val wasInFavorites = film.isInFavorites
                viewModel.onFavoriteClicked(film)

                if (!wasInFavorites) {
                    binding.favoriteButton.setImageResource(R.drawable.baseline_favorite_24)
                    Snackbar.make(binding.root, "Добавлено в избранное", Snackbar.LENGTH_SHORT).show()
                } else {
                    binding.favoriteButton.setImageResource(R.drawable.baseline_favorite_border_24)
                    Snackbar.make(binding.root, "Удалено из избранного", Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        // Обрабатываем нажатие на кнопку "Посмотреть позже".
        binding.watchLaterButton.setOnClickListener {
            Snackbar.make(binding.root, "Добавлено в список 'Посмотреть позже'", Snackbar.LENGTH_SHORT).show()
        }

        // Обрабатываем нажатие на FAB для отправки фильма.
        binding.detailsFab.setOnClickListener {
            currentFilm?.let { film ->
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

        // Устанавливаем тип масштабирования для постера.
        binding.detailsPoster.apply {
            this.scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }

    // Метод для установки деталей фильма в UI.
    private fun setFilmsDetails(film: Film) {
        binding.apply {
            detailsToolbar.title = film.title

            // Загружаем постер фильма.
            film.poster?.let { posterPath ->
                try {
                    // Если постер - это ресурс, загружаем его как ресурс.
                    val resourceId = posterPath.toInt()
                    Glide.with(this@DetailsFragment)
                        .load(resourceId)
                        .error(R.drawable.no_poster)
                        .centerCrop()
                        .into(detailsPoster)
                } catch (_: NumberFormatException) {
                    // Если постер - это URL, загружаем его из сети.
                    val fullUrl = ApiConstants.IMAGES_URL + "w780/" + posterPath.removePrefix("/")
                    val thumbnailUrl = ApiConstants.IMAGES_URL + "w342/" + posterPath.removePrefix("/")
                    Glide.with(this@DetailsFragment)
                        .load(fullUrl)
                        .thumbnail(Glide.with(this@DetailsFragment).load(thumbnailUrl))
                        .error(R.drawable.no_poster)
                        .centerCrop()
                        .into(detailsPoster)
                }
            } ?: Glide.with(this@DetailsFragment) // Если постера нет, показываем заглушку.
                .load(R.drawable.no_poster)
                .centerCrop()
                .into(detailsPoster)

            detailsDescription.text = film.description

            // Устанавливаем иконку "Избранное" в зависимости от статуса фильма.
            favoriteButton.setImageResource(
                if (film.isInFavorites) R.drawable.baseline_favorite_24
                else R.drawable.baseline_favorite_border_24
            )

            // Устанавливаем прогресс рейтинга.
            val progress = (film.rating * 10).toInt().coerceIn(0, 100)
            ratingDonut.setProgress(progress)
        }
    }

    // Вызывается, когда иерархия представлений, связанная с фрагментом, удаляется.
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
