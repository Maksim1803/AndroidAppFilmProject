package com.example.androidappfilmproject

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
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


// * Класс MainActivity, который является главным в приложении для basic флавора.

class MainActivity : AppCompatActivity() {

    // Объявляем переменную для хранения экземпляра биндинга
    private lateinit var binding: ActivityMainBinding

    // Объявляем переменную для ресивера
    private lateinit var receiver: BroadcastReceiver

    // Добавляем Handler для фоновой проверки времени (используем android.os.Handler)
    private val trialHandler = Handler(Looper.getMainLooper())

    // Объект-задача, который будет проверять триал каждую секунду
    private val trialCheckerRunnable = object : Runnable {
        override fun run() {
            if (!isFeatureAvailable()) {
                // Если время вышло — принудительно выкидываем на главную
                handleTrialExpiration()
            } else {
                // Если еще есть время, планируем следующую проверку через 1 сек
                trialHandler.postDelayed(this, 1000)
            }
        }
    }

    // Метод, вызываемый при создании активности
    override fun onCreate(savedInstanceState: Bundle?) {
        // Включаем поддержку современного режима отрисовки
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // === ЛАЙФХАК ДЛЯ ТЕСТОВ ===
        // Сбрасываем триал при каждом запуске, чтобы можно было потестить 20 секунд
        getSharedPreferences("trial_prefs", Context.MODE_PRIVATE).edit().clear().apply()

        // Записываем время запуска
        val prefs = getSharedPreferences("trial_prefs", Context.MODE_PRIVATE)
        prefs.edit { putLong("first_launch_time", System.currentTimeMillis()) }

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

        // Запускаем фоновую проверку времени
        trialHandler.post(trialCheckerRunnable)
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

    // Логика проверки пробного периода
    private fun isFeatureAvailable(): Boolean {
        val prefs = getSharedPreferences("trial_prefs", Context.MODE_PRIVATE)
        val firstLaunchTime = prefs.getLong("first_launch_time", 0L)
        
        // Если времени нет — считаем, что доступ есть (первые миллисекунды запуска)
        if (firstLaunchTime == 0L) return true
        
        val currentTime = System.currentTimeMillis()
        val trialTimeInMillis = 20 * 1000L // 20 секунд

        return (currentTime - firstLaunchTime) < trialTimeInMillis
    }

    // Метод для автоматического возврата на главную
    private fun handleTrialExpiration() {
        val currentId = binding.bottomNavigation.selectedItemId
        // Проверяем, находится ли пользователь сейчас на "платной" вкладке
        if (currentId == R.id.favorites || currentId == R.id.selections) {
            Toast.makeText(this, "Пробный период истек!", Toast.LENGTH_SHORT).show()

            // Программно переключаем BottomNavigation на Home
            binding.bottomNavigation.selectedItemId = R.id.home

            // Меняем фрагмент
            val tag = "home"
            val fragment = checkFragmentExistence(tag) ?: HomeFragment()
            changeFragment(fragment, tag)
        }
        // Останавливаем таймер, так как выкидывать больше некого
        trialHandler.removeCallbacks(trialCheckerRunnable)
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
                    if (isFeatureAvailable()) {
                        val tag = "favorites"
                        val fragment = checkFragmentExistence(tag) ?: FavoritesFragment()
                        changeFragment(fragment, tag)
                    } else {
                        Toast.makeText(this, "Доступно в Pro версии", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                R.id.watch_later -> {
                    val tag = "watch_later"
                    val fragment = checkFragmentExistence(tag) ?: WatchLaterFragment()
                    changeFragment(fragment, tag)
                    true
                }
                R.id.selections -> {
                    if (isFeatureAvailable()) {
                        val tag = "selections"
                        val fragment = checkFragmentExistence(tag) ?: SelectionsFragment()
                        changeFragment(fragment, tag)
                    } else {
                        Toast.makeText(this, "Доступно в Pro версии", Toast.LENGTH_SHORT).show()
                    }
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
        // Останавливаем таймер
        trialHandler.removeCallbacks(trialCheckerRunnable)
    }
}
