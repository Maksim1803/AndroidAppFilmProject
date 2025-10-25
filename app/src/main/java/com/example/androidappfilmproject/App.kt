package com.example.androidappfilmproject

import android.app.Application
import com.example.androidappfilmproject.data.MainRepository
import com.example.androidappfilmproject.domain.Film
import com.example.androidappfilmproject.domain.Interactor

class App : Application() {
    lateinit var repo: MainRepository
    lateinit var interactor: Interactor

    override fun onCreate() {
        super.onCreate()
        //Инициализируем экземпляр App, через который будем получать доступ к остальным переменным
        instance = this
        //Инициализируем репозиторий
        repo = MainRepository()
        //Инициализируем интерактор
        interactor = Interactor(repo)
    }

    companion object {
        //Здесь статически хранится ссылка на экземпляр App
        lateinit var instance: App
            //Приватный сеттер, чтобы нельзя было в эту переменную присвоить что-либо другое
            private set

        // Добавляем статическое свойство для избранных фильмов
        val favoriteFilms: MutableList<Film> = mutableListOf()
    }
}


//Класс для доступа к базе данных вариант 3 (рабочий)

//class App : Application() {

//    companion object {
//        lateinit var instance: App
//            private set
//        val favoriteFilms = HashSet<Film>() // Храним избранные фильмы в памяти
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//        instance = this
//    }
//}
//Класс для доступа к базе данных вариант 2
//class App : Application() {
//
//    // Объявление переменной для хранения экземпляра базы данных, где
//    // lateinit - указывает, что переменная будет инициализирована позже
//    lateinit var db: AppDatabase // AppDatabase - тип переменной (класс - база данных)
//        private set // обеспечивает доступ к переменной только для чтения извне класса
//
//    // Метод, вызываемый при создании приложения
//    override fun onCreate() {
//        super.onCreate() // Вызов родительского класса
//        instance = this
//        db = Room.databaseBuilder(
//            applicationContext,
//            AppDatabase::class.java,
//            "film_database" // Имя базы данных
//        ).build()
//        // Запускаем корутину для добавления фильмов в базу данных
//        CoroutineScope(Dispatchers.IO).launch {
//            val filmDao = db.filmDao()
//            // Проверяем, есть ли фильмы в базе данных
//            if (filmDao.getAllFilms().firstOrNull()?.isEmpty() == true) {
//                // Добавляем фильмы из filmsDataBase в базу данных
//                HomeFragment().filmsDataBase.forEach { film ->
//                    filmDao.insert(film)
//                }
//            }
//        }
//    }
//
//    // Статический объект для статических переменных и методов
//    companion object {
//        lateinit var instance: App // App - тип переменной (сам класс App)
//            private set
//    }
//}

//Класс для доступа к базе данных вариант 1
//class App : Application() {
//
//    // Объявление переменной для хранения экземпляра базы данных, где
//    // lateinit - указывает, что переменная будет инициализирована позже
//    lateinit var db: AppDatabase // AppDatabase - тип переменной (класс - база данных)
//        private set // обеспечивает доступ к переменной только для чтения извне класса
//
//    // Метод, вызываемый при создании приложения
//    override fun onCreate() {
//        super.onCreate() // Вызов родительского класса
//        instance = this
//        db = Room.databaseBuilder(
//            applicationContext,
//            AppDatabase::class.java,
//            "film_database" // Имя базы данных
//        ).build()
//    }
//    // Статический объект для статических переменных и методов
//    companion object {
//        lateinit var instance: App // App - тип переменной (сам класс App)
//            private set
//    }
//}

//class DetailsFragment : Fragment() {
//    // View Binding для доступа к элементам разметки фрагмента
//    private var _binding: FragmentDetailsBinding? = null
//    // Не-null доступ к binding между onCreateView и onDestroyView
//    private val binding get() = _binding!!
//
//    // Переменная для хранения объекта фильма, который будет отображаться
//    private var film: Film? = null
//
//
//    //Создаем и возвращаем иерархию представлений, связанную с фрагментом.
//    //Инициализируем View Binding.
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
//        return binding.root // Возвращаем корневую View, полученную из binding
//    }
//
//     //Вызываем onCreatedView(), когда иерархия представлений фрагмента была создана.
//     //Инициализируем UI-элементы и обработчики событий.
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // Получаем объект фильма из аргументов фрагмента
//        val args = arguments
//        film = args?.getParcelable<Film>("film")
//        // Если фильм не был передан, показываем ошибку и закрываем Activity
//        if (film == null) {
//            Snackbar.make(binding.root, "Ошибка: Фильм не найден", Snackbar.LENGTH_SHORT).show()
//            activity?.finish()
//            return
//        }
//
//        // Настраиваем Toolbar для отображения навигации "назад"
//        (activity as? AppCompatActivity)?.setSupportActionBar(binding.detailsToolbar)
//        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
//
//        // Обновляем детали фильма на UI
//        setFilmsDetails()
//
//        // Устанавливаем обработчик кликов для кнопки "Избранное"
//        binding.favoriteButton.setOnClickListener {
//            film?.let {
//                if (!it.isInFavorites) {
//                    // Добавляем фильм в избранное
//                    it.isInFavorites = true
//                    binding.favoriteButton.setImageResource(R.drawable.baseline_favorite_24)
//                    Snackbar.make(binding.root, "Добавлено в избранное", Snackbar.LENGTH_SHORT).show()
//                } else {
//                    // Удаляем фильм из избранного
//                    it.isInFavorites = false
//                    binding.favoriteButton.setImageResource(R.drawable.baseline_favorite_border_24)
//                    Snackbar.make(binding.root, "Удалено из избранного", Snackbar.LENGTH_SHORT).show()
//                }
//            }
//        }
//
//        // Устанавливаем обработчик кликов для кнопки "Посмотреть позже"
//        binding.watchLaterButton.setOnClickListener {
//            Snackbar.make(binding.root, "Добавлено в список 'Посмотреть позже'", Snackbar.LENGTH_SHORT).show()
//        }
//
//        // Применяем scaleType для постера фильма
//        binding.detailsPoster.apply {
//            this.scaleType = ImageView.ScaleType.CENTER_CROP
//        }
//
//        // Устанавливаем обработчик кликов для кнопки "Поделиться"
//        binding.detailsFab.setOnClickListener {
//            film?.let {
//                val intent = Intent().apply {
//                    Intent.setAction = Intent.ACTION_SEND
//                    putExtra(
//                        Intent.EXTRA_TEXT,
//                        "Check out this film: ${it.title}\n\n${it.description}"
//                    )
//                    Intent.setType = "text/plain"
//                }
//                startActivity(Intent.createChooser(intent, "Share To:"))
//            }
//        }
//    }


//Обновляем UI-элементы фрагмента данными текущего фильма.
//Запускаем в корутине жизненного цикла View для безопасного доступа к UI.

//    private fun setFilmsDetails() {
//        viewLifecycleOwner.lifecycleScope.launch {
//            film?.let { // Проверяем, что фильм не null
//                binding.apply { // Применяем операции к элементам через binding
//                    detailsToolbar.title = it.title // Устанавливаем заголовок Toolbar
//                    detailsPoster.setImageResource(it.poster) // Устанавливаем изображение постера
//                    detailsDescription.text = it.description // Устанавливаем описание фильма
//
//                    // Устанавливаем иконку избранного в зависимости от статуса фильма
//                    favoriteButton.setImageResource(
//                        if (it.isInFavorites) R.drawable.baseline_favorite_24
//                        else R.drawable.baseline_favorite_border_24
//                    )
//
//                    // Вычисляем и анимируем прогресс рейтинга
//                    val progress = (it.rating * 10).toInt().coerceIn(0, 100)
//                    ratingDonut.setProgressAnimated(progress, 2500L)
//                }
//            }
//        }
//    }
//
//     //Вызываем при уничтожении иерархии представлений фрагмента.
//     //Обнуляем _binding для предотвращения утечек памяти.
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}