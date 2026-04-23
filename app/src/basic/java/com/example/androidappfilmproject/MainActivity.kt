package com.example.androidappfilmproject

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
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
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

// Класс MainActivity, который является главным в приложении для BASIC флавора.
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var receiver: BroadcastReceiver
    private val trialHandler = Handler(Looper.getMainLooper())

    private val trialCheckerRunnable = object : Runnable {
        override fun run() {
            if (!isFeatureAvailable()) {
                handleTrialExpiration()
            } else {
                trialHandler.postDelayed(this, 1000)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Записываем время ПЕРВОГО запуска (если его еще нет)
        val prefs = getSharedPreferences("trial_prefs", Context.MODE_PRIVATE)
        if (prefs.getLong("first_launch_time", 0L) == 0L) {
            prefs.edit { putLong("first_launch_time", System.currentTimeMillis()) }
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        receiver = ConnectionChecker()
        val filters = IntentFilter().apply {
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_BATTERY_LOW)
        }
        registerReceiver(receiver, filters)

        initNavigation()

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_placeholder, SplashFragment())
                .commit()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            showPromoIfNeeded()
        }, 3000)

        intent?.let { intent ->
            val film = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra("film", Film::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra<Film>("film")
            }
            film?.let { launchDetailsFragment(it) }
        }

        trialHandler.post(trialCheckerRunnable)
    }

    @SuppressLint("CheckResult")
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

                    // Теперь из Remote Config берем только разрешение на показ
                    val isPromoEnabled = firebaseRemoteConfig.getBoolean("show_promo")

                    if (isPromoEnabled) {
                        // Если показ разрешен, идем в TMDB за свежими трендами
                        val interactor = (application as App).dagger.getInteractor()

                        interactor.getRecommendation("popular")
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ films ->
                                if (films.isNotEmpty()) {
                                    // Выбираем фильм на основе дня года, чтобы он менялся раз в сутки
                                    val dayOfYear = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)
                                    val index = dayOfYear % films.size
                                    val selectedFilm = films[index]

                                    displayPromo(selectedFilm)
                                }
                            }, {
                                // Ошибка загрузки из API (например, нет сети)
                                Snackbar.make(binding.root, R.string.error_connection_vpn, Snackbar.LENGTH_LONG).show()
                            })
                    }
                }
        }
    }

    private fun displayPromo(film: Film) {
        App.instance.isPromoShown = true
        binding.promoViewGroup.apply {
            visibility = View.VISIBLE
            animate().setDuration(1000).alpha(1f).start()

            // Устанавливаем постер
            setLinkForPoster(film.poster ?: "")

            closeButton.setOnClickListener {
                visibility = View.GONE
            }

            val action = {
                launchDetailsFragment(film)
                visibility = View.GONE
            }

            watchButton.setOnClickListener { action() }
            poster.setOnClickListener { action() }
        }
    }

    fun launchDetailsFragment(film: Film) {
        val bundle = Bundle().apply { putParcelable("film", film) }
        val fragment = DetailsFragment().apply { arguments = bundle }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_placeholder, fragment)
            .addToBackStack(null).commit()
    }

    @Suppress("unused")
    fun launchLocalDetailsFragment(film: Film) {
        val bundle = Bundle().apply { putParcelable("film", film) }
        val fragment = LocalDetailsFragment().apply { arguments = bundle }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_placeholder, fragment)
            .addToBackStack(null).commit()
    }

    private fun checkFragmentExistence(tag: String): Fragment? = supportFragmentManager.findFragmentByTag(tag)

    private fun changeFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_placeholder, fragment, tag).commit()
    }

    private fun isFeatureAvailable(): Boolean {
        val prefs = getSharedPreferences("trial_prefs", Context.MODE_PRIVATE)
        val firstLaunchTime = prefs.getLong("first_launch_time", 0L)
        if (firstLaunchTime == 0L) return true
        val currentTime = System.currentTimeMillis()
        val trialTimeInMillis = 30 * 24 * 60 * 60 * 1000L // 30 дней
        return (currentTime - firstLaunchTime) < trialTimeInMillis
    }

    private fun handleTrialExpiration() {
        val currentId = binding.bottomNavigation.selectedItemId
        if (currentId == R.id.selections) {
            Toast.makeText(this, R.string.trial_expired, Toast.LENGTH_SHORT).show()
            binding.bottomNavigation.selectedItemId = R.id.home
            val tag = "home"
            val fragment = checkFragmentExistence(tag) ?: HomeFragment()
            changeFragment(fragment, tag)
        }
        trialHandler.removeCallbacks(trialCheckerRunnable)
    }

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
                    if (isFeatureAvailable()) {
                        val tag = "selections"
                        val fragment = checkFragmentExistence(tag) ?: SelectionsFragment()
                        changeFragment(fragment, tag)
                    } else {
                        Toast.makeText(this, R.string.available_in_pro, Toast.LENGTH_SHORT).show()
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

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
        trialHandler.removeCallbacks(trialCheckerRunnable)
    }
}
