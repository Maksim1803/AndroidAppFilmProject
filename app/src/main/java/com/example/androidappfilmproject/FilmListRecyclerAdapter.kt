package com.example.androidappfilmproject

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidappfilmproject.databinding.FilmItemBinding

// Вариант 4 с submitList() и ListAdapter (используется)

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

    interface OnItemClickListener {
        fun click(film: Film)
    }

    inner class FilmViewHolder(private val binding: FilmItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(film: Film) {

//            binding.title.text = film.title
//            binding.poster.setImageResource(film.poster)
//            binding.description.text = film.description

            // Для модуля 28:
            //Устанавливаем заголовок
            binding.title.text = film.title
            //Устанавливаем постер
            //Указываем контейнер, в котором будет "жить" наша картинка
            Glide.with(itemView)
                //Загружаем сам ресурс
                .load(film.poster)
                //Центруем изображение
                .centerCrop()
                //Указываем ImageView, куда будем загружать изображение
                .into(binding.poster)
            //Устанавливаем описание
            binding.description.text = film.description
        }
    }
}


// Вариант 3
//class FilmListRecyclerAdapter(
//
//    private val clickListener: OnItemClickListener
//
//) : ListAdapter<Film, FilmListRecyclerAdapter.FilmViewHolder>(FilmDiffCallback()) {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
//        val binding = FilmItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return FilmViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
//        val film = getItem(position)
//        holder.bind(film)
//        holder.itemView.setOnClickListener {
//            clickListener.click(film)
//        }
//    }
//
//    interface OnItemClickListener {
//        fun click(film: Film)
//    }
//
//    inner class FilmViewHolder(private val binding: FilmItemBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//
//        fun bind(film: Film) {
//            binding.title.text = film.title ?: "No Title" // Обрабатываем случай, когда title == null
//            binding.poster.setImageResource(film.poster) // Проверять poster на валидность сложнее (см. ниже)
//            binding.description.text = film.description ?: "No Description" // Обрабатываем случай, когда description == null
//        }
//    }
//  }
//



//Вариант 2 без diffutils

//class FilmListRecyclerAdapter(private val clickListener: OnItemClickListener) :
//    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//    //Здесь у нас хранится список элементов для RV
//    private val items = mutableListOf<Film>()
//
//    //Этот метод нужно переопределить на возврат количества элементов в списке RV
//    override fun getItemCount() = items.size
//
//    //В этом методе мы привязываем наш view holder и передаем туда "надутую" верстку нашего фильма
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        val binding = FilmItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return FilmViewHolder(binding)
//    }
//
//    //В этом методе будет привязка полей из объекта Film, к view из film_item.xml
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        //Проверяем какой у нас ViewHolder
//        when (holder) {
//            is FilmViewHolder -> {
//                //Вызываем метод bind(), который мы создали и передаем туда объект
//                //из нашей базы данных с указанием позиции
//                holder.bind(items[position])
//                //Обрабатываем нажатие на весь элемент целиком(можно сделать на отдельный элемент
//                //например, картинку) и вызываем метод нашего листенера, который мы получаем из
//                //конструктора адаптера
//                holder.itemView.setOnClickListener { // Убираем .item_container, так как itemView
//                    // теперь это root binding
//                    clickListener.click(items[position])
//                }
//            }
//        }
//    }
//
//    //Метод для добавления объектов в наш список.
//    fun addItems(list: List<Film>) {
//        // Вариант без DiffUtils. Сначала очишаем(если не реализовать DiffUtils)
//        items.clear()
//        //Добавляем
//        items.addAll(list)
//        //Уведомляем RV, что пришел новый список и ему нужно заново все "привязывать"
//        notifyDataSetChanged()
//
//    }
//
//
//    //Интерфейс для обработки кликов
//    interface OnItemClickListener {
//        fun click(film: Film)
//    }
//}


// Вариант 1 с RecyclerView.Adapter

//class Film ListRecyclerAdapter(
//    private val clickListener: OnItemClickListener
//) : RecyclerView.Adapter<FilmListRecyclerAdapter.FilmViewHolder>() {
//
//    private val items = mutableListOf<Film>()
//
//    override fun getItemCount() = items.size
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
//        val binding = FilmItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return FilmViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
//        val film = items[position]
//        holder.bind(film)
//        holder.itemView.setOnClickListener {
//            clickListener.click(film)
//        }
//    }
//
//    fun setItems(newItems: List<Film>) {
//        items.clear()
//        items.addAll(newItems)
//        notifyDataSetChanged()
//    }
//
//    inner class FilmViewHolder(private val binding: FilmItemBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//
//        fun bind(film: Film) {
//            binding.title.text = film.title
//            binding.poster.setImageResource(film.poster)
//            binding.description.text = film.description
//        }
//    }
//
//    interface OnItemClickListener {
//        fun click(film: Film)
//    }
//}

