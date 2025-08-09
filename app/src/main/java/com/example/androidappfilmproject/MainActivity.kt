package com.example.androidappfilmproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.androidappfilmproject.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    // Объявляем переменную binding
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Очистка кеша Glide (один раз при старте приложения)
        Glide.get(this).clearMemory()
        CoroutineScope(Dispatchers.IO).launch {
            Glide.get(this@MainActivity).clearDiskCache()
        }
        // Инициализируем binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Запускаем нижнее навигационное меню
        initNavigation()

        // Запускаем фрагмент при старте
        binding.fragmentPlaceholder?.let {
            setFragmentBackground(R.drawable.corner_background)
            supportFragmentManager
                .beginTransaction()
                .add(it.id, SplashFragment())
             // .addToBackStack(null)
                .commit()
        }
    }

    // Запускаем фоновый ресурс для контейнера фрагмента
    private fun setFragmentBackground(colorResId: Int) {
        binding.fragmentPlaceholder?.setBackgroundResource(colorResId)
    }

    // Запускаем фрагмент с деталями фильма
    fun launchDetailsFragment(film: Film) {
        val bundle = Bundle().apply {
            putParcelable("film", film)
        }
        val fragment = DetailsFragment().apply {
            arguments = bundle
        }
    // Заменяем текущий фрагмент на DetailsFragment
        binding.fragmentPlaceholder?.let {
            supportFragmentManager
                .beginTransaction()
                .replace(it.id, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    //Ищем фрагмент по тэгу, если он есть то возвращаем его, если нет - то null
    private fun checkFragmentExistence(tag: String): Fragment? =
        supportFragmentManager.findFragmentByTag(tag)

    //Запускаем метод смены фрагментов при нажатии на иконки нижнего меню
    private fun changeFragment(fragment: Fragment, tag: String) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_placeholder, fragment, tag)
            .addToBackStack(null)
            .commit()
    }
    // Инициализация навигации по нижнему меню
    private fun initNavigation() {
        binding.bottomNavigation?.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {

                R.id.home -> {
                    val tag = "home"
                    val fragment = checkFragmentExistence(tag) ?: HomeFragment()
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










