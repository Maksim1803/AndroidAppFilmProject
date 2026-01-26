package com.example.androidappfilmproject.data

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.androidappfilmproject.BuildConfig
import com.example.androidappfilmproject.data.db.AppDatabase
import com.example.androidappfilmproject.data.entity.Film
import com.example.androidappfilmproject.data.entity.toFilm
import com.example.androidappfilmproject.utils.NetworkChecker
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class FilmRemoteMediator(
    context: Context,
    private val tmdbApi: TmdbApi,
    private val appDatabase: AppDatabase,
    private val category: String
) : RemoteMediator<Int, Film>() {

    private val filmDao = appDatabase.filmDao()
    private val networkChecker = NetworkChecker(context)

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Film>): MediatorResult {
        if (!networkChecker.isInternetAvailable()) {
            return MediatorResult.Success(endOfPaginationReached = true)
        }

        return try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    if (lastItem == null) {
                        1
                    } else {
                        (state.pages.sumOf { it.data.size } / state.config.pageSize) + 1
                    }
                }
            }

            val response = tmdbApi.getFilms(
                category = category,
                apiKey = BuildConfig.TMDB_API_KEY,
                language = "ru-RU",
                page = loadKey
            )
            
            // Используем оператор map и метод расширения вместо Converter
            val films = response.body()?.tmdbFilms?.map { it.toFilm() } ?: emptyList()

            appDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    filmDao.deleteAll()
                }
                filmDao.insertAll(films)
            }
            MediatorResult.Success(endOfPaginationReached = films.isEmpty())
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}
