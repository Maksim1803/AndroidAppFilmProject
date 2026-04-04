package com.example.androidappfilmproject.data

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.androidappfilmproject.BuildConfig
import com.example.database_module.db.AppDatabase
import com.example.database_module.entity.Film
import com.example.androidappfilmproject.data.entity.toFilm
import com.example.androidappfilmproject.utils.NetworkChecker
import com.example.remote_module.TmdbApi
import kotlinx.coroutines.rx3.awaitSingle
import java.io.IOException

// Создаем класс для синхронизации данных из сети в локальную БД
@OptIn(ExperimentalPagingApi::class)
class FilmRemoteMediator(
    context: Context,
    private val tmdbApi: TmdbApi,
    private val appDatabase: AppDatabase,
    private val category: String
) : RemoteMediator<Int, Film>() {

    // Инициализируем DAO через базу данных из database_module
    private val filmDao = appDatabase.filmDao()

    // Инициализируем NetworkChecker для проверки состояния сети
    private val networkChecker = NetworkChecker(context)

    // Метод для управления загрузкой данных при скролле или обновлении списка
    override suspend fun load(loadType: LoadType, state: PagingState<Int, Film>): MediatorResult {
        // Проверка интернета
        if (!networkChecker.isInternetAvailable()) {
            return MediatorResult.Error(IOException("Нет подключения к интернету"))
        }
        // Основной блок обработки сетевого запроса и кэширования
        return try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    if (lastItem == null) 1 else (state.pages.sumOf { it.data.size } / state.config.pageSize) + 1
                }
            }

            // Выполняем сетевой запрос к TMDB API через RxJava
            val response = tmdbApi.getFilms(
                category = category,
                apiKey = BuildConfig.TMDB_API_KEY,
                language = "ru-RU",
                page = loadKey
            ).awaitSingle()

            // Только если данные успешно получены, работаем с БД
            appDatabase.withTransaction {
                // Получаем текущие фильмы из БД, которые помечены как "Посмотреть позже" или "Избранное"
                // Это нужно, чтобы при обновлении списка (REFRESH) не затереть их статус.
                
                // В данном случае мы просто будем проверять ID при вставке.
                // Но проблема в том, что deleteByCategory удаляет записи.
                
                if (loadType == LoadType.REFRESH) {
                    // Очищаем только те фильмы, которые НЕ в избранном И НЕ в списке "Посмотреть позже"
                    filmDao.deleteUnusedByCategory(category)
                }

                // Получаем списки ID для сохранения статусов
                val favoriteIds = filmDao.getFavoriteIds()
                val watchLaterFilms = filmDao.getWatchLaterFilmsSync()

                // Преобразуем DTO модели из сети в Entity модели для БД и назначаем категорию
                val films = response.tmdbFilms.map { tmdbFilm ->
                    tmdbFilm.toFilm().apply {
                        this.category = this@FilmRemoteMediator.category
                        
                        // Сохраняем статус "Избранное"
                        if (favoriteIds.contains(this.id)) {
                            this.isInFavorites = true
                        }
                        
                        // Сохраняем статус "Посмотреть позже" и время
                        watchLaterFilms.find { it.id == this.id }?.let { savedFilm ->
                            this.isInWatchLater = true
                            this.watchLaterTime = savedFilm.watchLaterTime
                        }
                    }
                }

                // Сохраняем свежезагруженные фильмы в локальное хранилище
                filmDao.insertAll(films)
            }

            // Возвращаем результат: Success, указывая, достигнут ли конец списка
            MediatorResult.Success(endOfPaginationReached = response.tmdbFilms.isEmpty())
        } catch (e: Exception) {
            // Любая ошибка (timeout, 401, и т.д.) пробрасывается в UI
            MediatorResult.Error(e)
        }
    }
}
