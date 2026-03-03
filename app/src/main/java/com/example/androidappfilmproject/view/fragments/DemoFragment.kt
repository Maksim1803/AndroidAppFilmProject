package com.example.androidappfilmproject.view.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidappfilmproject.App
import com.example.androidappfilmproject.MainActivity
import com.example.androidappfilmproject.databinding.FragmentDemoBinding
import com.example.androidappfilmproject.view.rv_adapters.FilmListRecyclerAdapter
import com.example.androidappfilmproject.view.rv_adapters.TopSpacingItemDecoration
import com.example.androidappfilmproject.viewmodel.DemoFragmentViewModel
import com.example.database_module.entity.Film
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

// Создаем класс DemoFragment, который отвечает за отображение
// демонстрационного списка фильмов.
class DemoFragment : Fragment() {

    // Инициализируем ViewBinding
    private var _binding: FragmentDemoBinding? = null
    private val binding get() = _binding!!

    // Инициализируем CompositeDisposable для управления подписками
    private val compositeDisposable = CompositeDisposable()

    // Внедряем нашу единую фабрику для ViewModel
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    // Инициализируем ViewModel с помощью Dagger-фабрики
    private val viewModel: DemoFragmentViewModel by viewModels { viewModelFactory }

    // Ленивая инициализация адаптера
    private lateinit var filmsAdapter: FilmListRecyclerAdapter

    // Метод для внедрения зависимостей через Dagger
    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as App).dagger.inject(this)
    }

    // Метод для создания и раздувания (inflate) макета фрагмента
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDemoBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Метод для настройки компонентов экрана и подписки на данные из ViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecycler()

        binding.searchViewDemo.postDelayed({
            if (_binding != null) initSearchView()
        }, 600)

        val disposable = viewModel.films
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { films ->
                filmsAdapter.submitData(lifecycle, PagingData.from(films))
            }
        compositeDisposable.add(disposable)
    }


    // Метод для инициализации RecyclerView
    private fun initRecycler() {
        filmsAdapter =
            FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
                override fun click(film: Film) {
                    (requireActivity() as MainActivity).launchLocalDetailsFragment(film)
                }

                override fun onFavoriteClick(film: Film, favoriteIcon: ImageView) {
                    // В демо-режиме эта функция не используется
                }

                override fun longClick(film: Film) {
                    // В демо-режиме эта функция не используется
                }
            })

        binding.demoRecycler.apply {
            adapter = filmsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(TopSpacingItemDecoration(8))
        }
    }

    // Метод для настройки поисковой строки и обработки ввода текста
    private fun initSearchView() {
        binding.searchViewDemo.setIconifiedByDefault(false)
        binding.searchViewDemo.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.setQuery(newText)
                return true
            }
        })
    }

    // Метод для скрытия мягкой клавиатуры при уходе с фрагмента
    override fun onPause() {
        super.onPause()
        // Ипользуем searchViewDemo вместо searchViewHome
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchViewDemo.windowToken, 0)
    }

    // Метод для очистки подписок и зануления binding для предотвращения утечек памяти
    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
        _binding = null
    }
}
