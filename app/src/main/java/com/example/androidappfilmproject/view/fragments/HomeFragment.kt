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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: HomeFragmentViewModel by viewModels { viewModelFactory }

    private lateinit var filmsAdapter: FilmListRecyclerAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as App).dagger.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AnimationHelper.performFragmentCircularRevealAnimation(binding.root, requireActivity(), 1)

        initRecycler()
        initPullToRefresh()
        initSearchView()

        // Используем один scope для всех подписок (подсказка 3)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // 1. Сбор данных для списка
                launch {
                    viewModel.films.collectLatest { pagedData ->
                        filmsAdapter.submitData(pagedData)
                    }
                }

                // 2. Сбор состояния прогресс-бара из Channel/Flow (подсказка 4)
                launch {
                    viewModel.showProgressBar.collect { isVisible ->
                        binding.progressBar.isVisible = isVisible
                    }
                }

                // 3. Обработка состояний загрузки Paging
                launch {
                    filmsAdapter.loadStateFlow.collectLatest { loadState ->
                        viewModel.toggleProgressBar(loadState.refresh is LoadState.Loading)
                        
                        if (loadState.refresh is LoadState.Error) {
                            val error = (loadState.refresh as LoadState.Error).error
                            Snackbar.make(binding.root, error.localizedMessage ?: "Ошибка", Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun initRecycler() {
        filmsAdapter = FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
            override fun click(film: Film) {
                (requireActivity() as MainActivity).launchDetailsFragment(film)
            }

            override fun onFavoriteClick(film: Film) {
                viewModel.onFavoriteClicked(film)
            }

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

    private fun initPullToRefresh() {
        binding.pullToRefresh.setOnRefreshListener {
            filmsAdapter.refresh()
            binding.pullToRefresh.isRefreshing = false
        }
    }

    private fun initSearchView() {
        binding.searchView.setOnClickListener {
            binding.searchView.isIconified = false
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
