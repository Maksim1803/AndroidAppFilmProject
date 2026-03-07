package com.example.androidappfilmproject.view.rv_adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidappfilmproject.R
import com.example.androidappfilmproject.databinding.FilmItemBinding
import com.example.database_module.entity.Film
import com.example.remote_module.entity.ApiConstants

// Создаем класс FilmListRecyclerAdapter, который является адаптером для RecyclerView
// работает с PagingData.
class FilmListRecyclerAdapter(private val clickListener: OnItemClickListener) :
    PagingDataAdapter<Film, FilmListRecyclerAdapter.FilmViewHolder>(FilmDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        val binding = FilmItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FilmViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        getItem(position)?.let { film ->
            holder.bind(film)
            
            holder.binding.itemContainer.setOnClickListener {
                clickListener.click(film)
            }
            
            holder.binding.favoriteIcon.setOnClickListener {
                // Передаем и саму иконку, чтобы фрагмент мог её поменять
                clickListener.onFavoriteClick(film, it as ImageView)
            }

            holder.binding.itemContainer.setOnLongClickListener {
                clickListener.longClick(film)
                true
            }
        }
    }

    // ViewHolder для представления элемента списка
    class FilmViewHolder(val binding: FilmItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Метод для привязки данных к элементу списка
        fun bind(film: Film) {
            binding.title.text = film.title
            binding.description.text = film.description
            
            binding.favoriteIcon.visibility = View.VISIBLE
            binding.favoriteIcon.setImageResource(
                if (film.isInFavorites) R.drawable.baseline_favorite_24
                else R.drawable.baseline_favorite_border_24
            )

            film.poster?.let { posterPath ->
                try {
                    val resourceId = posterPath.toInt()
                    Glide.with(itemView).load(resourceId).centerCrop().into(binding.poster)
                } catch (_: NumberFormatException) {
                    val fullUrl = ApiConstants.IMAGES_URL + "w342" + posterPath
                    Glide.with(itemView).load(fullUrl).centerCrop().into(binding.poster)
                }
            } ?: Glide.with(itemView).load(R.drawable.no_poster).centerCrop().into(binding.poster)

            val progress = (film.rating * 10).toInt().coerceIn(0, 100)
            binding.ratingDonut.setProgress(progress)
        }
    }

    // Интерфейс для обработки кликов
    interface OnItemClickListener {
        fun click(film: Film)
        // Теперь принимаем иконку
        fun onFavoriteClick(film: Film, favoriteIcon: ImageView)
        fun longClick(film: Film)
    }

    // Класс для сравнения элементов списка
    class FilmDiffCallback : DiffUtil.ItemCallback<Film>() {
        override fun areItemsTheSame(oldItem: Film, newItem: Film): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Film, newItem: Film): Boolean = oldItem == newItem
    }
}
