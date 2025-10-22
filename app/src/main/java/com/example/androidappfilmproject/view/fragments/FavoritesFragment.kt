package com.example.androidappfilmproject.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidappfilmproject.utils.AnimationHelper
import com.example.androidappfilmproject.App
import com.example.androidappfilmproject.view.rv_adapters.FilmListRecyclerAdapter
import com.example.androidappfilmproject.MainActivity
import com.example.androidappfilmproject.view.rv_adapters.TopSpacingItemDecoration
import com.example.androidappfilmproject.databinding.FragmentFavoritesBinding
import com.example.androidappfilmproject.domain.Film

// Класс фрагмента для отображения списка избранных фильмов вариант 2 (используется)
class FavoritesFragment : Fragment() {

    // View Binding для доступа к элементам UI
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    // Адаптер для RecyclerView
    private lateinit var filmsAdapter: FilmListRecyclerAdapter

    // Создание View фрагмента
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Инициализация View Binding
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        // Возвращаем корневой View макета
        return binding.root
    }

    // Когда View создана и готова к использованию
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Запускаем анимацию модуля 29
        AnimationHelper.Companion.performFragmentCircularRevealAnimation(binding.root, requireActivity(), 2)

        // Инициализация адаптера RecyclerView
        filmsAdapter =
            FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
                // Обработка клика по элементу списка
                override fun click(film: Film) {
                    // Запуск DetailsFragment из MainActivity
                    (requireActivity() as MainActivity).launchDetailsFragment(film)
                }
            })

        // Настройка RecyclerView
        binding.favoritesRecycler.apply {
            // Установка адаптера
            adapter = filmsAdapter
            // Установка LinearLayoutManager
            layoutManager = LinearLayoutManager(requireContext())

            // Добавление декоратора для отступов между элементами
            val decorator = TopSpacingItemDecoration(8)
            addItemDecoration(decorator)
        }

        // Загрузка списка избранных фильмов
        getFavoritesList()
    }

    // Функция для получения списка избранных фильмов из базы данных
    private fun getFavoritesList() {
        filmsAdapter.submitList(App.Companion.favoriteFilms.toList())
    }

    // Уничтожение View фрагмента (освобождение ресурсов)
    override fun onDestroyView() {
        super.onDestroyView()
        // Обнуление binding для предотвращения утечек памяти
        _binding = null
    }
}