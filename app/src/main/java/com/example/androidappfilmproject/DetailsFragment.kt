package com.example.androidappfilmproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.androidappfilmproject.databinding.FragmentDetailsBinding
import com.google.android.material.snackbar.Snackbar

class DetailsFragment : Fragment() {

    private var binding: FragmentDetailsBinding? = null// Объявление переменной для View Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Устанавливаем Toolbar
        (activity as? AppCompatActivity)?.setSupportActionBar(binding?.detailsToolbar)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Обработка кнопок
        binding?.favoriteButton?.setOnClickListener {
            Snackbar.make(binding!!.root, "Добавлено в избранное", Snackbar.LENGTH_SHORT).show()
        }

        binding?.watchLaterButton?.setOnClickListener {
            Snackbar.make(binding!!.root, "Добавлено в список 'Посмотреть позже'", Snackbar.LENGTH_SHORT).show()
        }

        binding?.detailsFab?.setOnClickListener {
            Snackbar.make(binding!!.root, "Функция 'Поделиться' еще не реализована", Snackbar.LENGTH_SHORT).show()
        }

        // Установка деталей фильма
        setFilmsDetails()

        // Доступ к ImageView через View Binding
        val imageView = binding?.detailsPoster

        // Изменяем scaleType динамически
        if (imageView != null) {
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }

    private fun setFilmsDetails() {
        val args = arguments
        val film = args?.getParcelable<Film>("film")
        if (film != null) {
            binding?.apply {
                detailsToolbar.title = film.title
                detailsPoster.setImageResource(film.poster)
                detailsDescription.text = film.description
            }
        } else {
            // Обработка ошибки, если фильм не передан
            // Например, закрыть фрагмент или показать сообщение
            activity?.finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}


