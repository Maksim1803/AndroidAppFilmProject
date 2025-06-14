package com.example.androidappfilmproject

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter

class FilmListRecyclerAdapter(
    private val clickListener: OnItemClickListener
) : ListAdapter<Film, FilmListRecyclerAdapter.FilmViewHolder>(FilmDiffCallback()) {

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

    inner class FilmViewHolder(private val binding: FilmItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(film: Film) {
            binding.title.text = film.title
            binding.poster.setImageResource(film.poster)
            binding.description.text = film.description
        }
    }

    interface OnItemClickListener {
        fun click(film: Film)
    }
}
