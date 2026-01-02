package com.example.androidappfilmproject.view.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidappfilmproject.App
import com.example.androidappfilmproject.MainActivity
import com.example.androidappfilmproject.data.entity.Film
import com.example.androidappfilmproject.databinding.FragmentHomeBinding
import com.example.androidappfilmproject.utils.AnimationHelper
import com.example.androidappfilmproject.view.rv_adapters.FilmListRecyclerAdapter
import com.example.androidappfilmproject.view.rv_adapters.TopSpacingItemDecoration
import com.example.androidappfilmproject.viewmodel.HomeFragmentViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

// Создаем класс HomeFragment, который отвечает за отображение главного экрана.
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Внедряем нашу единую фабрику для ViewModel
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    // Получаем ViewModel с помощью Dagger-фабрики
    private val viewModel: HomeFragmentViewModel by viewModels { viewModelFactory }

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
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Вызывается сразу после того, как onCreateView() завершил свою работу.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AnimationHelper.performFragmentCircularRevealAnimation(binding.root, requireActivity(), 1)

        initRecycler()
        initPullToRefresh()
        initSearchView()

        // Запускаем корутину для наблюдения за потоком данных из ViewModel.
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.films.collectLatest { pagedData ->
                filmsAdapter.submitData(pagedData)
            }
        }

        // Наблюдаем за состоянием загрузки данных.
        viewLifecycleOwner.lifecycleScope.launch {
            filmsAdapter.loadStateFlow.collectLatest { loadState ->
                binding.progressBar.isVisible = loadState.refresh is LoadState.Loading
            }
        }
    }

    // Инициализируем RecyclerView.
    private fun initRecycler() {
        filmsAdapter = FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
            // Обрабатываем клик по элементу списка.
            override fun click(film: Film) {
                (requireActivity() as MainActivity).launchDetailsFragment(film)
            }

            // Обрабатываем клик по иконке "избранное".
            override fun onFavoriteClick(film: Film) {
                viewModel.onFavoriteClicked(film)
            }

            // Обрабатываем долгий клик по элементу списка.
            override fun longClick(film: Film) {
                viewModel.removeFilmFromCache(film)
                Snackbar.make(binding.root, "Фильм \"${film.title}\" удален из кэша", Snackbar.LENGTH_SHORT).show()
            }
        })

        binding.mainRecycler.apply {
            adapter = filmsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            val decorator = TopSpacingItemDecoration(8)
            addItemDecoration(decorator)
        }
    }

    // Инициализируем SwipeRefreshLayout.
    private fun initPullToRefresh() {
        binding.pullToRefresh.setOnRefreshListener {
            filmsAdapter.refresh()
            binding.pullToRefresh.isRefreshing = false
        }
    }

    // Инициализируем SearchView.
    private fun initSearchView() {
        binding.searchView.setOnClickListener {
            binding.searchView.isIconified = false
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            // Обрабатываем отправку запроса.
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            // Обрабатываем изменение текста в поисковой строке.
            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isNotBlank()) {
                    viewModel.setQuery(newText.lowercase(Locale.getDefault()))
                } else {
                    viewModel.setQuery("")
                }
                return true
            }
        })
    }

    // Вызывается, когда иерархия представлений, связанная с фрагментом, удаляется.
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
