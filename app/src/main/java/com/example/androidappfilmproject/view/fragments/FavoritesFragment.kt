package com.example.androidappfilmproject.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidappfilmproject.App
import com.example.androidappfilmproject.MainActivity
import com.example.androidappfilmproject.databinding.FragmentFavoritesBinding
import com.example.androidappfilmproject.domain.Film
import com.example.androidappfilmproject.utils.AnimationHelper
import com.example.androidappfilmproject.view.rv_adapters.FilmListRecyclerAdapter
import com.example.androidappfilmproject.view.rv_adapters.TopSpacingItemDecoration
import com.example.androidappfilmproject.viewmodel.FavoritesFragmentViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// Создаем класс FavoritesFragment, который отвечает за отображение списка избранных фильмов.
class FavoritesFragment : Fragment() {

    // Переменная для хранения экземпляра биндинга (nullable)
    private var _binding: FragmentFavoritesBinding? = null
    // Свойство для доступа к биндингу, которое гарантирует,
    // что он не будет null после onCreateView
    private val binding get() = _binding!!

    // Адаптер для RecyclerView
    private lateinit var filmsAdapter: FilmListRecyclerAdapter

    // Инициализация ViewModel с помощью делегата viewModels и кастомной фабрики
    private val viewModel: FavoritesFragmentViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(FavoritesFragmentViewModel::class.java)) {
                    // Получаем Interactor из синглтона App
                    val interactor = App.instance.dagger.filmInteractor()
                    @Suppress("UNCHECKED_CAST")
                    // Создаем экземпляр FavoritesFragmentViewModel
                    return FavoritesFragmentViewModel(interactor) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    // Метод для создания и возвращения View фрагмента
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Инициализируем биндинг
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        // Возвращаем корневой View макета
        return binding.root
    }

    // Метод, вызываемый после создания View
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Запускаем анимацию появления фрагмента
        AnimationHelper.performFragmentCircularRevealAnimation(binding.root, requireActivity(), 2)

        // Инициализация адаптера RecyclerView с обработчиками кликов
        filmsAdapter = FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
            // Обработчик клика по элементу списка
            override fun click(film: Film) {
                // Запускаем фрагмент с деталями фильма
                (requireActivity() as MainActivity).launchDetailsFragment(film)
            }

            override fun onFavoriteClick(film: Film) {
                viewModel.onFavoriteClicked(film)
            }
        })

        // Настройка RecyclerView
        binding.favoritesRecycler.apply {
            adapter = filmsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            // Добавляем отступы между элементами
            val decorator = TopSpacingItemDecoration(8)
            addItemDecoration(decorator)
        }

        // Запускаем корутину для наблюдения за потоком избранных фильмов из ViewModel
        lifecycleScope.launch {
            viewModel.favoriteFilms.collectLatest { films ->
                // Передаем список фильмов в адаптер
                filmsAdapter.submitData(films)
            }
        }
    }

    // Метод, вызываемый при уничтожении View фрагмента
    override fun onDestroyView() {
        super.onDestroyView()
        // Очищаем ссылку на биндинг для предотвращения утечек памяти
        _binding = null
    }
}
