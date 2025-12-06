package com.example.androidappfilmproject.di

import androidx.room.Room
import com.example.androidappfilmproject.AppDatabase
import com.example.androidappfilmproject.BuildConfig
import com.example.androidappfilmproject.data.ApiConstants
import com.example.androidappfilmproject.data.MainRepository
import com.example.androidappfilmproject.data.TmdbApi
import com.example.androidappfilmproject.domain.Interactor
import com.example.androidappfilmproject.viewmodel.HomeFragmentViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object DI {
    val mainModule = module {
        // База данных
        single { Room.databaseBuilder(androidContext(), AppDatabase::class.java, "film_db").build() }
        // DAO
        single { get<AppDatabase>().filmDao() }
        // Репозиторий
        single { MainRepository(get(), get()) }

        // Retrofit
        single<TmdbApi> {
            val okHttpClient = OkHttpClient.Builder()
                .callTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(HttpLoggingInterceptor().apply {
                    if (BuildConfig.DEBUG) {
                        level = HttpLoggingInterceptor.Level.BASIC
                    }
                })
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(ApiConstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()

            retrofit.create(TmdbApi::class.java)
        }

        // Интерактор
        single { Interactor(get()) }

        // ViewModel
        viewModel { HomeFragmentViewModel() }
    }
}
