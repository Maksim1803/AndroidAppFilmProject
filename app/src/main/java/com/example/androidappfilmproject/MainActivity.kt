package com.example.androidappfilmproject

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androidappfilmproject.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

     private lateinit var binding: ActivityMainBinding // Объявление переменной для View Binding

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityMainBinding.inflate(layoutInflater) // Инициализация binding
            setContentView(binding.root) // Устанавливаем contentView через binding

            initNavigation()

            // Запускаем фрагмент при старте, используя binding.fragmentPlaceholder
            binding.fragmentPlaceholder?.let {
                supportFragmentManager
                    .beginTransaction()
                    .add(binding.fragmentPlaceholder!!.id, HomeFragment())
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
            // Обращение к topAppBar через binding
            binding.topAppBar?.setNavigationOnClickListener {
                Toast.makeText(this, "Когда-нибудь здесь будет навигация...", Toast.LENGTH_SHORT).show()
            }

            binding.topAppBar?.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.settings -> {
                        Toast.makeText(this, "Настройки", Toast.LENGTH_SHORT).show()
                        true
                    }

                    else -> false
                }
            }

            // Обращение к bottomNavigation через binding
            binding.bottomNavigation?.setOnNavigationItemSelectedListener { item ->
                when (item.itemId) {
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








