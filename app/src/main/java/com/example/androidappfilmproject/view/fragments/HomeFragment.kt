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
import java.util.Locale


class HomeFragment : Fragment() {

    // View Binding для доступа к элементам разметки фрагмента
    private var _binding: FragmentHomeBinding? = null
    // Не-null доступ к binding между onCreateView и onDestroyView
    private val binding get() = _binding!!

    // Флаг, чтобы анимация запускалась только при первой загрузке
    private var isDataLoadedAndAnimated = false

    // Адаптер для RecyclerView, который будет отображать список фильмов
    private lateinit var filmsAdapter: FilmListRecyclerAdapter

    // Ленивая инициализация ViewModel

   // private val viewModel: HomeFragmentViewModel by viewModels()
   // Ленивая инициализация ViewModel
   private val viewModel: HomeFragmentViewModel by viewModels {
   // Анонимный ViewModelProvider.Factory
       object : ViewModelProvider.Factory {
           override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
               if (modelClass.isAssignableFrom(HomeFragmentViewModel::class.java)) {

                   // Безопасно получаем interactor через статический App.instance
                   val interactor = App.instance.interactor

                   @Suppress("UNCHECKED_CAST")
                   // Предполагается, что HomeFragmentViewModel имеет конструктор (interactor: Interactor)
                   return HomeFragmentViewModel(interactor) as T
               }
               throw IllegalArgumentException("Unknown ViewModel class")
           }
       }
   }

    // Переменная для хранения списка фильмов, который будет обновляться из ViewModel
    private var filmsDataBase = listOf<Film>()
        // Используем backing field для обновления списка и адаптера RecyclerView
        set(value) {
            // Если придет такое же значение, то мы выходим из метода
            if (field == value) return
            // Если пришло другое значение, то кладем его в переменную
            field = value
            // Обновляем RV адаптер
            filmsAdapter.submitList(field) // Используем submitList вместо addItems, так как submitList более эффективен для DiffUtil
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

        binding.searchView.setOnClickListener {
            binding.searchView.isIconified = false
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isEmpty()) {
                    filmsAdapter.submitList(filmsDataBase)
                    return true
                }
                val result = filmsDataBase.filter {
                    it.title.lowercase(Locale.getDefault())
                        .contains(newText.lowercase(Locale.getDefault()))
                }
                filmsAdapter.submitList(result)
                return true
            }
        })

        initRecycler()


        // Подписываемся на изменения LiveData из ViewModel
        viewModel.filmsListLiveData.observe(viewLifecycleOwner) { films ->
            filmsDataBase = films // Обновление списка

            // Вызов метода startHomeScreenAnimation() отвечающего за анимацию при запуске программы
            if (!isDataLoadedAndAnimated && films.isNotEmpty()) {
                startHomeScreenAnimation()
                isDataLoadedAndAnimated = true
            }
        }

        binding.mainRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && binding.searchView.isVisible) {
                    binding.searchView.visibility = View.GONE
                } else if (dy < 0 && !binding.searchView.isVisible) {
                    binding.searchView.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun initRecycler() {
        binding.mainRecycler.apply {
            filmsAdapter =
                FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
                    override fun click(film: Film) {
                        (requireActivity() as MainActivity).launchDetailsFragment(film)
                    }
                })
            adapter = filmsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            val decorator = TopSpacingItemDecoration(8)
            addItemDecoration(decorator)
        }
    }

    // Метод, запускающий анимацию экрана при первом запуске программы
    private fun startHomeScreenAnimation() {
        binding.searchView.alpha = 0f
        binding.mainRecycler.alpha = 0f

        val searchViewAnimator = ObjectAnimator.ofFloat(binding.searchView, "alpha", 0f, 1f)
        searchViewAnimator.duration = 500
        searchViewAnimator.interpolator = AccelerateDecelerateInterpolator()

        val recyclerViewAnimator = ObjectAnimator.ofFloat(binding.mainRecycler, "alpha", 0f, 1f)
        recyclerViewAnimator.duration = 500
        recyclerViewAnimator.interpolator = AccelerateDecelerateInterpolator()

        searchViewAnimator.start()
        searchViewAnimator.doOnEnd {
            recyclerViewAnimator.start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
    //Метод, запускающий анимацию модуля 28 (вариант 3)
//    private fun startHomeScreenAnimation(view: View) {
//        // Устанавливаем начальные значения прозрачности
//        binding.searchView.alpha = 0f
//        binding.mainRecycler.alpha = 0f
//
//        // Анимация для SearchView
//        val searchViewAnimator = ObjectAnimator.ofFloat(binding.searchView, "alpha", 0f, 1f)
//        searchViewAnimator.duration = 500 // Длительность анимации в миллисекундах
//        searchViewAnimator.interpolator = AccelerateDecelerateInterpolator() // Интерполяция
//
//        // Анимация для RecyclerView
//        val recyclerViewAnimator = ObjectAnimator.ofFloat(binding.mainRecycler, "alpha", 0f, 1f)
//        recyclerViewAnimator.duration = 500 // Длительность анимации в миллисекундах
//        recyclerViewAnimator.interpolator = AccelerateDecelerateInterpolator() // Интерполяция
//
//        // Запускаем анимации последовательно
//        searchViewAnimator.start()
//        searchViewAnimator.doOnEnd {
//            recyclerViewAnimator.start()
//        }
//    }

