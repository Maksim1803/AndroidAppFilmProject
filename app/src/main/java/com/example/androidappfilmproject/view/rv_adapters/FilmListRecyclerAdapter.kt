package com.example.androidappfilmproject.view.rv_adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidappfilmproject.data.ApiConstants
import com.example.androidappfilmproject.databinding.FilmItemBinding
import com.example.androidappfilmproject.domain.Film

// Создаем класс FilmListRecyclerAdapter, который является адаптером для
// RecyclerView, использующим Paging 3 (пагинацию) для отображения фильмов.
class FilmListRecyclerAdapter(
    // Слушатель для обработки кликов по элементам списка.
    private val clickListener: OnItemClickListener
) : PagingDataAdapter<Film, FilmListRecyclerAdapter.ViewHolder>(DiffCallback()) {

    // Метод для создания нового ViewHolder.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // "Надуваем" макет для элемента списка.
        val binding = FilmItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // Метод для привязки данных к ViewHolder.
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Если элемент не null, привязываем данные.
        getItem(position)?.let { holder.bind(it) }
    }

    // ViewHolder, который содержит представление элемента списка.
    inner class ViewHolder(private val binding: FilmItemBinding) : RecyclerView.ViewHolder(binding.root) {
        // Метод для привязки данных фильма к представлениям в ViewHolder.
        fun bind(film: Film) {
            // Устанавливаем заголовок фильма.
            binding.title.text = film.title

            // Пытаемся загрузить постер как ресурс, если не получается - как URL.
            try {
                val resourceId = film.poster.toInt()
                Glide.with(itemView)
                    .load(resourceId)
                    .centerCrop()
                    .into(binding.poster)
            } catch (_: NumberFormatException) {
                // Если не получилось как ресурс, загружаем по URL.
                Glide.with(itemView)
                    .load(ApiConstants.IMAGES_URL + "w342" + film.poster)
                    .centerCrop()
                    .into(binding.poster)
            }

            // Устанавливаем описание фильма.
            binding.description.text = film.description
            // Анимируем кольцевой индикатор рейтинга.
            binding.ratingDonut.setProgressAnimated((film.rating * 10).toInt())

            // Скрываем иконку "избранное", так как добавление в избранное
            // теперь происходит только на экране с деталями.
            binding.favoriteIcon.visibility = View.GONE

            // Устанавливаем слушатель клика для всего элемента.
            binding.root.setOnClickListener {
                clickListener.click(film)
            }
        }
    }

    // Интерфейс для обработки кликов по элементам.
    interface OnItemClickListener {
        fun click(film: Film)
    }

    // DiffUtil.ItemCallback для эффективного сравнения элементов списка.
    class DiffCallback : DiffUtil.ItemCallback<Film>() {
        // Метод для проверки, являются ли два элемента одним и тем же.
        override fun areItemsTheSame(oldItem: Film, newItem: Film): Boolean {
            return oldItem.id == newItem.id
        }

        // Метод для проверки, изменилось ли содержимое элемента.
        override fun areContentsTheSame(oldItem: Film, newItem: Film): Boolean {
            return oldItem == newItem
        }
    }
}
