package com.example.androidappfilmproject.view.fragments

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.os.Environment
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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.androidappfilmproject.App
import com.example.androidappfilmproject.R
import com.example.androidappfilmproject.databinding.FragmentDetailsBinding
import com.example.androidappfilmproject.view.notifications.NotificationHelper
import com.example.androidappfilmproject.viewmodel.DetailsFragmentViewModel
import com.example.database_module.entity.Film
import com.example.remote_module.entity.ApiConstants
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

// Создаем класс DetailsFragment, который отвечает за отображение подробной информации о фильме.
class DetailsFragment : Fragment() {

    //Инициализируем ViewBinding
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private var currentFilm: Film? = null
    private val compositeDisposable = CompositeDisposable()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: DetailsFragmentViewModel by viewModels { viewModelFactory }

    // Вызывается при присоединении фрагмента к контексту.
    override fun onAttach(context: Context) {
        super.onAttach(context)
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

        val filmFromArgs = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("film", Film::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable<Film>("film")
        }

        if (filmFromArgs == null) {
            Snackbar.make(binding.root, R.string.error_film_not_found, Snackbar.LENGTH_SHORT).show()
            return
        }

        (activity as? AppCompatActivity)?.setSupportActionBar(binding.detailsToolbar)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        currentFilm = filmFromArgs
        setFilmsDetails(filmFromArgs)

        // Подписка на обновления из базы
        val disposable = viewModel.getFilmById(filmFromArgs.id)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ film ->
                currentFilm = film
                updateFavoriteIcon(film.isInFavorites)
            }, {
                it.printStackTrace()
            })
        compositeDisposable.add(disposable)

        // ЛОГИКА ИЗБРАННОГО
        binding.favoriteButton.setOnClickListener {
            currentFilm?.let { film ->
                film.isInFavorites = !film.isInFavorites
                updateFavoriteIcon(film.isInFavorites)
                val messageResId = if (film.isInFavorites) R.string.added_to_favorites else R.string.removed_from_favorites
                Snackbar.make(binding.root, messageResId, Snackbar.LENGTH_SHORT).show()
                viewModel.onFavoriteClicked(film)
            }
        }

        // КНОПКА "ПОСМОТРЕТЬ ПОЗЖЕ" (Нотификация)
        binding.watchLaterButton.setOnClickListener {
            currentFilm?.let { film ->
                NotificationHelper.notificationSet(requireContext(), film) {
                    Snackbar.make(binding.root, R.string.reminder_set, Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        // КНОПКА "ПОДЕЛИТЬСЯ"
        binding.detailsFab.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, "Check out this film: ${filmFromArgs.title}\n\n${filmFromArgs.description}")
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, "Share To:"))
        }

        binding.detailsFabDownloadWp.setOnClickListener {
            performAsyncLoadOfPoster()
        }

        binding.detailsPoster.apply {
            this.scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }

    // Метод для установки деталей фильма в UI.
    private fun setFilmsDetails(film: Film) {
        binding.apply {
            detailsToolbar.title = film.title

            film.poster?.let { posterPath ->
                // Если это число (ресурс), грузим как ресурс
                val resourceId = posterPath.toIntOrNull()
                if (resourceId != null) {
                    Glide.with(this@DetailsFragment).load(resourceId).centerCrop().into(detailsPoster)
                } else {
                    // Иначе грузим как URL, убирая лишние слэши
                    val cleanPath = posterPath.removePrefix("/")
                    val fullUrl = ApiConstants.IMAGES_URL + "w780/" + cleanPath
                    
                    Glide.with(this@DetailsFragment)
                        .load(fullUrl)
                        .timeout(30000) // Таймаут 30 секунд
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>,
                                isFirstResource: Boolean
                            ): Boolean {
                                // Если загрузка основного постера сорвалась
                                binding.progressBar.isVisible = false
                                Snackbar.make(binding.root, R.string.error_loading_check_internet, Snackbar.LENGTH_LONG).show()
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable,
                                model: Any,
                                target: Target<Drawable>?,
                                dataSource: DataSource,
                                isFirstResource: Boolean
                            ): Boolean {
                                binding.progressBar.isVisible = false
                                return false
                            }
                        })
                        .centerCrop()
                        .into(detailsPoster)
                }
            } ?: Glide.with(this@DetailsFragment).load(R.drawable.no_poster).centerCrop().into(detailsPoster)

            detailsDescription.text = film.description
            updateFavoriteIcon(film.isInFavorites)
            val progress = (film.rating * 10).toInt().coerceIn(0, 100)
            ratingDonut.setProgress(progress)
        }
    }

    // Метод отвечает за обновление иконки избранного в UI.
    private fun updateFavoriteIcon(isInFavorites: Boolean) {
        binding.favoriteButton.setImageResource(
            if (isInFavorites) R.drawable.baseline_favorite_24
            else R.drawable.baseline_favorite_border_24
        )
    }

    // Метод для для проверки разрешений на запись во память телефона
    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    // Метод для запрса разрешения на запись в память телефона
    private fun requestPermission() {
        requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            performAsyncLoadOfPoster()
        }
    }

    // Метод отвечающий за сохранение постера в галерею
    private fun saveToGallery(bitmap: Bitmap) {
        val film = currentFilm ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.TITLE, film.title)
                put(MediaStore.Images.Media.DISPLAY_NAME, "${film.title}.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/FilmsSearchApp")
            }
            val contentResolver = requireActivity().contentResolver
            val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                contentResolver.openOutputStream(it)?.use { os ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
                }
            }
        } else {
            // ЛОГИКА ДЛЯ API 28 И НИЖЕ
            val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "FilmsSearchApp")
            if (!directory.exists()) directory.mkdirs()

            val file = File(directory, "${film.title}.jpg")
            FileOutputStream(file).use { os ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
            }

            // Оповещаем систему о новом файле
            MediaScannerConnection.scanFile(requireContext(), arrayOf(file.toString()), null, null)
        }
    }


    // Метод отвечающий за загрузку постера в галерею
    private fun performAsyncLoadOfPoster() {
        val film = currentFilm ?: return
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && !checkPermission()) {
            requestPermission()
            return
        }

        binding.progressBar.isVisible = true
        val cleanPath = film.poster?.removePrefix("/") ?: ""
        val fullUrl = ApiConstants.IMAGES_URL + "w500/" + cleanPath

        val disposable = viewModel.loadWallpaper(fullUrl)
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { 
                // Этот блок сработает ВСЕГДА: и при успехе, и при ошибке
                binding.progressBar.isVisible = false 
            }
            .subscribe({ bitmap ->
                saveToGallery(bitmap)
                Snackbar.make(binding.root, R.string.downloaded_to_gallery, Snackbar.LENGTH_LONG)
                    .setAction(R.string.open) {
                        val intent = Intent().apply {
                            action = Intent.ACTION_VIEW
                            type = "image/*"
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        startActivity(intent)
                    }
                    .show()
            }, {
                // Всплывающее сообщение об ошибке
                Snackbar.make(binding.root, R.string.error_loading_check_internet, Snackbar.LENGTH_SHORT).show()
            })
        compositeDisposable.add(disposable)
    }

    // Метод вызывается, когда иерархия представлений, связанная с фрагментом, удаляется
    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
        _binding = null
    }
}
