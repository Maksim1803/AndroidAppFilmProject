package com.example.androidappfilmproject.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.androidappfilmproject.BuildConfig
import com.example.androidappfilmproject.data.entity.Film
import com.example.androidappfilmproject.data.entity.toFilm
import retrofit2.HttpException
import java.io.IOException

// Создаем класс SearchFilmPagingSource, который является источником данных для Paging 3
// для поиска фильмов.
class SearchFilmPagingSource(
    private val tmdbApi: TmdbApi, // API для работы с сетью
    private val query: String // Поисковый запрос
) : PagingSource<Int, Film>() {

    // Метод для загрузки данных
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Film> {
        val page = params.key ?: 1

        return try {
            val results = tmdbApi.searchFilms(
                apiKey = BuildConfig.TMDB_API_KEY,
                language = "ru-RU",
                query = query,
                page = page
            )

            // Преобразуем DTO модели из сети в Entity модели для БД
            val films = results.tmdbFilms.map { it.toFilm() }

            // Возвращаем страницу с данными
            LoadResult.Page(
                data = films,
                // Ключ для предыдущей страницы
                prevKey = if (page == 1) null else page - 1,
                // Ключ для следующей страницы
                nextKey = if (films.isEmpty() || page >= results.totalPages) null else page + 1
            )

            // Обрабатываем ошибки
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    // Метод для определения ключа обновления
    override fun getRefreshKey(state: PagingState<Int, Film>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}