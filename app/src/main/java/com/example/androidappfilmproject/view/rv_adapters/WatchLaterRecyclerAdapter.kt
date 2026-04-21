package com.example.androidappfilmproject.view.rv_adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidappfilmproject.R
import com.example.androidappfilmproject.databinding.FilmItemBinding
import com.example.database_module.entity.Film
import com.example.remote_module.entity.ApiConstants
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Класс адаптера для RecyclerView, отображающего фильмы для экрана "Посмотреть позже"
class WatchLaterRecyclerAdapter(private val clickListener: OnItemClickListener) :
    ListAdapter<Film, WatchLaterRecyclerAdapter.ViewHolder>(FilmDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FilmItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val film = getItem(position)
        holder.bind(film)
        holder.binding.itemContainer.setOnClickListener {
            clickListener.click(film)
        }
        holder.binding.favoriteIcon.setOnClickListener {
            clickListener.removeClick(film)
        }
    }

    // Класс ViewHolder для каждого элемента списка
    class ViewHolder(val binding: FilmItemBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(film: Film) {
            binding.title.text = film.title
            
            val date = Date(film.watchLaterTime)
            val format = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            val context = binding.root.context
            binding.description.text = context.getString(R.string.watch_later_remind, format.format(date))

            // Используем иконку "Избранное", так как delete нет в проекте, 
            // либо можно использовать baseline_watch_later_24 для индикации отмены
            binding.favoriteIcon.setImageResource(R.drawable.baseline_watch_later_24)

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

    interface OnItemClickListener {
        fun click(film: Film)
        fun removeClick(film: Film)
    }

    // Класс FilmDiffCallback для сравнения элементов списка
    class FilmDiffCallback : DiffUtil.ItemCallback<Film>() {
        override fun areItemsTheSame(oldItem: Film, newItem: Film): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Film, newItem: Film): Boolean = oldItem == newItem
    }
}
