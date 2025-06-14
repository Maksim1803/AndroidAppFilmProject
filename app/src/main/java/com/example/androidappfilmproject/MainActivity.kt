package com.example.androidappfilmproject

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.androidappfilmproject.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding // Объявление переменной для View Binding

    // Нововведения модуля 24
    private lateinit var filmsAdapter: FilmListRecyclerAdapter
    val filmsDataBase = listOf(
        Film(
            "Начало",
            R.drawable.nachalo,
            "Вору, который крадёт секреты с помощью технологии обмена снами, поручают задачу: внедрить идею в сознание гендиректора, но его трагическое прошлое может привести всё к катастрофе."
        ),
        Film(
            "Грешники",
            R.drawable.greshniki,
            "Пытаясь оставить позади свою неспокойную жизнь, братья-близнецы возвращаются в родной город, и убеждаются, что здесь их ждёт ещё большее зло."
        ),
        Film(
            "Опустошение",
            R.drawable.opustoshenie,
            "После неудачной сделки с наркотиками избитый детектив пробивается сквозь преступный мир, чтобы спасти сына политика и распутать сложную сеть коррупции и заговоров, в которую попал весь его город."
        ),
        Film(
            "До рассвета",
            R.drawable.untildawn,
            "Группа друзей, оказавшихся в ловушке временной петли, где таинственные враги убивают их ужасными способами, должна дожить до рассвета, чтобы выбраться оттуда."
        ),
        Film(
            "Под огнем",
            R.drawable.podognem,
            "Взвод «морских котиков» отправляется на опасную миссию в Ирак, и рассказывает о хаосе и братстве на войне, вспоминая об этом событии."
        ),
        Film(
            "Субстанция",
            R.drawable.substance,
            "Увядающая знаменитость принимает препарат с чёрного рынка, который воспроизводит клетки и создаёт более молодую и привлекательную версию самой себя."
        ),
        Film(
            "Запертый",
            R.drawable.locked,
            "Вор, забравшийся в роскошный внедорожник, понимает, что попал в изощрённую игру в психологический хоррор."
        ),
        Film(
            "Компаньон",
            R.drawable.companion,
            "Отдых на выходных с друзьями превращается в хаос после того, как выясняется, что один из гостей не тот, за кого себя выдает."
        ),
        Film(
            "Ущелье",
            R.drawable.ushelie,
            "Когда появляется зло, они должны действовать сообща, чтобы выжить и противостоять тому, что внутри."
        ),
        Film(
            "Анора",
            R.drawable.anora,
            "Молодая стриптизерша из Бруклина знакомится с сыном олигарха и выходит за него замуж. Как только новость доходит до его родителей, её сказке приходит конец."
        )
    )
    //Метод, отображающий список фильмов и обрабатывающий нажатие на фильм из списка
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater) // Инициализация binding
        setContentView(binding.root) // Устанавливаем contentView через binding

        initNavigation()

        //находим наш RV
        binding.mainRecycler?.apply {
            //Инициализируем наш адаптер в конструктор передаем анонимно инициализированный интерфейс:
            filmsAdapter = FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener{

                override fun click(film: Film) {
                    //Реализация второй части
                    //Создаем бандл и кладем туда объект с данными фильма
                    val bundle = Bundle()
                    //Первым параметром указывается ключ, по которому потом будем искать, вторым сам
                    //передаваемый объект
                    bundle.putParcelable("film", film)
                    //Запускаем наше активити
                    val intent = Intent(this@MainActivity, DetailsActivity::class.java)
                    //Прикрепляем бандл к интенту
                    intent.putExtras(bundle)
                    //Запускаем активити через интент
                    startActivity(intent)
                }
            })
            //Присваиваем адаптер
            adapter = filmsAdapter
            //Присвоим layout manager
            layoutManager = LinearLayoutManager(this@MainActivity)
            //Применяем декоратор для отступов
            val decorator = TopSpacingItemDecoration(8)
            addItemDecoration(decorator)
        }
//        //Кладем нашу БД в RV - вариант без diffutils
//        filmsAdapter.addItems(filmsDataBase)
//      }
        //Кладем нашу БД в RV - вариант для diffutils
        filmsAdapter.submitList(filmsDataBase)
    }

    //Реализация метода для всплывающих надписей при нажатии на иконки
    private fun initNavigation() {

        //Надпись для верхней иконки "Навигация" (верхнее меню)
        binding.topAppBar?.setNavigationOnClickListener {
            Toast.makeText(this, "Когда-нибудь здесь будет навигация...", Toast.LENGTH_SHORT)
                .show()
        }
        //Надпись для верхней иконки "Настройки" (верхнее меню)
        binding.topAppBar?.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.settings -> {
                    Toast.makeText(this, "Настройки", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
        //Всплывающие надписи для иконок нижнего меню
        binding.bottomNavigation?.setOnNavigationItemSelectedListener {

            when (it.itemId) {
                R.id.favorites -> {
                    Toast.makeText(this, "Избранное", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.watch_later -> {
                    Toast.makeText(this, "Посмотреть позже", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.selections -> {
                    Toast.makeText(this, "Подборки", Toast.LENGTH_SHORT).show()
                    true
                }

                else -> false
            }
        }
    }
}


//Может пригодится...

//    fun onClickToast(view: View) {
//        Toast.makeText(this, "Меню кинофильмов", Toast.LENGTH_SHORT).show()
//    }
//
//    fun onClickToast2(view: View) {
//        Toast.makeText(this, "Избранные кинофильмы", Toast.LENGTH_SHORT).show()
//    }
//
//    fun onClickToast3(view: View) {
//        Toast.makeText(this, "Кинофильмы для просмотра попозже", Toast.LENGTH_SHORT).show()
//    }
//
//    fun onClickToast4(view: View) {
//        Toast.makeText(this, "Кинофильмы в подборке", Toast.LENGTH_SHORT).show()
//    }
//
//    fun onClickToast5(view: View) {
//        Toast.makeText(this, "Настройки для корректной работы", Toast.LENGTH_SHORT).show()
//    }








