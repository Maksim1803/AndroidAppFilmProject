package com.example.androidappfilmproject.view.fragments

import android.content.Context
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
import com.example.androidappfilmproject.data.entity.Film
import com.example.androidappfilmproject.databinding.FragmentDemoBinding
import com.example.androidappfilmproject.view.rv_adapters.FilmListRecyclerAdapter
import com.example.androidappfilmproject.view.rv_adapters.TopSpacingItemDecoration
import com.example.androidappfilmproject.viewmodel.DemoFragmentViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

// Создаем класс DemoFragment, который отвечает за отображение
// демонстрационного списка фильмов.
class DemoFragment : Fragment() {
    private var _binding: FragmentDemoBinding? = null
    private val binding get() = _binding!!

    // Внедряем нашу единую фабрику для ViewModel
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    // Получаем ViewModel с помощью Dagger-фабрики
    private val viewModel: DemoFragmentViewModel by viewModels { viewModelFactory }

    private lateinit var filmsAdapter: FilmListRecyclerAdapter

    // Вызывается при присоединении фрагмента к контексту.
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Выполняем Dagger-инъекцию, чтобы получить viewModelFactory
        (requireActivity().application as App).dagger.inject(this)
    }

    // Вызывается для создания иерархии представлений, связанной с фрагментом.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDemoBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Вызывается сразу после того, как onCreateView() завершил свою работу.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализируем адаптер для RecyclerView.
        filmsAdapter = FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
            // Обрабатываем клик по элементу списка.
            override fun click(film: Film) {
                (requireActivity() as MainActivity).launchLocalDetailsFragment(film)
            }
            // В демо-режиме эта функция не нужна.
            override fun onFavoriteClick(film: Film) {
                // В демо-режиме эта функция не нужна
            }

            override fun longClick(film: Film) {
                // В демо-режиме эта функция не нужна
            }
        })

        // Настраиваем RecyclerView.
        binding.demoRecycler.apply {
            adapter = filmsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            // Добавляем отступы между элементами списка.
            addItemDecoration(TopSpacingItemDecoration(8))
        }

        // Запускаем корутину для наблюдения за потоком данных из ViewModel.
        lifecycleScope.launch {
            viewModel.films.collectLatest { films ->
                // Передаем данные в адаптер.
                filmsAdapter.submitData(PagingData.from(films))
            }
        }
    }

    // Вызывается, когда иерархия представлений, связанная с фрагментом, удаляется.
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
