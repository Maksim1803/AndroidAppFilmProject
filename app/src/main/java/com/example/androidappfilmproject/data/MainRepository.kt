package com.example.androidappfilmproject.data

import android.content.Context
import android.content.res.Configuration
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.rxjava3.observable
import com.example.androidappfilmproject.BuildConfig
import com.example.androidappfilmproject.R
import com.example.androidappfilmproject.data.preferences.PreferenceProvider
import com.example.database_module.db.AppDatabase
import com.example.database_module.entity.Film
import com.example.remote_module.TmdbApi
import com.example.remote_module.entity.TmdbResults
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.Locale

// Создаем класс MainRepository, который является единой точкой доступа к данным
@OptIn(ExperimentalPagingApi::class)
class MainRepository(
    private val context: Context,
    private val appDatabase: AppDatabase,
    private val tmdbApi: TmdbApi,
    private val preferences: PreferenceProvider
) {

    // Инициализируем DAO для работы с таблицей фильмов
    private val filmDao = appDatabase.filmDao()

    // Используем BehaviorSubject для хранения и передачи последнего состояния загрузки
    private val loadingStatus = BehaviorSubject.createDefault(false)

    // Обновляет текущее состояние загрузки
    fun setLoadingStatus(isLoading: Boolean) = loadingStatus.onNext(isLoading)

    // Предоставляет поток состояния загрузки
    fun getLoadingStatus(): Observable<Boolean> = loadingStatus.hide()

    // Запрашивает список фильмов напрямую из API (RxJava)
    fun getFilmsFromApiRx(category: String): Observable<TmdbResults> {
        return tmdbApi.getFilms(
            category = category,
            apiKey = BuildConfig.TMDB_API_KEY,
            language = preferences.getLanguage(),
            page = 1
        )
    }

    // Получает поток данных PagingData с помощью RemoteMediator
    fun getFilms(category: String): Observable<PagingData<Film>> {
        val filmRemoteMediator = FilmRemoteMediator(
            context = context,
            tmdbApi = tmdbApi,
            appDatabase = appDatabase,
            category = category,
            preferences = preferences
        )
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            remoteMediator = filmRemoteMediator,
            pagingSourceFactory = { filmDao.getFilmsPagingSource(category) }
        ).observable
    }

    // Поиск фильмов через API с поддержкой пагинации
    fun getSearchResult(query: String): Observable<PagingData<Film>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { SearchFilmPagingSource(tmdbApi, query, preferences) }
        ).observable
    }

    // Обновляет данные фильма в БД (или вставляет, если фильма еще нет)
    fun updateFilm(film: Film): Completable = Completable.fromAction { filmDao.insert(film) }

    // Удаляет фильм из локальной БД
    fun deleteFilm(film: Film): Completable = Completable.fromAction { filmDao.delete(film) }

    // Метод для очистки кэша всех фильмов (кроме избранных)
    fun clearCache(): Completable = Completable.fromAction {
        // Очищаем кэш по категориям, чтобы не затронуть избранное и напоминания
        filmDao.deleteUnusedByCategory("popular")
        filmDao.deleteUnusedByCategory("top_rated")
        filmDao.deleteUnusedByCategory("upcoming")
        filmDao.deleteUnusedByCategory("now_playing")
    }

    // Получает список избранных фильмов из БД с поддержкой пагинации
    fun getFavoriteFilmsPaging(): Observable<PagingData<Film>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { filmDao.getFavoriteFilmsPagingSource() }
        ).observable
    }

    // Получает данные конкретного фильма по ID из БД
    fun getFilmById(id: Int): Observable<Film> = filmDao.getFilmById(id)

    // Получает список фильмов для экрана "Посмотреть позже"
    fun getWatchLaterFilmsFromDb(): Observable<List<Film>> = filmDao.getWatchLaterFilms()

    // Метод для получения всех статичных фильмов (Демо-режим). 
    // Мы создаем локализованный контекст на лету, чтобы getString() всегда возвращал правильный язык.
    fun getAllFilmsFromDb(): Observable<List<Film>> {
        return Observable.fromCallable {
            val language = preferences.getLanguage().split("-")[0] // Получаем "ru" или "en"
            val locale = Locale(language)
            val config = Configuration(context.resources.configuration)
            config.setLocale(locale)
            val localizedContext = context.createConfigurationContext(config)

            listOf(
                Film(id = 1, title = localizedContext.getString(R.string.demo_film_1_title), poster = R.drawable.nachalo.toString(), description = localizedContext.getString(R.string.demo_film_1_desc), rating = 9.7),
                Film(id = 2, title = localizedContext.getString(R.string.demo_film_2_title), poster = R.drawable.greshniki.toString(), description = localizedContext.getString(R.string.demo_film_2_desc), rating = 5.7),
                Film(id = 3, title = localizedContext.getString(R.string.demo_film_3_title), poster = R.drawable.homealone.toString(), description = localizedContext.getString(R.string.demo_film_3_desc), rating = 8.7),
                Film(id = 4, title = localizedContext.getString(R.string.demo_film_4_title), poster = R.drawable.podognem.toString(), description = localizedContext.getString(R.string.demo_film_4_desc), rating = 5.3),
                Film(id = 5, title = localizedContext.getString(R.string.demo_film_5_title), poster = R.drawable.substance.toString(), description = localizedContext.getString(R.string.demo_film_5_desc), rating = 6.7),
                Film(id = 6, title = localizedContext.getString(R.string.demo_film_6_title), poster = R.drawable.locked.toString(), description = localizedContext.getString(R.string.demo_film_6_desc), rating = 7.5),
                Film(id = 7, title = localizedContext.getString(R.string.demo_film_7_title), poster = R.drawable.companion.toString(), description = localizedContext.getString(R.string.demo_film_7_desc), rating = 4.7),
                Film(id = 8, title = localizedContext.getString(R.string.demo_film_8_title), poster = R.drawable.ushelie.toString(), description = localizedContext.getString(R.string.demo_film_8_desc), rating = 6.7),
                Film(id = 9, title = localizedContext.getString(R.string.demo_film_9_title), poster = R.drawable.interstellar.toString(), description = localizedContext.getString(R.string.demo_film_9_desc), rating = 8.8),
                Film(id = 10, title = localizedContext.getString(R.string.demo_film_10_title), poster = R.drawable.hishnik.toString(), description = localizedContext.getString(R.string.demo_film_10_desc), rating = 8.3),
                Film(id = 11, title = localizedContext.getString(R.string.demo_film_11_title), poster = R.drawable.topgun.toString(), description = localizedContext.getString(R.string.demo_film_11_desc), rating = 8.7),
                Film(id = 12, title = localizedContext.getString(R.string.demo_film_12_title), poster = R.drawable.indianajones.toString(), description = localizedContext.getString(R.string.demo_film_12_desc), rating = 8.7)
            )
        }
    }
}
