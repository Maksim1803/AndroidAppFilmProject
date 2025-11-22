package com.example.androidappfilmproject

import androidx.recyclerview.widget.DiffUtil
import com.example.androidappfilmproject.domain.Film


// Создаем класс FilmDiffCallback, который помогает RecyclerView определить, какие элементы в списке изменились.
class FilmDiffCallback : DiffUtil.ItemCallback<Film>() {

    // Метод для проверки, являются ли два элемента одним и тем же (в данном случае, по названию).
    override fun areItemsTheSame(oldItem: Film, newItem: Film): Boolean {
        // Сравниваем по названию
        return oldItem.title == newItem.title
    }

    // Метод для проверки, изменилось ли содержимое элемента.
    override fun areContentsTheSame(oldItem: Film, newItem: Film): Boolean {
        // Сравниваем содержимое объектов
        return oldItem == newItem
    }
}
