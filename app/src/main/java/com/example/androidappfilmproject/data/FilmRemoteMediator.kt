package com.example.androidappfilmproject.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.androidappfilmproject.AppDatabase
import com.example.androidappfilmproject.domain.Film
import com.example.androidappfilmproject.utils.Converter
import retrofit2.HttpException
import java.io.IOException

// Создаем класс FilmRemoteMediator, который является посредником между Paging Library, сетью и базой данных.
@OptIn(ExperimentalPagingApi::class)
class FilmRemoteMediator(
    private val tmdbApi: TmdbApi, // API для работы с сетью
    private val appDatabase: AppDatabase, // База данных для кэширования данных
    private val apiKey: String, // Ключ API
    private val language: String // Язык для запросов
) : RemoteMediator<Int, Film>() {

    private val filmDao = appDatabase.filmDao() // Получаем DAO для работы с таблицей фильмов

    // Метод для загрузки данных
    override suspend fun load(loadType: LoadType, state: PagingState<Int, Film>): MediatorResult {
        return try {
            // Определяем, какую страницу загружать, в зависимости от типа загрузки
            val loadKey = when (loadType) {
                LoadType.REFRESH -> 1 // При обновлении всегда загружаем первую страницу
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true) // В этом приложении не используется
                LoadType.APPEND -> { // При дозагрузке
                    val lastItem = state.lastItemOrNull()
                    if (lastItem == null) {
                        1 // Если нет элементов, загружаем первую страницу
                    } else {
                        (state.pages.size) + 1 // Иначе загружаем следующую страницу
                    }
                }
            }

            // Выполняем сетевой запрос
            val response = tmdbApi.getFilms(apiKey, language, loadKey)
            val films = Converter.convertApiListToDtoList(response.body()?.tmdbFilms)

            // Выполняем транзакцию в базе данных
            appDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    filmDao.deleteAll() // При обновлении очищаем таблицу
                }
                filmDao.insertAll(films) // Вставляем новые фильмы
            }

            // Возвращаем результат
            MediatorResult.Success(endOfPaginationReached = films.isEmpty())
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}
