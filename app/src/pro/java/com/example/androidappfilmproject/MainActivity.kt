package com.example.androidappfilmproject

import android.content.BroadcastReceiver
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

// Класс MainActivity, который является главным в приложении для PRO флавора.
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var receiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

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
                            closeButton.setOnClickListener { visibility = View.GONE }

                            val action = {
                                if (filmId != 0) {
                                    (application as App).dagger.getInteractor().getFilmById(filmId)
                                        .firstElement()
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe({ film ->
                                            launchDetailsFragment(film)
                                            visibility = View.GONE
                                        }, { visibility = View.GONE }, {
                                            Toast.makeText(this@MainActivity, R.string.promo_film_not_found, Toast.LENGTH_SHORT).show()
                                            visibility = View.GONE
                                        })
                                } else { visibility = View.GONE }
                            }
                            watchButton.setOnClickListener { action() }
                            poster.setOnClickListener { action() }
                        }
                    }
                }
        }
    }

    fun launchDetailsFragment(film: Film) {
        val bundle = Bundle().apply { putParcelable("film", film) }
        val fragment = DetailsFragment().apply { arguments = bundle }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_placeholder, fragment)
            .addToBackStack(null).commit()
    }

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

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}
