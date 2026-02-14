package com.example.androidappfilmproject.view.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
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
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.rx3.asObservable
import java.util.Locale
import javax.inject.Inject

// Создаем класс HomeFragment, который отвечает за отображение главного экрана.
class HomeFragment : Fragment() {

    // Инициализируем ViewBinding для доступа к компонентам макета
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Контейнер для хранения всех Rx-подписок (очищается при уничтожении экрана)
    private val compositeDisposable = CompositeDisposable()

    // Внедряем единую фабрику ViewModel через Dagger
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    // Получаем экземпляр ViewModel для этого фрагмента
    private val viewModel: HomeFragmentViewModel by viewModels { viewModelFactory }

    // Адаптер для списка фильмов (PagingData)
    private lateinit var filmsAdapter: FilmListRecyclerAdapter

    // Вызывается при прикреплении фрагмента к Activity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Выполняем инъекцию зависимостей
        (requireActivity().application as App).dagger.inject(this)
    }

    // Создаем иерархию View из XML макета
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Инициализируем UI и настраиваем подписки после создания View
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Запускаем анимацию кругового появления фрагмента
        AnimationHelper.performFragmentCircularRevealAnimation(binding.root, 1)

        initRecycler() // Настраиваем RecyclerView
        initPullToRefresh() // Настраиваем обновление "тяни-вниз"
        initSearchView() // Настраиваем поле поиска

        // 1. Подписка на поток PagingData (список фильмов) из ViewModel
        val filmsDisposable = viewModel.films
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { pagedData ->
                // Отправляем новые страницы данных в адаптер
                filmsAdapter.submitData(lifecycle, pagedData)
            }
        compositeDisposable.add(filmsDisposable)

        // 2. Подписка на статус видимости прогресс-бара
        val progressDisposable = viewModel.showProgressBar
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { isVisible ->
                binding.progressBar.isVisible = isVisible
            }
        compositeDisposable.add(progressDisposable)

        // 3. Подписка на рекомендации (фильм всплывающий при смене категории)
        val recommendationDisposable = viewModel.recommendation
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { film ->
                // Показываем Snackbar с названием рекомендованного фильма
                Snackbar.make(binding.root, "Рекомендуем: ${film.title}", Snackbar.LENGTH_LONG)
                    .setAction("Смотреть") {
                        // Переход на детали при нажатии на кнопку в Snackbar
                        (requireActivity() as MainActivity).launchDetailsFragment(film)
                    }
                    .show()
            }
        compositeDisposable.add(recommendationDisposable)

        // 4. Обработка состояний загрузки Paging (ошибки, пустые списки)
        val loadStateDisposable = filmsAdapter.loadStateFlow
            .asObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { loadState ->
                // Управляем прогресс-баром в зависимости от состояния загрузки
                viewModel.toggleProgressBar(loadState.refresh is LoadState.Loading)

                // Если при загрузке произошла ошибка, выводим сообщение
                if (loadState.refresh is LoadState.Error) {
                    val error = (loadState.refresh as LoadState.Error).error
                    Snackbar.make(binding.root, error.localizedMessage ?: "Ошибка загрузки", Snackbar.LENGTH_LONG).show()
                }
            }
        compositeDisposable.add(loadStateDisposable)
    }

    // Настройка списка (RecyclerView) и его адаптера
    private fun initRecycler() {
        filmsAdapter = FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
            // Клик по карточке фильма
            override fun click(film: Film) {
                (requireActivity() as MainActivity).launchDetailsFragment(film)
            }
            // Клик по иконке сердечка
            override fun onFavoriteClick(film: Film) {
                viewModel.onFavoriteClicked(film)
            }
            // Долгий клик для удаления из кэша
            override fun longClick(film: Film) {
                viewModel.removeFilmFromCache(film)
                Snackbar.make(binding.root, "Фильм \"${film.title}\" удален из кэша", Snackbar.LENGTH_SHORT).show()
            }
        })

        binding.mainRecycler.apply {
            adapter = filmsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            // Добавляем отступы сверху для элементов списка
            addItemDecoration(TopSpacingItemDecoration(8))
        }
    }

    // Настройка жеста обновления списка
    private fun initPullToRefresh() {
        binding.pullToRefresh.setOnRefreshListener {
            // Принудительно обновляем данные в Paging адаптере
            filmsAdapter.refresh()
            binding.pullToRefresh.isRefreshing = false
        }
    }

    // Настройка строки поиска
    private fun initSearchView() {
        // Поле поиска всегда развернуто по умолчанию
        binding.searchViewHome.setIconifiedByDefault(false)
        binding.searchViewHome.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true
            // Срабатывает при каждом изменении текста (с учетом debounce во ViewModel)
            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.setQuery(newText.lowercase(Locale.getDefault()))
                return true
            }
        })
    }

    // Вызывается, когда пользователь уходит с экрана
    override fun onStop() {
        super.onStop()
        // Сбрасываем поисковый запрос, чтобы при возвращении не было конфликтов
        viewModel.setQuery("")
    }

    // Вызывается при временной приостановке фрагмента
    override fun onPause() {
        super.onPause()
        // Принудительно прячем клавиатуру, чтобы она не "висела" на других экранах
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchViewHome.windowToken, 0)
    }

    // Очистка ссылок и подписок при уничтожении фрагмента
    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear() // Отписываемся от всех потоков данных
        _binding = null // Очищаем биндинг для предотвращения утечек памяти
    }
}
