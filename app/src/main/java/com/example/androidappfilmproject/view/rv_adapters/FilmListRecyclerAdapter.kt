package com.example.androidappfilmproject.view.rv_adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidappfilmproject.FilmDiffCallback
import com.example.androidappfilmproject.databinding.FilmItemBinding
import com.example.androidappfilmproject.domain.Film

class FilmListRecyclerAdapter(
    private val clickListener: OnItemClickListener
) : ListAdapter<Film, FilmListRecyclerAdapter.FilmViewHolder>(FilmDiffCallback()) {

    // Отслеживаем, какие фильмы уже анимировались (по уникальному ключу, например title или id)
    private val animatedKeys = mutableSetOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        val binding = FilmItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FilmViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        val film = getItem(position)
        holder.bind(film)
        holder.itemView.setOnClickListener {
            clickListener.click(film)
        }
    }

    interface OnItemClickListener {
        fun click(film: Film)
    }

    inner class FilmViewHolder(private val binding: FilmItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(film: Film) {
            // Очистка старого постера
            Glide.with(itemView).clear(binding.poster)
            binding.poster.setImageDrawable(null)

            binding.title.text = film.title
            Glide.with(itemView)
                .load(film.poster)
                .centerCrop()
                .into(binding.poster)
            binding.description.text = film.description

            val key = film.title // или film.id.toString(), если id уникален
            val progress = (film.rating * 10).toInt().coerceIn(0, 100)

            if (!animatedKeys.contains(key)) {
                // первый раз для этого фильма — анимируем
                binding.ratingDonut.setProgressAnimated(progress, 2000L)
                animatedKeys.add(key)
            } else {
                // после анимации — ставим статично, чтобы при скролле не дергало
                binding.ratingDonut.setProgress(progress)
            }
        }
    }
}