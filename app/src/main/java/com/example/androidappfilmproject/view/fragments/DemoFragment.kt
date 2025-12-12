package com.example.androidappfilmproject.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidappfilmproject.App
import com.example.androidappfilmproject.MainActivity
import com.example.androidappfilmproject.databinding.FragmentDemoBinding
import com.example.androidappfilmproject.domain.Film
import com.example.androidappfilmproject.view.rv_adapters.FilmListRecyclerAdapter
import com.example.androidappfilmproject.view.rv_adapters.TopSpacingItemDecoration
import com.example.androidappfilmproject.viewmodel.DemoFragmentViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// Создаем класс DemoFragment, который отвечает за отображение
// демонстрационного списка фильмов.
class DemoFragment : Fragment() {
    // Переменная для хранения экземпляра биндинга (nullable)
    private var _binding: FragmentDemoBinding? = null
    // Свойство для доступа к биндингу, которое гарантирует,
    // что он не будет null после onCreateView
    private val binding get() = _binding!!

    // Инициализация ViewModel с помощью делегата viewModels и кастомной фабрики
    private val viewModel: DemoFragmentViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(DemoFragmentViewModel::class.java)) {
                    // Получаем Interactor из Dagger-компонента
                    val interactor = App.instance.dagger.filmInteractor()
                    @Suppress("UNCHECKED_CAST")
                    // Создаем экземпляр DemoFragmentViewModel
                    return DemoFragmentViewModel(interactor) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    // Адаптер для RecyclerView
    private lateinit var filmsAdapter: FilmListRecyclerAdapter

    // Метод для создания и возвращения View фрагмента
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Инициализируем биндинг
        _binding = FragmentDemoBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Метод, вызываемый после создания View
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализируем адаптер RecyclerView
        filmsAdapter = FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
            // Обработчик клика по элементу списка
            override fun click(film: Film) {
                // Запускаем фрагмент с деталями фильма
                (requireActivity() as MainActivity).launchLocalDetailsFragment(film)
            }

            override fun onFavoriteClick(film: Film) {
                //В демо-режиме эта функция не нужна, поэтому оставляем ее пустой
            }
        })

        // Настраиваем RecyclerView
        binding.demoRecycler.apply {
            adapter = filmsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            // Добавляем отступы между элементами
            addItemDecoration(TopSpacingItemDecoration(8))
        }

        // Запускаем корутину для наблюдения за потоком фильмов из ViewModel
        lifecycleScope.launch {
            viewModel.films.collectLatest { films ->
                // Преобразуем список в PagingData и передаем в адаптер
                filmsAdapter.submitData(PagingData.from(films))
            }
        }
    }

    // Метод, вызываемый при уничтожении View фрагмента
    override fun onDestroyView() {
        super.onDestroyView()
        // Очищаем ссылку на биндинг, чтобы избежать утечек памяти
        _binding = null
    }
}
