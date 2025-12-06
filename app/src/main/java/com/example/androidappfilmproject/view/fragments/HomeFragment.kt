package com.example.androidappfilmproject.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidappfilmproject.MainActivity
import com.example.androidappfilmproject.databinding.FragmentHomeBinding
import com.example.androidappfilmproject.domain.Film
import com.example.androidappfilmproject.utils.AnimationHelper
import com.example.androidappfilmproject.view.rv_adapters.FilmListRecyclerAdapter
import com.example.androidappfilmproject.view.rv_adapters.TopSpacingItemDecoration
import com.example.androidappfilmproject.viewmodel.HomeFragmentViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// Создаем класс HomeFragment, который отвечает за отображение
// главного экрана со списком фильмов.
class HomeFragment : Fragment() {

    // Переменная для хранения экземпляра биндинга (nullable)
    private var _binding: FragmentHomeBinding? = null
    // Свойство для доступа к биндингу, которое гарантирует, что он не будет null после onCreateView
    private val binding get() = _binding!!

    // Адаптер для RecyclerView
    private lateinit var filmsAdapter: FilmListRecyclerAdapter

    // Инициализация ViewModel с помощью делегата viewModels
    private val viewModel: HomeFragmentViewModel by viewModels()

    // Метод для создания и возвращения View фрагмента
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Инициализируем биндинг
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Метод, вызываемый после создания View
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Запускаем анимацию появления фрагмента
        AnimationHelper.performFragmentCircularRevealAnimation(
            binding.root, requireActivity(), 1
        )

        //Инициализируем SearchView
        initSearchView()
        //Инициализируем RecyclerView
        initRecycler()

        // Запускаем корутину для наблюдения за потоком фильмов из ViewModel
        lifecycleScope.launch {
            viewModel.films.collectLatest { films ->
                // Передаем PagingData в адаптер
                filmsAdapter.submitData(films)
            }
        }
    }

    // Метод, вызываемый при остановке фрагмента
    override fun onStop() {
        super.onStop()
        // Сбрасываем поисковый запрос, чтобы при возвращении на экран список не был отфильтрован
        binding.searchView.setQuery("", false)
    }

    // Метод для инициализации SearchView
    private fun initSearchView() {
        // Устанавливаем слушатель клика, чтобы разворачивать поле поиска
        binding.searchView.setOnClickListener {
            binding.searchView.isIconified = false
        }

        // Устанавливаем слушатель для отслеживания изменений текста в поле поиска
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            // Этот метод нам не нужен, но его нужно реализовать
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            // Метод, который вызывается при каждом изменении текста
            override fun onQueryTextChange(newText: String?): Boolean {
                // Проверяем, присоединен ли фрагмент к Activity
                if (isAdded) {
                    // Передаем новый поисковый запрос в ViewModel
                    viewModel.setQuery(newText ?: "")
                }
                return true
            }
        })
    }

    // Метод для инициализации RecyclerView
    private fun initRecycler() {
        // Настраиваем RecyclerView
        binding.mainRecycler.apply {
            // Инициализируем адаптер с обработчиками кликов
            filmsAdapter = FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
                // Обработчик клика по элементу списка
                override fun click(film: Film) {
                    // Запускаем фрагмент с деталями фильма
                    (requireActivity() as MainActivity).launchDetailsFragment(film)
                }

                // Обработчик клика по иконке "избранное"
                override fun onFavoriteClick(film: Film) {
                    // Вызываем метод ViewModel для изменения статуса "избранное"
                    viewModel.onFavoriteClicked(film)
                    // Обновляем список, чтобы отобразить изменения
                    filmsAdapter.refresh()
                }
            })
            // Устанавливаем адаптер
            adapter = filmsAdapter
            // Устанавливаем LayoutManager
            layoutManager = LinearLayoutManager(requireContext())
            // Добавляем отступы между элементами
            val decorator = TopSpacingItemDecoration(8)
            addItemDecoration(decorator)
        }
    }

    // Метод, вызываемый при уничтожении View фрагмента
    override fun onDestroyView() {
        super.onDestroyView()
        // Очищаем ссылку на биндинг, чтобы избежать утечек памяти
        _binding = null
    }
}
