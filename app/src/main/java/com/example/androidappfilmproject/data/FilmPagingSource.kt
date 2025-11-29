package com.example.androidappfilmproject.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.androidappfilmproject.domain.Film
import retrofit2.HttpException
import java.io.IOException

// Создаем класс FilmPagingSource, который является источником данных для Paging 3,
// загружая фильмы из TmdbApi.
class FilmPagingSource(
    private val tmdbApi: TmdbApi,
    private val apiKey: String,
    private val language: String
) : PagingSource<Int, Film>() {

    // Метод для загрузки данных для пагинации.
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Film> {
        val page = params.key ?: 1 // Получаем номер текущей страницы или 1, если это первая загрузка.
        return try {
            val response = tmdbApi.getFilms(apiKey, language, page) // Выполняем сетевой запрос.
            val films = response.body()?.tmdbFilms?.map { // Преобразуем полученные данные в список объектов Film.
                Film(
                    title = it.title,
                    poster = it.posterPath,
                    description = it.overview,
                    rating = it.voteAverage,
                    id = it.id
                )
            } ?: emptyList()

            // Возвращаем страницу с данными.
            LoadResult.Page(
                data = films,
                prevKey = if (page == 1) null else page - 1, // Ключ для предыдущей страницы.
                nextKey = if (films.isEmpty()) null else page + 1 // Ключ для следующей страницы.
            )
        } catch (e: IOException) {
            return LoadResult.Error(e) // Возвращаем ошибку в случае проблем с сетью.
        } catch (e: HttpException) {
            return LoadResult.Error(e) // Возвращаем ошибку в случае проблем с HTTP.
        }
    }

    // Метод для получения ключа для обновления данных.
    override fun getRefreshKey(state: PagingState<Int, Film>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
