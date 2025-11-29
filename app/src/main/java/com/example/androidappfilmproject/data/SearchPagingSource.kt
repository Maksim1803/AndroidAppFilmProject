package com.example.androidappfilmproject.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.androidappfilmproject.domain.Film
import com.example.androidappfilmproject.utils.Converter
import retrofit2.HttpException
import java.io.IOException

// Создаем класс SearchPagingSource, который является источником данных для Paging 3
// для поиска фильмов.
class SearchPagingSource(
    private val tmdbApi: TmdbApi, // API для работы с сетью
    private val apiKey: String, // Ключ API
    private val language: String, // Язык для запросов
    private val query: String // Поисковый запрос
) : PagingSource<Int, Film>() {

    // Метод для загрузки данных
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Film> {
        val page = params.key ?: 1 // Получаем номер текущей страницы
        return try {
            // Выполняем сетевой запрос для поиска фильмов
            val response = tmdbApi.searchFilms(apiKey, language, query, page)
            val films = Converter.convertApiListToDtoList(response.body()?.tmdbFilms)

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
