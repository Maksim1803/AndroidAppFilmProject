package com.example.androidappfilmproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidappfilmproject.databinding.FragmentFavoritesBinding

// Класс фрагмента для отображения списка избранных фильмов вариант 2 (используется)
class FavoritesFragment : Fragment() {

    // View Binding для доступа к элементам UI
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    // Адаптер для RecyclerView
    private lateinit var filmsAdapter: FilmListRecyclerAdapter

    // Создание View фрагмента
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Инициализация View Binding
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        // Возвращаем корневой View макета
        return binding.root
    }

    // Когда View создана и готова к использованию
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Запускаем анимацию модуля 29
        AnimationHelper.performFragmentCircularRevealAnimation(binding.root, requireActivity(), 2)

        // Инициализация адаптера RecyclerView
        filmsAdapter = FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
            // Обработка клика по элементу списка
            override fun click(film: Film) {
                // Запуск DetailsFragment из MainActivity
                (requireActivity() as MainActivity).launchDetailsFragment(film)
            }
        })

        // Настройка RecyclerView
        binding.favoritesRecycler.apply {
            // Установка адаптера
            adapter = filmsAdapter
            // Установка LinearLayoutManager
            layoutManager = LinearLayoutManager(requireContext())

            // Добавление декоратора для отступов между элементами
            val decorator = TopSpacingItemDecoration(8)
            addItemDecoration(decorator)
        }

        // Загрузка списка избранных фильмов
        getFavoritesList()
    }

    // Функция для получения списка избранных фильмов из базы данных
    private fun getFavoritesList() {
        filmsAdapter.submitList(App.favoriteFilms.toList())
    }

    // Уничтожение View фрагмента (освобождение ресурсов)
    override fun onDestroyView() {
        super.onDestroyView()
        // Обнуление binding для предотвращения утечек памяти
        _binding = null
    }
}

// Класс фрагмента для отображения списка избранных фильмов вариант 1
//class FavoritesFragment : Fragment() {
//
//    // View Binding для доступа к элементам UI
//    private var _binding: FragmentFavoritesBinding? = null
//    private val binding get() = _binding!!
//
//    // Адаптер для RecyclerView
//    private lateinit var filmsAdapter: FilmListRecyclerAdapter
//
//    // Создание View фрагмента
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        // Инициализация View Binding
//        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
//        // Возвращаем корневой View макета
//        return binding.root
//    }
//
//    // Когда View создана и готова к использованию
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // Инициализация адаптера RecyclerView
//        filmsAdapter = FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
//            // Обработка клика по элементу списка
//            override fun click(film: Film) {
//                // Запуск DetailsFragment из MainActivity
//                (requireActivity() as MainActivity).launchDetailsFragment(film)
//            }
//        })
//
//        // Настройка RecyclerView
//        binding.favoritesRecycler.apply {
//            // Установка адаптера
//            adapter = filmsAdapter
//            // Установка LinearLayoutManager
//            layoutManager = LinearLayoutManager(requireContext())
//
//            // Добавление декоратора для отступов между элементами
//            val decorator = TopSpacingItemDecoration(8)
//            addItemDecoration(decorator)
//        }
//
//        // Загрузка списка избранных фильмов
//        getFavoritesList()
//    }
//
//    // Функция для получения списка избранных фильмов из базы данных
//    private fun getFavoritesList() {
//        // Запуск корутины (потока выполнения)
//        // в lifecycleScope для автоматического управления жизненным циклом
//        lifecycleScope.launch(Dispatchers.IO) {
//            // Получение Flow списка избранных фильмов из FilmDao
//            App.instance.db.filmDao().getFavoriteFilms()
//                // Сбор данных из Flow
//                .collect { favoritesList ->
//                    // Переключение в главный поток для обновления UI
//                    withContext(Dispatchers.Main) {
//                        // Отправка списка фильмов в адаптер для отображения
//                        filmsAdapter.submitList(favoritesList)
//                    }
//                }
//        }
//    }
//
//    // Уничтожение View фрагмента (освобождение ресурсов)
//    override fun onDestroyView() {
//        super.onDestroyView()
//        // Обнуление binding для предотвращения утечек памяти
//        _binding = null
//    }
//}
// Класс фрагмента для отображения списка избранных фильмов вариант 1
//class FavoritesFragment : Fragment() {
//
//    // Адаптер для отображения списка фильмов
//    private lateinit var filmsAdapter: FilmListRecyclerAdapter
//
//    // RecyclerView для отображения списка фильмов
//    private lateinit var favoritesRecycler: RecyclerView
//
//    // Вызываем метод при создании вида фрагмента
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Возвращаем разметку фрагмента
//        return inflater.inflate(R.layout.fragment_favorites, container, false)
//    }
//
//    // Вызываем метод после создания вида фрагмента
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // Находим RecyclerView по идентификатору id
//        favoritesRecycler = view.findViewById(R.id.favorites_recycler)
//
//        // Инициализируем адаптер с обработчиком клика по фильму
//        filmsAdapter =
//            FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
//                override fun click(film: Film) {
//                    (requireActivity() as MainActivity).launchDetailsFragment(film)
//                }
//            })
//
//        //Настраиваем RecyclerView
//        favoritesRecycler.apply {
//            //Присваиваем адаптер
//            adapter = filmsAdapter
//            //Присваиваем layout manager
//            layoutManager = LinearLayoutManager(requireContext())
//            //Применяем декоратор для отступов
//            val decorator = TopSpacingItemDecoration(8)
//            addItemDecoration(decorator)
//        }
//
//        // Загружаем список избранных фильмов из базы данных
//        getFavoritesList()
//    }
//
//    // Функция для асинхронного получения списка избранных фильмов из базы данных
//    private fun getFavoritesList() {
//        // Запускаем поток выполнения для асинхронных операций в UI-потоке
//        lifecycleScope.launch(Dispatchers.IO) {
//            // Получаем список избранных фильмов из базы данных через DAO
//            val favoritesList = App.instance.db.filmDao()
//                .getFavoriteFilms() // Получаем избранные фильмы из базы данных
//
//            // Возвращаемся в главный поток для обновления UI
//            withContext(Dispatchers.Main) {
//                // Передаем полученный список в адаптер для отображения
//                filmsAdapter.addItems(favoritesList)
//            }
//        }
//
//    }
//}


//class FavoritesFragment : Fragment() {
//
//     // Адаптер для отображения списка фильмов
//     private lateinit var filmsAdapter: FilmListRecyclerAdapter
//
//     // RecyclerView для отображения списка фильмов
//     private lateinit var favoritesRecycler: RecyclerView
//
//     // Вызываем метод при создании вида фрагмента
//     override fun onCreateView(
//         inflater: LayoutInflater, container: ViewGroup?,
//         savedInstanceState: Bundle?
//     ): View? {
//         // Возвращаем разметку фрагмента
//         return inflater.inflate(R.layout.fragment_favorites, container, false)
//     }
//
//     // Вызываем метод после создания вида фрагмента
//     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//         super.onViewCreated(view, savedInstanceState)
//
//         // Находим RecyclerView по идентификатору id
//         favoritesRecycler = view.findViewById(R.id.favorites_recycler)
//
//         // Инициализируем адаптер с обработчиком клика по фильму
//         filmsAdapter =
//             FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
//                 override fun click(film: Film) {
//                     (requireActivity() as MainActivity).launchDetailsFragment(film)
//                 }
//             })
//
//         //Настраиваем RecyclerView
//         favoritesRecycler.apply {
//             //Присваиваем адаптер
//             adapter = filmsAdapter
//             //Присваиваем layout manager
//             layoutManager = LinearLayoutManager(requireContext())
//             //Применяем декоратор для отступов
//             val decorator = TopSpacingItemDecoration(8)
//             addItemDecoration(decorator)
//         }
//
//         // Загружаем список избранных фильмов из базы данных
//         getFavoritesList()
//     }
//
//     // Функция для асинхронного получения списка избранных фильмов из базы данных
//     private fun getFavoritesList() {
//         // Запускаем поток выполнения для асинхронных операций в UI-потоке
//         lifecycleScope.launch(Dispatchers.IO) {
//             // Получаем список избранных фильмов из базы данных через DAO
//             val favoritesList = App.instance.db.filmDao()
//                 .getFavoriteFilms() // Получаем избранные фильмы из базы данных
//
//             // Возвращаемся в главный поток для обновления UI
//             withContext(Dispatchers.Main) {
//                 // Передаем полученный список в адаптер для отображения
//                 filmsAdapter.submitList(favoritesList)
//             }
//         }
//
//     }
// }
//         lifecycleScope.launch(Dispatchers.IO) {
//             App.instance.db.filmDao().getFavoriteFilms()
//                 .collect { favoritesList ->
//                     withContext(Dispatchers.Main) {
//                         filmsAdapter.submitList(favoritesList)
//                     }
//                 }
//         }
//     }
// }