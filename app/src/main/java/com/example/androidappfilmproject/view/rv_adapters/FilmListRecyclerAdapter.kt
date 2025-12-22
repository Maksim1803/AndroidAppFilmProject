package com.example.androidappfilmproject.view.rv_adapters

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidappfilmproject.R
import com.example.androidappfilmproject.data.ApiConstants
import com.example.androidappfilmproject.databinding.FilmItemBinding
import com.example.androidappfilmproject.domain.Film

// Создаем класс FilmListRecyclerAdapter, который является адаптером для RecyclerView
// работает с PagingData.
class FilmListRecyclerAdapter(private val clickListener: OnItemClickListener) :
    PagingDataAdapter<Film, FilmListRecyclerAdapter.FilmViewHolder>(FilmDiffCallback()) {

    // Метод для создания нового ViewHolder, который будет содержать View-элемент списка.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        // Инфлейтим макет film_item.xml для каждого элемента списка.
        val binding = FilmItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        // Возвращаем новый экземпляр FilmViewHolder.
        return FilmViewHolder(binding)
    }

    // Метод для привязки данных к ViewHolder
    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        // Получаем фильм по позиции.
        getItem(position)?.let {
            // Вызываем метод bind у ViewHolder, чтобы установить данные.
            holder.bind(it, clickListener)
            // Устанавливаем слушатель клика на сам элемент списка.
            holder.itemView.setOnClickListener { _ ->
                clickListener.click(it)
            }
        }
    }

    // ViewHolder для элемента списка.
    class FilmViewHolder(private val binding: FilmItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        // Метод для установки данных фильма в View-элементы.
        fun bind(film: Film, clickListener: OnItemClickListener) {
            // Устанавливаем название фильма.
            binding.title.text = film.title

            // Устанавливаем слушатель клика на иконку "избранное".
            binding.favoriteIcon.setOnClickListener {
                clickListener.onFavoriteClick(film)
            }

            // Устанавливаем иконку в зависимости от того,
            // находится ли фильм в избранном
            if (film.isInFavorites) {
                binding.favoriteIcon.setImageResource(R.drawable.baseline_favorite_24)
                binding.favoriteIcon.visibility = View.VISIBLE
            } else {
                binding.favoriteIcon.visibility = View.GONE
            }

            // Загружаем постер фильма.
            film.poster?.let { posterPath ->
                try {
                    // Пытаемся загрузить из ресурсов, если это локальный фильм.
                    val resourceId = posterPath.toInt()
                    Glide.with(itemView)
                        .load(resourceId)
                        .error(R.drawable.no_poster)
                        .centerCrop()
                        .into(binding.poster)
                } catch (_: NumberFormatException) {
                    // Загружаем из сети, если это фильм из API.
                    Glide.with(itemView)
                        .load(ApiConstants.IMAGES_URL + "w342" + posterPath)
                        .error(R.drawable.no_poster)
                        .centerCrop()
                        .into(binding.poster)
                }
            } ?: Glide.with(itemView) // Если постера нет, ставим заглушку.
                .load(R.drawable.no_poster)
                .centerCrop()
                .into(binding.poster)

            // Устанавливаем описание фильма.
            binding.description.text = film.description
            // Устанавливаем прогресс для кольцевого индикатора рейтинга.
            val progress = (film.rating * 10).toInt().coerceIn(0, 100)
            binding.ratingDonut.setProgress(progress)
        }
    }

    // Интерфейс для обработки кликов по элементам списка.
    interface OnItemClickListener {
        // Метод для обработки клика по элементу списка.
        fun click(film: Film)
        // Метод для обработки клика по иконке "избранное".
        fun onFavoriteClick(film: Film)
    }

    // Класс для вычисления различий между двумя списками.
    class FilmDiffCallback : DiffUtil.ItemCallback<Film>() {
        // Проверяет, являются ли два элемента одним и тем же.
        override fun areItemsTheSame(oldItem: Film, newItem: Film): Boolean {
            return oldItem.id == newItem.id
        }

        // Проверяет, изменилось ли что-то, включая состояние "избранное"
        override fun areContentsTheSame(oldItem: Film, newItem: Film): Boolean {

            Log.d(TAG, "ID: ${oldItem.id} | Old Fav: ${oldItem.isInFavorites} | New Fav: ${newItem.isInFavorites}")
            return oldItem == newItem
        }
    }
}
