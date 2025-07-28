package com.example.androidappfilmproject

import androidx.recyclerview.widget.DiffUtil


class FilmDiffCallback : DiffUtil.ItemCallback<Film>() {

    override fun areItemsTheSame(oldItem: Film, newItem: Film): Boolean {
        // Сравниваем по названию
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: Film, newItem: Film): Boolean {
        // Сравниваем содержимое объектов
        return oldItem == newItem
    }
}
