package com.example.androidappfilmproject.view.fragments

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.widget.SearchView
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import java.util.Locale

// Создаем класс HomeFragment, который отвечает за отображение
// главного экрана со списком фильмов.
class HomeFragment : Fragment() {

    // Переменная для хранения экземпляра биндинга (nullable)
    private var _binding: FragmentHomeBinding? = null
    // Свойство для доступа к биндингу, которое гарантирует,
    // что он не будет null после onCreateView
    private val binding get() = _binding!!

    // Флаг, чтобы анимация запускалась только при первой загрузке
    private var isDataLoadedAndAnimated = false

    // Адаптер для RecyclerView, который будет отображать список фильмов
    private lateinit var filmsAdapter: FilmListRecyclerAdapter

    // Ленивая инициализация ViewModel с использованием делегата viewModels и
    // объекта ViewModelProvider.Factory
   private val viewModel: HomeFragmentViewModel by viewModels {
   // Анонимный ViewModelProvider.Factory для создания ViewModel
       object : ViewModelProvider.Factory {
           override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
               if (modelClass.isAssignableFrom(HomeFragmentViewModel::class.java)) {

                   // Получаем Interactor из синглтона App
                   val interactor = App.instance.interactor

                   @Suppress("UNCHECKED_CAST")
                   // Создаем и возвращаем экземпляр HomeFragmentViewModel
                   return HomeFragmentViewModel(interactor) as T
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
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Метод, вызываемый после создания View
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Запускаем анимацию появления фрагмента
        AnimationHelper.performFragmentCircularRevealAnimation(binding.root, requireActivity(), 1)

        // Устанавливаем слушатель клика на SearchView, чтобы он раскрывался по клику
        binding.searchView.setOnClickListener {
            binding.searchView.isIconified = false
        }

        // Устанавливаем слушатель изменений текста в SearchView
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            // Метод, вызываемый при отправке запроса (нажатии на Enter)
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            // Метод, вызываемый при изменении текста в поле поиска
            override fun onQueryTextChange(newText: String?): Boolean {
                // Передаем новый текст в ViewModel для выполнения поиска
                viewModel.setQuery(newText.orEmpty())
                return true
            }
        })

        // Инициализируем RecyclerView
        initRecycler()

        // Запускаем корутину для наблюдения за потоком фильмов из ViewModel
        lifecycleScope.launch {
            viewModel.films.collectLatest { pagingData ->
                // Передаем данные в адаптер
                filmsAdapter.submitData(pagingData)
            }
        }

        // Подписываемся на изменения состояния загрузки данных в адаптере
        filmsAdapter.addLoadStateListener { loadState ->
            // Запускаем анимацию, если данные загружены и анимация еще не была показана
            if (!isDataLoadedAndAnimated && filmsAdapter.itemCount > 0) {
                startHomeScreenAnimation()
                isDataLoadedAndAnimated = true
            }
        }

        // Добавляем слушатель прокрутки к RecyclerView для скрытия/показа SearchView
        binding.mainRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // Если скроллим вниз, скрываем SearchView
                if (dy > 0 && binding.searchView.isVisible) {
                    binding.searchView.visibility = View.GONE
                // Если скроллим вверх, показываем SearchView
                } else if (dy < 0 && !binding.searchView.isVisible) {
                    binding.searchView.visibility = View.VISIBLE
                }
            }
        })
    }

    // Метод для инициализации RecyclerView
    private fun initRecycler() {
        binding.mainRecycler.apply {
            // Инициализируем адаптер с обработчиком клика
            filmsAdapter =
                FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
                    override fun click(film: Film) {
                        // Запускаем фрагмент с деталями фильма
                        (requireActivity() as MainActivity).launchDetailsFragment(film)
                    }
                })
            adapter = filmsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            // Добавляем отступы между элементами списка
            val decorator = TopSpacingItemDecoration(8)
            addItemDecoration(decorator)
        }
    }

    // Метод, запускающий анимацию появления SearchView и RecyclerView
    private fun startHomeScreenAnimation() {
        // Устанавливаем начальную прозрачность в 0
        binding.searchView.alpha = 0f
        binding.mainRecycler.alpha = 0f

        // Создаем аниматор для SearchView
        val searchViewAnimator = ObjectAnimator.ofFloat(binding.searchView, "alpha", 0f, 1f)
        searchViewAnimator.duration = 500
        searchViewAnimator.interpolator = AccelerateDecelerateInterpolator()

        // Создаем аниматор для RecyclerView
        val recyclerViewAnimator = ObjectAnimator.ofFloat(binding.mainRecycler, "alpha", 0f, 1f)
        recyclerViewAnimator.duration = 500
        recyclerViewAnimator.interpolator = AccelerateDecelerateInterpolator()

        // Запускаем анимацию SearchView
        searchViewAnimator.start()
        // После завершения анимации SearchView запускаем анимацию RecyclerView
        searchViewAnimator.doOnEnd {
            recyclerViewAnimator.start()
        }
    }

    // Метод, вызываемый при уничтожении View фрагмента
    override fun onDestroyView() {
        super.onDestroyView()
        // Очищаем ссылку на биндинг, чтобы избежать утечек памяти
        _binding = null
    }
}
