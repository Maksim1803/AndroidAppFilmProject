package com.example.androidappfilmproject.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.androidappfilmproject.BuildConfig
import com.example.androidappfilmproject.data.preferences.PreferenceProvider
import com.example.database_module.entity.Film
import com.example.androidappfilmproject.data.entity.toFilm
import com.example.remote_module.TmdbApi
import kotlinx.coroutines.rx3.awaitSingle
import retrofit2.HttpException
import java.io.IOException

// Класс - источник данных для поиска. Теперь использует Film из database_module
class SearchFilmPagingSource(
    private val tmdbApi: TmdbApi,
    private val query: String,
    private val preferences: PreferenceProvider
) : PagingSource<Int, Film>() {

    // Метод для загрузки порции данных (страницы) по поисковому запросу
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Film> {
        val page = params.key ?: 1

        return try {
            val results = tmdbApi.searchFilms(
                apiKey = BuildConfig.TMDB_API_KEY,
                language = preferences.getLanguage(),
                query = query,
                page = page
            ).awaitSingle()

            // Мапим результаты в нашу сущность Film
            val films = results.tmdbFilms.map { it.toFilm() }

            // Метод для формирования успешного результата с ключами соседних страниц
            LoadResult.Page(
                data = films,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (films.isEmpty() || page >= results.totalPages) null else page + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    // Метод для определения ключа (номера страницы) при обновлении данных (инвалидации)
    override fun getRefreshKey(state: PagingState<Int, Film>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
