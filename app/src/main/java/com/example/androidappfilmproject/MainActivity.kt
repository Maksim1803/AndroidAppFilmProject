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