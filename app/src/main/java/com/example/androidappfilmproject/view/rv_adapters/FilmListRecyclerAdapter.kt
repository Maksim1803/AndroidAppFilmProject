package com.example.androidappfilmproject.view.rv_adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidappfilmproject.FilmDiffCallback
import com.example.androidappfilmproject.data.ApiConstants
import com.example.androidappfilmproject.databinding.FilmItemBinding
import com.example.androidappfilmproject.domain.Film

// Создаем класс FilmListRecyclerAdapter, который является адаптером для
// RecyclerView, использующим Paging 3 (пагинацию) для отображения фильмов.
class FilmListRecyclerAdapter(
    // Слушатель для обработки кликов по элементам списка.
    private val clickListener: OnItemClickListener
) : PagingDataAdapter<Film, FilmListRecyclerAdapter.FilmViewHolder>(FilmDiffCallback()) {

    // Множество для отслеживания ключей элементов,
    // для которых уже была показана анимация.
    private val animatedKeys = mutableSetOf<String>()

    // Метод для создания нового ViewHolder.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        // "Надуваем" макет для элемента списка.
        val binding = FilmItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FilmViewHolder(binding)
    }

    // Метод для привязки данных к ViewHolder.
    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        // Получаем фильм в текущей позиции.
        val film = getItem(position)
        if (film != null) {
            // Привязываем данные фильма к ViewHolder.
            holder.bind(film)
            // Устанавливаем слушатель клика для элемента.
            holder.itemView.setOnClickListener {
                clickListener.click(film)
            }
        }
    }

    // Интерфейс для обработки кликов по элементам.
    interface OnItemClickListener {
        fun click(film: Film)
    }

    // ViewHolder, который содержит представление элемента списка.
    inner class FilmViewHolder(val binding: FilmItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Метод для привязки данных фильма к представлениям в ViewHolder.
        fun bind(film: Film) {
            // Очищаем предыдущее изображение из Glide.
            Glide.with(itemView).clear(binding.poster)
            // Устанавливаем изображение постера в null, чтобы избежать отображения старых изображений во время загрузки.
            binding.poster.setImageDrawable(null)

            // Устанавливаем заголовок фильма.
            binding.title.text = film.title
            // Загружаем постер фильма с помощью Glide.
            Glide.with(itemView)
            // Подключаем фильмы из БД на https://www.themoviedb.org
                .load(ApiConstants.IMAGES_URL + "w342" + film.poster)
                .centerCrop()
                .into(binding.poster)

            // Устанавливаем описание фильма.
            binding.description.text = film.description

            // Используем заголовок фильма как ключ для отслеживания анимации.
            val key = film.title
            // Рассчитываем прогресс рейтинга (0-100).
            val progress = (film.rating * 10).toInt().coerceIn(0, 100)

            // Анимируем кольцевой индикатор рейтинга только один раз.
            if (!animatedKeys.contains(key)) {
                binding.ratingDonut.setProgressAnimated(progress, 2000L)
                animatedKeys.add(key)
            } else {
                // Если анимация уже была показана, просто устанавливаем прогресс без анимации.
                binding.ratingDonut.setProgress(progress)
            }
        }
    }
}
