package com.example.androidappfilmproject.view.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidappfilmproject.App
import com.example.androidappfilmproject.MainActivity
import com.example.androidappfilmproject.databinding.FragmentFavoritesBinding
import com.example.androidappfilmproject.utils.AnimationHelper
import com.example.androidappfilmproject.view.rv_adapters.FilmListRecyclerAdapter
import com.example.androidappfilmproject.view.rv_adapters.TopSpacingItemDecoration
import com.example.androidappfilmproject.viewmodel.FavoritesFragmentViewModel
import com.example.database_module.entity.Film
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

// Создаем класс FavoritesFragment, который отвечает за отображение списка избранных фильмов.
class FavoritesFragment : Fragment() {

    // Инициализируем ViewBinding
    private var _binding: FragmentFavoritesBinding? = null

    // Используем backing property для получения не nullable версии binding
    private val binding get() = _binding!! //

    // Инициализируем CompositeDisposable для управления подписками
    private val compositeDisposable = CompositeDisposable()

    // // Внедряем нашу единую фабрику для ViewModel
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    // Получаем ViewModel с помощью Dagger-фабрики
    private val viewModel: FavoritesFragmentViewModel by viewModels { viewModelFactory }

    // Ленивая инициализация адаптера
    private lateinit var filmsAdapter: FilmListRecyclerAdapter

    // Вызывается при присоединении фрагмента к контексту.
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Выполняем Dagger-инъекцию, чтобы получить viewModelFactory
        (requireActivity().application as App).dagger.inject(this)
    }
    // Метод для создания иерархии представлений, связанной с фрагментом.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }
    // Метод вызывается сразу после того, как onCreateView() завершил свою работу.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Метод для выполнения круговой анимации при активации фрагмента
        AnimationHelper.performFragmentCircularRevealAnimation(binding.root, 2)

        // Инициализируем адаптер для RecyclerView
        filmsAdapter = FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
            // Обрабатываем клик по элементу списка.
            override fun click(film: Film) {
                (requireActivity() as MainActivity).launchDetailsFragment(film)
            }
            // Добавлен второй параметр favoriteIcon для соответствия интерфейсу
            override fun onFavoriteClick(film: Film, favoriteIcon: ImageView) {
                viewModel.onFavoriteClicked(film)
            }
            // В этом фрагменте не используется
            override fun longClick(film: Film) {}
        })

        // Настраиваем RecyclerView.
        binding.favoritesRecycler.apply {
            adapter = filmsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            val decorator = TopSpacingItemDecoration(8)
            addItemDecoration(decorator)
        }

        // Вызываем метод для получения списка избранных фильмов
        val disposable = viewModel.favoriteFilms
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { films ->
                filmsAdapter.submitData(lifecycle, films)
            }
        compositeDisposable.add(disposable)
    }
    // Вызывается при уничтожении фрагмента.
    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
        _binding = null
    }
}
