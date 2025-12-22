package com.example.androidappfilmproject.view.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
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
import javax.inject.Inject

// Класс, отвечающий за отображение главного экрана приложения.
// Содержит список фильмов, строку поиска и функциональность обновления списка.
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var filmsAdapter: FilmListRecyclerAdapter

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: HomeFragmentViewModel by viewModels { viewModelFactory }

    // Метод вызывается при присоединении фрагмента к контексту.
    // Внедряет зависимости через Dagger.
    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as App).dagger.inject(this)
    }

    // Метод создает и возвращает иерархию представлений, связанную с фрагментом.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Метод вызывается сразу после onCreateView().
    // Здесь инициализируются UI-компоненты и настраиваются наблюдатели.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AnimationHelper.performFragmentCircularRevealAnimation(binding.root, requireActivity(), 1)

        initSearchView()
        initRecycler()
        initPullToRefresh()

        // Наблюдение за потоком фильмов из ViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.films.collectLatest { films ->
                    filmsAdapter.submitData(films)
                }
            }
        }

        // Наблюдение за состоянием загрузки данных для отображения/скрытия индикатора обновления
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                filmsAdapter.loadStateFlow.collectLatest { loadStates ->
                    binding.pullToRefresh.isRefreshing = loadStates.refresh is LoadState.Loading
                }
            }
        }
    }

    // Метод инициализирует SwipeRefreshLayout для обновления списка фильмов.
    private fun initPullToRefresh() {
        binding.pullToRefresh.setOnRefreshListener {
            filmsAdapter.refresh()
        }
    }

    // Метод вызывается, когда фрагмент становится невидимым.
    // Очищает строку поиска.
    override fun onStop() {
        super.onStop()
        binding.searchView.setQuery("", false)
    }

    // Метод инициализирует SearchView для поиска фильмов.
    // Устанавливает слушателей для обработки ввода текста.
    private fun initSearchView() {
        binding.searchView.setOnClickListener {
            binding.searchView.isIconified = false
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (isAdded) {
                    viewModel.setQuery(newText ?: "")
                }
                return true
            }
        })
    }

    // Метод инициализирует RecyclerView для отображения списка фильмов.
    // Настраивает адаптер, LayoutManager и декоратор элементов.
    private fun initRecycler() {
        binding.mainRecycler.apply {
            filmsAdapter = FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
                override fun click(film: Film) {
                    (requireActivity() as MainActivity).launchDetailsFragment(film)
                }

                override fun onFavoriteClick(film: Film) {
                    viewModel.onFavoriteClicked(film)
                }
            })
            adapter = filmsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            val decorator = TopSpacingItemDecoration(8)
            addItemDecoration(decorator)
        }
    }

    // Метод вызывается при уничтожении представления фрагмента.
    // Очищает ссылку на binding во избежание утечек памяти.
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
