package com.example.androidappfilmproject.view.fragments

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.androidappfilmproject.App
import com.example.androidappfilmproject.R
import com.example.androidappfilmproject.data.ApiConstants
import com.example.androidappfilmproject.data.entity.Film
import com.example.androidappfilmproject.databinding.FragmentDetailsBinding
import com.example.androidappfilmproject.viewmodel.DetailsFragmentViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
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
                if (film != null) { // Проверяем, что фильм не null
                    currentFilm = film
                    setFilmsDetails(film)
                }
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
            Snackbar.make(binding.root, "Добавлено в список \'Посмотреть позже\'", Snackbar.LENGTH_SHORT).show()
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

        // Кнопка скачивания постера
        binding.detailsFabDownloadWp.setOnClickListener {
            performAsyncLoadOfPoster()
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

    // Узнаем, было ли получено разрешение ранее
    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

    // Запрашиваем разрешение
    private fun requestPermission() {
        requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
    }

    // Слушаем ответ на запрос разрешений
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                performAsyncLoadOfPoster()
            } else {
                Snackbar.make(binding.root, "Разрешение отклонено", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    // Логика сохранения в галерею
    private fun saveToGallery(bitmap: Bitmap) {
        val film = currentFilm ?: return
        // Проверяем версию системы
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Создаем объект для передачи данных
            val contentValues = ContentValues().apply {
                // Составляем информацию для файла (имя, тип, дата создания, куда сохранять и т.д.)
                put(MediaStore.Images.Media.TITLE, film.title.handleSingleQuote())
                put(
                    MediaStore.Images.Media.DISPLAY_NAME,
                    "${film.title.handleSingleQuote()}.jpg"
                )
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(
                    MediaStore.Images.Media.DATE_ADDED,
                    System.currentTimeMillis() / 1000
                )
                put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/FilmsSearchApp")
            }
            // Получаем ссылку на объект Content resolver, который помогает передавать
            // информацию из приложения вовне
            val contentResolver = requireActivity().contentResolver
            val uri = contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            // Открываем канал для записи на диск
            val outputStream = uri?.let { contentResolver.openOutputStream(it) }
            // Передаем нашу картинку, может сделать компрессию
            outputStream?.use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }
        } else {
            // То же, но для более старых версий ОС
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.insertImage(
                requireActivity().contentResolver,
                bitmap,
                film.title.handleSingleQuote(),
                film.description.handleSingleQuote()
            )
        }
    }

    // Кастомный метод для того, чтобы убирать кавычки.
    private fun String.handleSingleQuote(): String {
        return this.replace("'", "")
    }

    // Метод, который вызывается при нажатии на кнопку. 
    private fun performAsyncLoadOfPoster() {
        val film = currentFilm ?: return
        // Проверяем есть ли разрешение (нужно для версий < Q)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && !checkPermission()) {
            // Если нет, то запрашиваем и выходим из метода через стандартное окно
            requestPermission()
            return
        }
        
        // Используем жизненный цикл фрагмента для запуска корутины
        viewLifecycleOwner.lifecycleScope.launch {
            // Включаем Прогресс-бар
            binding.progressBar.isVisible = true
            
            // Загружаем картинку
            val posterPath = film.poster?.removePrefix("/")
            // Используем "w185" (очень низкое качество) вместо "w500" или "original",
            // чтобы файл загрузился даже при очень плохом интернете.
            val fullUrl = ApiConstants.IMAGES_URL + "w500/" + posterPath
            val bitmap = viewModel.loadWallpaper(fullUrl)
            
            // Отключаем Прогресс-бар
            binding.progressBar.isVisible = false

            if (bitmap != null) {
                // Сохраняем в галерею
                saveToGallery(bitmap)
                // Выводим снекбар
                Snackbar.make(
                    binding.root,
                    "Сохранено в Галерею",
                    Snackbar.LENGTH_LONG
                )
                    .setAction("В ГАЛЕРЕЮ") {
                        val intent = Intent()
                        intent.action = Intent.ACTION_VIEW
                        intent.type = "image/*"
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }
                    .show()
            } else {
                // Обработка ошибки
                Snackbar.make(
                    binding.root,
                    "Ошибка при загрузке изображения",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    // Вызывается, когда иерархия представлений, связанная с фрагментом, удаляется.
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
