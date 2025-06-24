package com.example.androidappfilmproject

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.androidappfilmproject.databinding.FragmentDetailsBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class DetailsFragment : Fragment() {

    private var binding: FragmentDetailsBinding? = null
    private var film: Film? = null  // Объявляем переменную для фильма

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получение фильма из аргументов
        val args = arguments
        film = args?.getParcelable<Film>("film")
        if (film == null) {
            // Обработка ошибки: фильм не передан
            Snackbar.make(view, "Ошибка: Фильм не найден", Snackbar.LENGTH_SHORT).show()
            activity?.finish()
            return
        }

        // Устанавливаем Toolbar
        (activity as? AppCompatActivity)?.setSupportActionBar(binding?.detailsToolbar)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)


        // Обновляем иконку
        setFilmsDetails()
        // Обработка кнопки "добавить в избранное"
        binding?.favoriteButton?.setOnClickListener {
            film?.let {
                if (!it.isInFavorites) {
                    // Добавляем в избранное
                    it.isInFavorites = true
                    binding!!.favoriteButton.setImageResource(R.drawable.baseline_favorite_24)
                    Snackbar.make(binding!!.root, "Добавлено в избранное", Snackbar.LENGTH_SHORT).show()
                } else {
                    // Удаляем из избранного
                    it.isInFavorites = false
                    binding!!.favoriteButton.setImageResource(R.drawable.baseline_favorite_border_24)
                    Snackbar.make(binding!!.root, "Удалено из избранного", Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        // Обработка кнопки "посмотреть позже"
        binding?.watchLaterButton?.setOnClickListener {
            Snackbar.make(binding!!.root, "Добавлено в список 'Посмотреть позже'", Snackbar.LENGTH_SHORT).show()
        }

        binding?.detailsFab?.setOnClickListener {
            Snackbar.make(binding!!.root, "Функция 'Поделиться' уже реализована", Snackbar.LENGTH_SHORT).show()
        }

        // Доступ к ImageView через View Binding и изменение scaleType
        binding?.detailsPoster?.apply {
            this.scaleType = ImageView.ScaleType.CENTER_CROP
        }


        // Обработка кнопки "поделиться"
        binding?.detailsFab?.setOnClickListener {
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

    private fun setFilmsDetails() {
        viewLifecycleOwner.lifecycleScope.launch {

        binding?.apply {
            film?.let {
                detailsToolbar.title = it.title
                detailsPoster.setImageResource(it.poster)
                detailsDescription.text = it.description

                // Устанавливаем иконку в зависимости от статуса
                favoriteButton.setImageResource(
                    if (it.isInFavorites) R.drawable.baseline_favorite_24
                    else R.drawable.baseline_favorite_border_24
                )
            }
        }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}

//
//   private var binding: FragmentDetailsBinding? = null// Объявление переменной для View Binding
//
//   override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = FragmentDetailsBinding.inflate(inflater, container, false)
//        return binding!!.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // Устанавливаем Toolbar
//        (activity as? AppCompatActivity)?.setSupportActionBar(binding?.detailsToolbar)
//        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
//
//        // Обработка кнопок
//        binding?.favoriteButton?.setOnClickListener {
//            Snackbar.make(binding!!.root, "Добавлено в избранное", Snackbar.LENGTH_SHORT).show()
//        }
//
//        binding?.watchLaterButton?.setOnClickListener {
//            Snackbar.make(binding!!.root, "Добавлено в список 'Посмотреть позже'", Snackbar.LENGTH_SHORT).show()
//        }
//
//        binding?.detailsFab?.setOnClickListener {
//            Snackbar.make(binding!!.root, "Функция 'Поделиться' еще не реализована", Snackbar.LENGTH_SHORT).show()
//        }
//
//        // Установка деталей фильма
//        setFilmsDetails()
//
//        // Доступ к ImageView через View Binding
//        val imageView = binding?.detailsPoster
//
//        // Изменяем scaleType динамически (вариант масштабирования картинки в коде)
//        if (imageView != null) {
//            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
//        }
//
//        details_fab_favorites.setOnClickListener {
//            if (!film.isInFavorites) {
//                details_fab_favorites.setImageResource(R.drawable.baseline_favorite_24)
//                film.isInFavorites = true
//            } else {
//                details_fab_favorites.setImageResource(R.drawable.baseline_favorite_border_24)
//                film.isInFavorites = false
//            }
//        }
//
//        details_fab_share.setOnClickListener {
//            //Создаем интент
//            val intent = Intent()
//            //Укзываем action с которым он запускается
//            intent.action = Intent.ACTION_SEND
//            //Кладем данные о нашем фильме
//            intent.putExtra(
//                Intent.EXTRA_TEXT,
//                "Check out this film: ${film.title} \n\n ${film.description}"
//            )
//            //Указываем MIME тип, чтобы система знала, какое приложение предложить
//            intent.type = "text/plain"
//            //Запускаем наше активити
//            startActivity(Intent.createChooser(intent, "Share To:"))
//        }
//    }
//
//
//    private fun setFilmsDetails() {
//        val args = arguments
//        val film = args?.getParcelable<Film>("film")
//        if (film != null) {
//            binding?.apply {
//                detailsToolbar.title = film.title
//                detailsPoster.setImageResource(film.poster)
//                detailsDescription.text = film.description
//            }
//        } else {
//            // Обработка ошибки, если фильм не передан
//            // Например, закрыть фрагмент или показать сообщение
//            activity?.finish()
//        }
//
//        //Получаем наш фильм из переданного бандла
//        film = arguments?.get("film") as Film
//
//        //Устанавливаем заголовок
//        details_toolbar.title = film.title
//        //Устанавливаем картинку
//        details_poster.setImageResource(film.poster)
//        //Устанавливаем описание
//        details_description.text = film.description
//
//        details_fab_favorites.setImageResource(
//            if (film.isInFavorites) R.drawable.ic_baseline_favorite_24
//            else R.drawable.ic_baseline_favorite_border_24
//        )
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        binding = null
//    }
//}


