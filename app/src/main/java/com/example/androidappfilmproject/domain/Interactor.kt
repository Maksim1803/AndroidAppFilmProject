package com.example.androidappfilmproject.domain

import androidx.paging.PagingData
import com.example.androidappfilmproject.data.MainRepository
import com.example.androidappfilmproject.data.entity.toFilm
import com.example.androidappfilmproject.data.preferences.PreferenceProvider
import com.example.database_module.entity.Film
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

// Класс представляет собой набор методов для взаимодействия с данными.
class Interactor(
    private val repo: MainRepository,
    private val preferences: PreferenceProvider
) {
    // Поле для получения текущего статуса загрузки из репозитория
    val loadingStatus: Observable<Boolean> = repo.getLoadingStatus()

    // Метод для установки или изменения флага состояния загрузки
    fun setLoadingStatus(isLoading: Boolean) = repo.setLoadingStatus(isLoading)

    // Метод для получения реактивного потока выбранной категории из настроек
    fun getCategoryPreferenceObservable(): Observable<String> = preferences.getCategoryObservable()

    // Метод для сохранения выбранной пользователем категории в SharedPreferences
    fun saveDefaultCategoryToPreferences(category: String) {
        preferences.saveDefaultCategory(category)
    }

    // Метод для получения списка рекомендованных фильмов из API с маппингом данных
    fun getRecommendation(category: String): Observable<List<Film>> {
        return repo.getFilmsFromApiRx(category)
            .map { tmdbResults ->
                tmdbResults.tmdbFilms.map { it.toFilm().apply { this.category = category } }
            }
    }

    // Метод для получения пагинированного потока фильмов по категориям
    fun getPagedFilms(category: String): Observable<PagingData<Film>> = repo.getFilms(category)

    // Метод для получения результатов поиска через PagingData
    fun getSearchResult(query: String): Observable<PagingData<Film>> = repo.getSearchResult(query)

    // Метод для получения всех сохраненных в локальной БД фильмов в виде списка
    fun getAllFilmsFromDb(): Observable<List<Film>> = repo.getAllFilmsFromDb()

    // Метод для получения пагинированного списка только избранных фильмов
    fun getFavoriteFilmsPaging(): Observable<PagingData<Film>> = repo.getFavoriteFilmsPaging()

    // Метод для получения детальной информации о конкретном фильме по его ID
    fun getFilmById(id: Int): Observable<Film> = repo.getFilmById(id)

    // Метод для обновления данных фильма (включая статус "избранное") в репозитории
    fun toggleFavoriteStatus(film: Film): Completable {
        return repo.updateFilm(film)
    }

    // Метод для получения списка фильмов "Посмотреть позже"
    fun getWatchLaterFilms(): Observable<List<Film>> = repo.getWatchLaterFilmsFromDb()

    // Метод для обновления фильма в БД
    fun updateFilmInDb(film: Film): Completable = repo.updateFilm(film)

    // Метод для удаления конкретного фильма из локального кэша (базы данных)
    fun removeFilmFromCache(film: Film): Completable = repo.deleteFilm(film)

    // Метод для очистки кэша
    fun clearCache(): Completable = repo.clearCache()

    // Метод для сохранения языка
    fun saveLanguage(language: String) {
        preferences.saveLanguage(language)
    }
}