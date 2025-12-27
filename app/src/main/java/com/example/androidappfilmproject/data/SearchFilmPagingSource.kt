package com.example.androidappfilmproject.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.androidappfilmproject.BuildConfig
import com.example.androidappfilmproject.domain.Film
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
            // Выполняем сетевой запрос для поиска фильмов
            val response = tmdbApi.searchFilms(
                apiKey = BuildConfig.TMDB_API_KEY,
                language = "ru-RU",
                query = query,
                page = page
            )
            val films = response.body()?.tmdbFilms?.map { 
                Film(
                    title = it.title,
                    poster = it.posterPath,
                    description = it.overview,
                    rating = it.voteAverage,
                    id = it.id
                )
            } ?: emptyList()
            // Возвращаем страницу с данными
            LoadResult.Page(
                data = films,
                prevKey = if (page == 1) null else page - 1, // Ключ для предыдущей страницы
                nextKey = if (films.isEmpty()) null else page + 1 // Ключ для следующей страницы
            )
        } catch (e: IOException) {
            return LoadResult.Error(e) // Возвращаем ошибку в случае проблем с сетью
        } catch (e: HttpException) {
            return LoadResult.Error(e) // Возвращаем ошибку в случае проблем с HTTP
        }
    }
    // Метод для получения ключа для обновления данных
    override fun getRefreshKey(state: PagingState<Int, Film>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
