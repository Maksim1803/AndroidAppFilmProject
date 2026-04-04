package com.example.androidappfilmproject.view.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidappfilmproject.App
import com.example.androidappfilmproject.MainActivity
import com.example.androidappfilmproject.R
import com.example.androidappfilmproject.databinding.FragmentHomeBinding
import com.example.androidappfilmproject.utils.AnimationHelper
import com.example.androidappfilmproject.view.rv_adapters.FilmListRecyclerAdapter
import com.example.androidappfilmproject.view.rv_adapters.TopSpacingItemDecoration
import com.example.androidappfilmproject.viewmodel.HomeFragmentViewModel
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.rx3.asObservable
import java.util.Locale
import javax.inject.Inject
import com.example.database_module.entity.Film

// Создаем класс HomeFragment, который отвечает за отображение главного экрана.
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val compositeDisposable = CompositeDisposable()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: HomeFragmentViewModel by viewModels { viewModelFactory }
    private lateinit var filmsAdapter: FilmListRecyclerAdapter

    // Метод для внедрения зависимостей через Dagger
    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as App).dagger.inject(this)
    }

    // Метод для инициализации ViewBinding и создания UI фрагмента
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Метод для настройки UI и подписки на потоки данных после создания View
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AnimationHelper.performFragmentCircularRevealAnimation(binding.root, 1)

        initRecycler()
        initPullToRefresh()
        initSearchView()

        val filmsDisposable = viewModel.films
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { pagedData ->
                filmsAdapter.submitData(lifecycle, pagedData)
            }
        compositeDisposable.add(filmsDisposable)

        val progressDisposable = viewModel.showProgressBar
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { isVisible ->
                binding.progressBar.isVisible = isVisible
            }
        compositeDisposable.add(progressDisposable)

        // Закоментим пока рекомендацию фильма к просмотру
        // (задание со звездочкой из предыдущих модулей), чтобы
        // не конфликтовала с промо-экраном модуля 53.
        /*
        val recommendationDisposable = viewModel.recommendation
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { film ->
                Snackbar.make(binding.root, "Рекомендуем: ${film.title}", Snackbar.LENGTH_LONG)
                    .setAction("Смотреть") {
                        (requireActivity() as MainActivity).launchDetailsFragment(film)
                    }
                    .show()
            }
        compositeDisposable.add(recommendationDisposable)
        */

        val loadStateDisposable = filmsAdapter.loadStateFlow
            .asObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { loadState ->
                viewModel.toggleProgressBar(loadState.refresh is LoadState.Loading)
                if (loadState.refresh is LoadState.Error) {
                    val error = (loadState.refresh as LoadState.Error).error
                    Snackbar.make(binding.root, error.localizedMessage ?: "Ошибка загрузки", Snackbar.LENGTH_LONG).show()
                }
            }
        compositeDisposable.add(loadStateDisposable)
    }

    // Метод для настройки RecyclerView, адаптера и обработки кликов
    private fun initRecycler() {
        filmsAdapter = FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
            override fun click(film: Film) {
                (requireActivity() as MainActivity).launchDetailsFragment(film)
            }

            override fun onFavoriteClick(film: Film, favoriteIcon: ImageView) {
                film.isInFavorites = !film.isInFavorites

                if (film.isInFavorites) {
                    favoriteIcon.setImageResource(R.drawable.baseline_favorite_24)
                    Snackbar.make(binding.root, "Добавлено в избранное", Snackbar.LENGTH_SHORT).show()
                } else {
                    favoriteIcon.setImageResource(R.drawable.baseline_favorite_border_24)
                    Snackbar.make(binding.root, "Удалено из избранного", Snackbar.LENGTH_SHORT).show()
                }

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
            addItemDecoration(TopSpacingItemDecoration(8))
        }
    }

    // Метод для инициализации обновления списка жестом "pull-to-refresh"
    private fun initPullToRefresh() {
        binding.pullToRefresh.setOnRefreshListener {
            filmsAdapter.refresh()
            binding.pullToRefresh.isRefreshing = false
        }
    }

    // Метод для инициализации и настройки логики поиска
    private fun initSearchView() {
        binding.searchViewHome.setIconifiedByDefault(false)
        binding.searchViewHome.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.setQuery(newText.lowercase(Locale.getDefault()))
                return true
            }
        })
    }

    // Метод для сброса поискового запроса при остановке фрагмента
    override fun onStop() {
        super.onStop()
        viewModel.setQuery("")
    }

    // Метод для принудительного скрытия клавиатуры при приостановке фрагмента
    override fun onPause() {
        super.onPause()
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchViewHome.windowToken, 0)
    }

    // Метод для очистки ресурсов и отписки от RxJava при уничтожении View
    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
        _binding = null
    }
}