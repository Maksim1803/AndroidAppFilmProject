package com.example.androidappfilmproject

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.androidappfilmproject.databinding.ActivityMainBinding
import com.example.androidappfilmproject.receivers.ConnectionChecker
import com.example.androidappfilmproject.view.fragments.DemoFragment
import com.example.androidappfilmproject.view.fragments.DetailsFragment
import com.example.androidappfilmproject.view.fragments.FavoritesFragment
import com.example.androidappfilmproject.view.fragments.HomeFragment
import com.example.androidappfilmproject.view.fragments.LocalDetailsFragment
import com.example.androidappfilmproject.view.fragments.SelectionsFragment
import com.example.androidappfilmproject.view.fragments.SplashFragment
import com.example.androidappfilmproject.view.fragments.WatchLaterFragment
import com.example.database_module.entity.Film
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

// Класс MainActivity, который является главным в приложении.
class MainActivity : AppCompatActivity() {

    // Объявляем переменную для хранения экземпляра биндинга
    private lateinit var binding: ActivityMainBinding

    // Объявляем переменную для ресивера
    private lateinit var receiver: BroadcastReceiver

    // Метод, вызываемый при создании активности
    override fun onCreate(savedInstanceState: Bundle?) {
        // Включаем поддержку современного режима отрисовки
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Инициализируем биндинг
        binding = ActivityMainBinding.inflate(layoutInflater)
        // Устанавливаем макет для активности
        setContentView(binding.root)

        // Настраиваем отступы
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        // Инициализируем ресивер
        receiver = ConnectionChecker()
        // Создаем фильтр для нужных нам событий
        val filters = IntentFilter().apply {
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_BATTERY_LOW)
        }
        // Регистрируем ресивер: теперь он будет слушать систему, пока MainActivity жива
        registerReceiver(receiver, filters)

        // Запускаем инициализацию нижнего навигационного меню
        initNavigation()

        // Запускаем SplashFragment в качестве начального экрана приветствия при первом запуске
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_placeholder, SplashFragment())
                .commit()
        }

        // Реализация показа кастомной композиции view при запуске
        // Добавляем задержку, чтобы Splash успел закрыться и не перебивал навигацию
        Handler(Looper.getMainLooper()).postDelayed({
            showPromoIfNeeded()
        }, 3000)

        // Обработка перехода из уведомления (задание со звездочкой)
        intent?.let { intent ->
            val film = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra("film", Film::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra<Film>("film")
            }
            film?.let { launchDetailsFragment(it) }
        }
    }

    private fun showPromoIfNeeded() {
        if (!App.instance.isPromoShown) {
            val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
            
            val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build()
            firebaseRemoteConfig.setConfigSettingsAsync(configSettings)

            firebaseRemoteConfig.fetch()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        firebaseRemoteConfig.activate()
                    }
                    
                    val filmLink = firebaseRemoteConfig.getString("film_link")
                    val filmId = firebaseRemoteConfig.getLong("film_id").toInt()
                    
                    if (filmLink.isNotBlank()) {
                        App.instance.isPromoShown = true
                        binding.promoViewGroup.apply {
                            visibility = View.VISIBLE
                            animate().setDuration(1000).alpha(1f).start()
                            setLinkForPoster(filmLink)
                            
                            closeButton.setOnClickListener {
                                visibility = View.GONE
                            }

                            // Обработка клика: ищем фильм в БД по ID
                            val action = {
                                if (filmId != 0) {
                                    (application as App).dagger.getInteractor().getFilmById(filmId)
                                        .firstElement()
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe({ film ->
                                            launchDetailsFragment(film)
                                            visibility = View.GONE
                                        }, {
                                            visibility = View.GONE
                                        }, {
                                            visibility = View.GONE
                                        })
                                } else {
                                    visibility = View.GONE
                                }
                            }

                            watchButton.setOnClickListener { action() }
                            poster.setOnClickListener { action() }
                        }
                    }
                }
        }
    }

    // Метод для запуска фрагмента с деталями фильма
    fun launchDetailsFragment(film: Film) {
        val bundle = Bundle().apply {
            putParcelable("film", film)
        }
        val fragment = DetailsFragment().apply {
            arguments = bundle
        }
        // Добавляем транзакцию в backstack и заменяем текущий фрагмент
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_placeholder, fragment)
            .addToBackStack(null)
            .commit()
    }

    // Метод для запуска фрагмента с деталями фильма из локальной базы данных
    fun launchLocalDetailsFragment(film: Film) {
        val bundle = Bundle().apply {
            putParcelable("film", film)
        }
        val fragment = LocalDetailsFragment().apply {
            arguments = bundle
        }
        // Добавляем транзакцию в backstack и заменяем текущий фрагмент
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_placeholder, fragment)
            .addToBackStack(null)
            .commit()
    }

    // Метод для проверки существования фрагмента по заданному тегу
    private fun checkFragmentExistence(tag: String): Fragment? =
        supportFragmentManager.findFragmentByTag(tag)

    // Метод для изменения текущего фрагмента и добавления его в backstack
    private fun changeFragment(fragment: Fragment, tag: String) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_placeholder, fragment, tag)
            .commit()
    }

    // Метод для инициализации нижнего навигационного меню
    private fun initNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
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

    // Метод, вызываемый при уничтожении активности
    override fun onDestroy() {
        super.onDestroy()
        // Отключаем ресивер при уничтожении активности
        unregisterReceiver(receiver)
    }
}
