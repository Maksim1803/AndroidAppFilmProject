package com.example.androidappfilmproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FavoritesFragment : Fragment() {

        private lateinit var filmsAdapter: FilmListRecyclerAdapter
        private lateinit var favoritesRecycler: RecyclerView // Объявляем переменную для RecyclerView

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_favorites, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            // Инициализируем RecyclerView
            favoritesRecycler = view.findViewById(R.id.favorites_recycler)

            filmsAdapter = FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
                override fun click(film: Film) {
                    (requireActivity() as MainActivity).launchDetailsFragment(film)
                }
            })

            //Настраиваем RecyclerView
            favoritesRecycler.apply {
                //Присваиваем адаптер
                adapter = filmsAdapter
                //Присвоим layoutmanager
                layoutManager = LinearLayoutManager(requireContext())
                //Применяем декоратор для отступов
                val decorator = TopSpacingItemDecoration(8)
                addItemDecoration(decorator)
            }

            // Получаем список фильмов из базы данных.  Важно:  Вместо emptyList() нужно получить реальные данные!
            val favoritesList: List<Film> = getFavoritesList() // Замените эту строку на реальный код для получения данных из БД.  Пример:  val favoritesList = App.instance.db.filmDao().getAll()

            //Кладем нашу БД в RV
            filmsAdapter.addItems(favoritesList)
        }

        // **ВАЖНО:** Замените эту функцию реальным кодом для получения данных из базы данных!
        private fun getFavoritesList(): List<Film> {
            //  Здесь должен быть код, который получает список фильмов из вашей базы данных.
            //  Например, если вы используете Room, это может выглядеть так:
            //  return App.instance.db.filmDao().getFavoriteFilms()
            //  А пока возвращаем пустой список, чтобы код компилировался.
            return emptyList()
        }
    }
