package com.example.androidappfilmproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.androidappfilmproject.data.entity.Film
import com.example.androidappfilmproject.databinding.ActivityMainBinding
import com.example.androidappfilmproject.view.fragments.DemoFragment
import com.example.androidappfilmproject.view.fragments.DetailsFragment
import com.example.androidappfilmproject.view.fragments.FavoritesFragment
import com.example.androidappfilmproject.view.fragments.HomeFragment
import com.example.androidappfilmproject.view.fragments.LocalDetailsFragment
import com.example.androidappfilmproject.view.fragments.SelectionsFragment
import com.example.androidappfilmproject.view.fragments.SplashFragment
import com.example.androidappfilmproject.view.fragments.WatchLaterFragment

// Класс MainActivity, который является главным в приложении.
class MainActivity : AppCompatActivity() {

    // Объявляем переменную для хранения экземпляра биндинга
    private lateinit var binding: ActivityMainBinding

    // Метод, вызываемый при создании активности
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализируем биндинг
        binding = ActivityMainBinding.inflate(layoutInflater)
        // Устанавливаем макет для активности
        setContentView(binding.root)
        // Запускаем инициализацию нижнего навигационного меню
        initNavigation()

        // Запускаем фрагмент при старте
        binding.fragmentPlaceholder?.let {
            setFragmentBackground(R.drawable.corner_background)
            supportFragmentManager
                .beginTransaction()
                .add(it.id, SplashFragment()) // Добавляем SplashFragment в контейнер
             // .addToBackStack(null)
                .commit()
        }
    }

    // Метод для установки фонового ресурса для контейнера фрагмента
    private fun setFragmentBackground(colorResId: Int) {
        binding.fragmentPlaceholder?.setBackgroundResource(colorResId)
    }

    // Метод для запуска фрагмента с деталями фильма
    fun launchDetailsFragment(film: Film) {
        val bundle = Bundle().apply {
            putParcelable("film", film) // Кладем фильм в Bundle
        }
        val fragment  = DetailsFragment().apply {
            arguments = bundle // Передаем Bundle в фрагмент
        }
    // Заменяем текущий фрагмент на DetailsFragment
        binding.fragmentPlaceholder?.let {
            supportFragmentManager
                .beginTransaction()
                .replace(it.id, fragment)
                .addToBackStack(null) // Добавляем транзакцию в back stack
                .commit()
        }
    }

    // Метод для запуска фрагмента с деталями фильма из локальной базы данных
    fun launchLocalDetailsFragment(film: Film) {
        val bundle = Bundle().apply {
            putParcelable("film", film) // Кладем фильм в Bundle
        }
        val fragment = LocalDetailsFragment().apply {
            arguments = bundle // Передаем Bundle в фрагмент
        }
        binding.fragmentPlaceholder?.let {
            supportFragmentManager
                .beginTransaction()
                .replace(it.id, fragment)
                .addToBackStack(null) // Добавляем транзакцию в back stack
                .commit()
        }
    }

    // Метод для проверки существования фрагмента по тегу
    private fun checkFragmentExistence(tag: String): Fragment? =
        supportFragmentManager.findFragmentByTag(tag)

    // Метод для смены фрагментов
    private fun changeFragment(fragment: Fragment, tag: String) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_placeholder, fragment, tag) // Заменяем фрагмент в контейнере
            .commit()
    }
    // Метод для инициализации навигации по нижнему меню
    private fun initNavigation() {
        binding.bottomNavigation?.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.home -> {
                    val tag = "home"
                    val fragment = checkFragmentExistence(tag) ?: HomeFragment() // Если фрагмента нет, создаем новый
                    changeFragment(fragment, tag)
                    true
                }

                R.id.favorites -> {
                    val tag = "favorites"
                    val fragment = checkFragmentExistence(tag) ?: FavoritesFragment()
                    changeFragment(fragment, tag)
                    true
                }

                R.id.watch_later -> {
                    val tag = "watch_later"
                    val fragment = checkFragmentExistence(tag) ?: WatchLaterFragment()
                    changeFragment(fragment, tag)
                    true
                }

                R.id.selections -> {
                    val tag = "selections"
                    val fragment = checkFragmentExistence(tag) ?: SelectionsFragment()
                    changeFragment(fragment, tag)
                    true
                }

                R.id.demo -> {
                    val tag = "demo"
                    val fragment = checkFragmentExistence(tag) ?: DemoFragment()
                    changeFragment(fragment, tag)
                    true
                }

                else -> false
            }
        }
    }
}
