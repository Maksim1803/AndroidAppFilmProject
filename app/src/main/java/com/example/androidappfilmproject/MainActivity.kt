package com.example.androidappfilmproject

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.androidappfilmproject.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding  // Объявляем переменную binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Инициализируем binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavigation()

        // Запускаем фрагмент при старте
        binding.fragmentPlaceholder?.let {
            setFragmentBackground(R.drawable.corner_background)
            supportFragmentManager
                .beginTransaction()
                .add(it.id, HomeFragment())
                .addToBackStack(null)
                .commit()
        }
    }
    // Запускаем функцию
    private fun setFragmentBackground(colorResId: Int) {
        binding.fragmentPlaceholder?.setBackgroundResource(colorResId)
    }


    fun launchDetailsFragment(film: Film) {
        val bundle = Bundle().apply {
            putParcelable("film", film)
        }
        val fragment = DetailsFragment().apply {
            arguments = bundle
        }

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

//    //Вариант 1
//    private fun changeFragment(fragment: Fragment, tag: String) {
//        supportFragmentManager
//            .beginTransaction()
//            .replace(R.id.fragment_placeholder, fragment, tag)
//            .addToBackStack(null)
//            .commit()
//    }
      //Вариант 2
      private fun changeFragment(fragment: Fragment, tag: String, bgRes: Int) {
        setFragmentBackground(bgRes)
          supportFragmentManager
          .beginTransaction()
          .replace(R.id.fragment_placeholder, fragment, tag)
          .addToBackStack(null)
          .commit()
      }

    private fun initNavigation() {
        binding.bottomNavigation?.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {

                //Вариант 1
//                R.id.home -> {
//                    val tag = "home"
//                    val fragment = checkFragmentExistence(tag) ?: HomeFragment()
//                    changeFragment(fragment, tag)
//                    true
//                }
//
//                R.id.favorites -> {
//                    val tag = "favorites"
//                    val fragment = checkFragmentExistence(tag) ?: FavoritesFragment()
//                    changeFragment(fragment, tag)
//                    true
//                }
//
//                R.id.selections -> {
//                    val tag = "selections"
//                    val fragment = checkFragmentExistence(tag) ?: SelectionsFragment()
//                    changeFragment(fragment, tag)
//                    true
//                }
//
//                R.id.watch_later -> {
//                    val tag = "watch_later"
//                    val fragment = checkFragmentExistence(tag) ?: WatchLaterFragment()
//                    changeFragment(fragment, tag)
//                    true
//                }

                //Вариант 2
                R.id.home -> {
                    val tag = "home"
                    val fragment = checkFragmentExistence(tag) ?: HomeFragment()
                    changeFragment(fragment, "home", R.drawable.corner_background)
                    true
                }

                R.id.favorites -> {
                    val tag = "favorites"
                    val fragment = checkFragmentExistence(tag) ?: FavoritesFragment()
                    changeFragment(fragment, "home", R.drawable.background_favorites)
                    true
                }

                R.id.watch_later -> {
                    val tag = "watch_later"
                    val fragment = checkFragmentExistence(tag) ?: HomeFragment()
                    changeFragment(fragment, "home", R.drawable.background_watch_later)
                    true
                }

                R.id.selections -> {
                    val tag = "selections"
                    val fragment = checkFragmentExistence(tag) ?: SelectionsFragment()
                    changeFragment(fragment, "home", R.drawable.background_selections)
                    true
                }

//                R.id.favorites -> {
//                    val tag = "favorites"
//                    val fragment =
//                        supportFragmentManager.findFragmentByTag(tag) ?: FavoritesFragment()
//                    supportFragmentManager
//                        .beginTransaction()
//                        .replace(
//                            binding.fragmentPlaceholder?.id
//                                ?: return@setOnNavigationItemSelectedListener false, fragment, tag
//                        )
//                        .addToBackStack(null)
//                        .commit()
//                    true
//                }

//                R.id.favorites -> {
//                    binding.fragmentPlaceholder?.let {
//                        supportFragmentManager
//                            .beginTransaction()
//                            .replace(it.id, FavoritesFragment())
//                            .addToBackStack(null)
//                            .commit()
//                    }
//                    true
//                }

//                R.id.favorites -> {
//                        val tag = "favorites"
//                        val fragment = checkFragmentExistence(tag) ?: FavoritesFragment()
//                        changeFragment(fragment, tag)
//                        true
//                        }

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








