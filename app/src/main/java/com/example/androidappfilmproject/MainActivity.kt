package com.example.androidappfilmproject

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
            supportFragmentManager
                .beginTransaction()
                .add(it.id, HomeFragment())
                .addToBackStack(null)
                .commit()
        }
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

    private fun initNavigation() {
        binding.bottomNavigation?.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {

                R.id.favorites -> {
                    binding.fragmentPlaceholder?.let {
                        supportFragmentManager
                            .beginTransaction()
                            .replace(it.id, FavoritesFragment())
                            .addToBackStack(null)
                            .commit()
                    }
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
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater) // Инициализация binding
//        setContentView(binding.root) // Устанавливаем contentView через binding
//
//        initNavigation()
//        // Запускаем фрагмент при старте, используя binding.fragmentPlaceholder
//        addFragment(HomeFragment()) // Используем helper function для добавления фрагмента
//    }
//
//    private fun initNavigation() {
//        // Обращение к topAppBar через binding
//        binding.topAppBar?.setNavigationOnClickListener {
//            Toast.makeText(this, "Когда-нибудь здесь будет навигация...", Toast.LENGTH_SHORT)
//                .show()
//        }
//
//        binding.topAppBar?.setOnMenuItemClickListener {
//            when (it.itemId) {
//                R.id.settings -> {
//                    Toast.makeText(this, "Настройки", Toast.LENGTH_SHORT).show()
//                    true
//                }
//                else -> false
//            }
//        }
//
//        // Обращение к bottomNavigation через binding
//
//        binding.bottomNavigation?.setOnItemSelectedListener { item -> // Используем setOnItemSelectedListener
//            when (item.itemId) {
//                R.id.favorites -> {
//                    showFavorites()
//                    true
//                }
//
//                R.id.watch_later -> {
//                    Toast.makeText(this, "Посмотреть позже", Toast.LENGTH_SHORT).show()
//                    true
//                }
//
//                R.id.selections -> {
//                    Toast.makeText(this, "Подборки", Toast.LENGTH_SHORT).show()
//                    true
//                }
//
//                else -> false
//            }
//        }
//    }
//
//    private fun showFavorites() {
//        replaceFragment(FavoritesFragment()) // Используем helper function для замены фрагмента
//    }
//
//    fun launchDetailsFragment(film: Film) {
//        val bundle = Bundle().apply {
//            putParcelable("film", film)
//        }
//        val fragment = DetailsFragment().apply {
//            arguments = bundle
//        }
//        replaceFragment(fragment) // Используем helper function для замены фрагмента
//    }
//
//
//    // Helper function для добавления фрагмента (используется только при старте)
//    private fun addFragment(fragment: Fragment) {
//        binding.fragmentPlaceholder?.let {
//            supportFragmentManager
//                .beginTransaction()
//                .add(it.id, fragment)
//                .commit()
//        } ?: run {
//            // Обработка случая, когда fragmentPlaceholder отсутствует в макете.
//            // Можно выбросить исключение, показать ошибку или использовать альтернативный способ отображения фрагмента.
//            Toast.makeText(this, "Ошибка: fragmentPlaceholder отсутствует в макете!", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//
//    // Helper function для замены фрагмента (используется для навигации)
//    private fun replaceFragment(fragment: Fragment) {
//        binding.fragmentPlaceholder?.let {
//            supportFragmentManager
//                .beginTransaction()
//                .replace(it.id, fragment)
//                .addToBackStack(null)
//                .commit()
//        } ?: run {
//            // Обработка случая, когда fragmentPlaceholder отсутствует в макете.
//            // Можно выбросить исключение, показать ошибку или использовать альтернативный способ отображения фрагмента.
//            Toast.makeText(this, "Ошибка: fragmentPlaceholder отсутствует в макете!", Toast.LENGTH_SHORT).show()
//        }
//    }
//}

//        // Запускаем фрагмент при старте, используя binding.fragmentPlaceholder
//      //  binding.fragmentPlaceholder?.let {
//            supportFragmentManager
//                .beginTransaction()
//                .add(binding.fragmentPlaceholder!!.id, HomeFragment())
//                .commit()
//        }
// //   }
//
//    private fun initNavigation() {
//        // Обращение к topAppBar через binding
//        binding.topAppBar?.setNavigationOnClickListener {
//            Toast.makeText(this, "Когда-нибудь здесь будет навигация...", Toast.LENGTH_SHORT)
//                .show()
//        }
//
//        binding.topAppBar?.setOnMenuItemClickListener {
//            when (it.itemId) {
//                R.id.settings -> {
//                    Toast.makeText(this, "Настройки", Toast.LENGTH_SHORT).show()
//                    true
//                }
//                else -> false
//            }
//        }
//
//        // Обращение к bottomNavigation через binding
////            binding.bottomNavigation?.setOnNavigationItemSelectedListener { item ->
////                when (item.itemId) {
////                    R.id.favorites -> {
////                        Toast.makeText(this, "Избранное", Toast.LENGTH_SHORT).show()
////                        true
////                    }
////
////                    R.id.watch_later -> {
////                        Toast.makeText(this, "Посмотреть позже", Toast.LENGTH_SHORT).show()
////                        true
////                    }
////
////                    R.id.selections -> {
////                        Toast.makeText(this, "Подборки", Toast.LENGTH_SHORT).show()
////                        true
////                    }
////
////                    else -> false
////                }
////             }
////           }
//
//        binding.bottomNavigation?.setOnNavigationItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.favorites -> {
//                    showFavorites()
//                    true
//                }
//
//                R.id.watch_later -> {
//                    Toast.makeText(this, "Посмотреть позже", Toast.LENGTH_SHORT).show()
//                    true
//                }
//
//                R.id.selections -> {
//                    Toast.makeText(this, "Подборки", Toast.LENGTH_SHORT).show()
//                    true
//                }
//
//                else -> false
//            }
//        }
//    }
//
//    private fun showFavorites() {
//        supportFragmentManager
//            .beginTransaction()
//            .replace(binding.fragmentPlaceholder!!.id, FavoritesFragment())
//            .addToBackStack(null)
//            .commit()
//    }
//
//    fun launchDetailsFragment(film: Film) {
//        val bundle = Bundle().apply {
//            putParcelable("film", film)
//        }
//        val fragment = DetailsFragment().apply {
//            arguments = bundle
//        }
//        binding.fragmentPlaceholder?.let {
//            supportFragmentManager
//                .beginTransaction()
//                .replace(it.id, fragment)
//                .addToBackStack(null)
//                .commit()
//        }
//    }
//}


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








