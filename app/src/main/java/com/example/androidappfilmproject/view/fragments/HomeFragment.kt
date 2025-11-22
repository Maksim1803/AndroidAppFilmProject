package com.example.androidappfilmproject.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidappfilmproject.App
import com.example.androidappfilmproject.MainActivity
import com.example.androidappfilmproject.databinding.FragmentHomeBinding
import com.example.androidappfilmproject.domain.Film
import com.example.androidappfilmproject.utils.AnimationHelper
import com.example.androidappfilmproject.view.rv_adapters.FilmListRecyclerAdapter
import com.example.androidappfilmproject.view.rv_adapters.TopSpacingItemDecoration
import com.example.androidappfilmproject.viewmodel.HomeFragmentViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// Создаем класс HomeFragment, который отвечает за отображение главного экрана,
// включая список фильмов и строку поиска.
class HomeFragment : Fragment() {

    // Переменная для хранения экземпляра биндинга (nullable).
    // Нужна, чтобы избежать утечек памяти в onDestroyView.
    private var _binding: FragmentHomeBinding? = null
    // Свойство для доступа к биндингу, которое гарантирует,
    // что он не будет null после onCreateView.
    private val binding get() = _binding!!

    // Адаптер для RecyclerView, который будет отображать список фильмов.
    private lateinit var filmsAdapter: FilmListRecyclerAdapter

    // Инициализация ViewModel с помощью делегата viewModels и кастомной фабрики.
    // ViewModel переживает пересоздание фрагмента, сохраняя его состояние.
    private val viewModel: HomeFragmentViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(HomeFragmentViewModel::class.java)) {
                    // Получаем Interactor из синглтона App для доступа к работе.
                    val interactor = App.instance.interactor
                    @Suppress("UNCHECKED_CAST")
                    // Создаем экземпляр HomeFragmentViewModel.
                    return HomeFragmentViewModel(interactor) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    // Метод для создания и возвращения View фрагмента.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Инициализируем биндинг для доступа к элементам макета.
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Метод, вызываемый после создания View.
    // Здесь происходит основная настройка UI и подписка на данные.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Запускаем анимацию появления фрагмента для более плавного перехода.
        AnimationHelper.performFragmentCircularRevealAnimation(
            binding.root, requireActivity(), 1
        )

        // Инициализируем SearchView для поиска фильмов.
        initSearchView()
        // Инициализируем RecyclerView для отображения списка фильмов.
        initRecycler()

        // Запускаем корутину для наблюдения за потоком фильмов из ViewModel.
        // collectLatest автоматически отменяет предыдущий сбор данных при поступлении новых.
        lifecycleScope.launch {
            viewModel.films.collectLatest { films ->
                // Передаем PagingData в адаптер для отображения.
                filmsAdapter.submitData(films)
            }
        }
    }

    // Метод, вызываемый при остановке фрагмента.
    override fun onStop() {
        super.onStop()
        // Очищаем поисковый запрос, чтобы при возвращении на экран
        // не отображались результаты предыдущего поиска.
        binding.searchView.setQuery("", false)
    }

    // Метод для инициализации SearchView.
    private fun initSearchView() {
        binding.searchView.setOnClickListener {
            binding.searchView.isIconified = false
        }

        // Устанавливаем слушатель для обработки событий в SearchView.
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            // Этот метод вызывается, когда пользователь нажимает кнопку "поиск" на клавиатуре.
            // В данном случае нам не нужно ничего делать, поэтому возвращаем true.
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            // Этот метод вызывается при каждом изменении текста в поисковой строке.
            override fun onQueryTextChange(newText: String?): Boolean {
                // Проверяем, добавлен ли фрагмент к своей активности, чтобы избежать вызовов
                // после того, как фрагмент был отсоединен.
                if (isAdded) {
                    // Передаем новый поисковый запрос в ViewModel.
                    // Если newText равен null, передаем пустую строку.
                    viewModel.setQuery(newText ?: "")
                }
                return true
            }
        })
    }

    // Метод для инициализации RecyclerView.
    private fun initRecycler() {
        binding.mainRecycler.apply {
            // Инициализируем адаптер с обработчиком кликов.
            filmsAdapter = FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
                // Обработчик клика по элементу списка.
                override fun click(film: Film) {
                    // Запускаем фрагмент с деталями фильма.
                    (requireActivity() as MainActivity).launchDetailsFragment(film)
                }
            })
            adapter = filmsAdapter
            // Устанавливаем LayoutManager, который будет располагать элементы в виде вертикального списка.
            layoutManager = LinearLayoutManager(requireContext())
            // Добавляем отступы между элементами для лучшего визуального разделения.
            val decorator = TopSpacingItemDecoration(8)
            addItemDecoration(decorator)
        }
    }

    // Метод, вызываемый при уничтожении View фрагмента.
    override fun onDestroyView() {
        super.onDestroyView()
        // Очищаем ссылку на биндинг, чтобы избежать утечек памяти.
        _binding = null
    }
}
