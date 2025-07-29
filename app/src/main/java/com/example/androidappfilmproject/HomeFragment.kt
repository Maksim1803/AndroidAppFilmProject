package com.example.androidappfilmproject

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.SearchView
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidappfilmproject.databinding.FragmentHomeBinding
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var filmsAdapter: FilmListRecyclerAdapter

    val filmsDataBase: List<Film> = listOf(
        Film(
            "Начало",
            R.drawable.nachalo,
            "Вору, который крадёт секреты с помощью технологии обмена снами, поручают задачу: внедрить идею в сознание гендиректора, но его трагическое прошлое может привести всё к катастрофе.",
        ),
        Film(
            "Грешники",
            R.drawable.greshniki,
            "Пытаясь оставить позади свою неспокойную жизнь, братья-близнецы возвращаются в родной город, и убеждаются, что здесь их ждёт ещё большее зло.",
        ),
        Film(
            "До рассвета",
            R.drawable.untildawn,
            "Группа друзей, оказавшихся в ловушке временной петли, где таинственные враги убивают их ужасными способами, должна дожить до рассвета, чтобы выбраться оттуда.",
        ),
        Film(
            "Под огнем",
            R.drawable.podognem,
            "Взвод «морских котиков» отправляется на опасную миссию в Ирак, и рассказывает о хаосе и братстве на войне, вспоминая об этом событии.",
        ),
        Film(
            "Субстанция",
            R.drawable.substance,
            "Увядающая знаменитость принимает препарат с чёрного рынка, который воспроизводит клетки и создаёт более молодую и привлекательную версию самой себя.",
        ),
        Film(
            "Запертый",
            R.drawable.locked,
            "Вор, забравшийся в роскошный внедорожник, понимает, что попал в изощрённую игру в психологический хоррор.",
        ),
        Film(
            "Компаньон",
            R.drawable.companion,
            "Отдых на выходных с друзьями превращается в хаос после того, как выясняется, что один из гостей не тот, за кого себя выдает.",
        ),
        Film(
            "Ущелье",
            R.drawable.ushelie,
            "Когда появляется зло, они должны действовать сообща, чтобы выжить и противостоять тому, что внутри.",
        ),
        Film(
            "Интерстеллар",
            R.drawable.interstellar,
            "Когда человечество своим образом жизни пришло к продовольственному кризису, коллектив учёных отправляется сквозь черную дыру (которая соединяет области пространства-времени через большое расстояние) в путешествие, чтобы найти планету с подходящими для человечества условиями жизни.",
        ),
        Film(
            "Хищник",
            R.drawable.hishnik,
            "Для освобождения американских граждан элитная группа спецназа направлена в джунгли. Там, видавшим многое бойцам, предстоит встретиться с невероятным ужасом.",
        ),
        Film(
            "Топган",
            R.drawable.topgun,
            "Очень крутой боевик с Томом Крузом в главной роли. ",
        ),
        Film(
            "Анора",
            R.drawable.anora,
            "Молодая стриптизерша из Бруклина знакомится с сыном олигарха и выходит за него замуж. Как только новость доходит до его родителей, её сказке приходит конец.",
        ),
        Film(
            "Индиана Джонс",
            R.drawable.indianajones,
            "Крутой, можно сказать христоматийный приключенческий боевик",
        ),
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Запускаем анимацию модуля 29
        AnimationHelper.performFragmentCircularRevealAnimation(binding.root, requireActivity(), 1)

        // Для модуля 27 (кнопка поиска)
        binding.searchView.setOnClickListener {
            binding.searchView.isIconified = false
        }

        //Подключаем слушателя изменений введенного текста в поиск
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            //Этот метод отрабатывает при нажатии кнопки "поиск" на софт клавиатуре
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            //Этот метод отрабатывает на каждое изменения текста
            override fun onQueryTextChange(newText: String): Boolean {
                //Если ввод пуст то вставляем в адаптер всю БД
                if (newText.isEmpty()) {
                    filmsAdapter.submitList(filmsDataBase)
                    return true
                }
                //Фильтруем список на поиск подходящих сочетаний
                val result = filmsDataBase.filter {
                    //Чтобы все работало правильно, нужно запросить имя фильма и приводить к нижнему регистру
                    it.title.lowercase(Locale.getDefault())
                        .contains(newText.lowercase(Locale.getDefault()))
                }
                //Добавляем в адаптер
                filmsAdapter.submitList(result)
                return true
            }
        })

        //находим наш RV
        initRecycler()
        //Кладем нашу БД в RV
        filmsAdapter.submitList(filmsDataBase)
        //Вызываем анимацию (модуль 28) после инициализации
        //startHomeScreenAnimation(view)


        //Реализация скрытия поиска при скролле вниз и отображения при скролле вверх
        binding.mainRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && binding.searchView.isVisible) {
                    // Скролл вниз
                    binding.searchView.visibility = View.GONE
                } else if (dy < 0 && !binding.searchView.isVisible) {
                    // Скролл вверх
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
            //Присваиваем адаптер
            adapter = filmsAdapter
            //Присвоим layoutmanager
            layoutManager = LinearLayoutManager(requireContext())
            //Применяем декоратор для отступов
            val decorator = TopSpacingItemDecoration(8)
            addItemDecoration(decorator)
        }
    }

    //Метод, запускающий анимацию модуля 28 (вариант 3)
    private fun startHomeScreenAnimation(view: View) {
        // Устанавливаем начальные значения прозрачности
        binding.searchView.alpha = 0f
        binding.mainRecycler.alpha = 0f

        // Анимация для SearchView
        val searchViewAnimator = ObjectAnimator.ofFloat(binding.searchView, "alpha", 0f, 1f)
        searchViewAnimator.duration = 500 // Длительность анимации в миллисекундах
        searchViewAnimator.interpolator = AccelerateDecelerateInterpolator() // Интерполяция

        // Анимация для RecyclerView
        val recyclerViewAnimator = ObjectAnimator.ofFloat(binding.mainRecycler, "alpha", 0f, 1f)
        recyclerViewAnimator.duration = 500 // Длительность анимации в миллисекундах
        recyclerViewAnimator.interpolator = AccelerateDecelerateInterpolator() // Интерполяция

        // Запускаем анимации последовательно
        searchViewAnimator.start()
        searchViewAnimator.doOnEnd {
            recyclerViewAnimator.start()
        }
    }
}

//Метод, запускающий анимацию модуля 28 (вариант 2)
//    private fun startHomeScreenAnimation() {
//        val homeFragmentRoot = binding.root
//
//        // Создаем сцену из layout через биндинг
//        val scene = Scene.getSceneForLayout(
//            homeFragmentRoot,
//            R.layout.merge_home_screen_content,
//            requireContext()
//        )
//
//        // Создаем анимацию выезда поля поиска сверху
//        val searchSlide = Slide(Gravity.TOP).addTarget(binding.searchView)
//        // Создаем анимацию выезда RecyclerView снизу
//        val recyclerSlide = Slide(Gravity.BOTTOM).addTarget(binding.mainRecycler)
//
//        // Объединяем анимации
//        val customTransition = TransitionSet().apply {
//            duration = 500
//            addTransition(recyclerSlide)
//            addTransition(searchSlide)
//        }
//
//        // Запускаем переход
//        TransitionManager.go(scene, customTransition)
//
//        // Обработка клика по поисковому полю
//        binding.searchView.setOnClickListener {
//            binding.searchView.isIconified = false
//        }
//    }


//Метод, запускающий анимацию модуля 28 (вариант 1)
//    private fun startHomeScreenAnimation(view: View) {
//        val homeFragmentRoot = binding.root // Получаем root view из binding
//
//        view.viewTreeObserver.addOnGlobalLayoutListener(object :
//            ViewTreeObserver.OnGlobalLayoutListener {
//            override fun onGlobalLayout() {
//                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
//
//                val scene = Scene(
//                    homeFragmentRoot as ViewGroup, // Явное приведение к ViewGroup
//                    LayoutInflater.from(requireContext())
//                        .inflate(R.layout.merge_home_screen_content, homeFragmentRoot as ViewGroup, false) // Явное приведение к ViewGroup
//                )
//
//                val searchSlide = Slide(Gravity.TOP).addTarget(binding.searchView)
//                val recyclerSlide = Slide(Gravity.BOTTOM).addTarget(binding.mainRecycler)
//
//                val customTransition = TransitionSet().apply {
//                    duration = 500
//                    addTransition(recyclerSlide)
//                    addTransition(searchSlide)
//                }
//                TransitionManager.go(scene, customTransition)
//            }
//        })
//    }





