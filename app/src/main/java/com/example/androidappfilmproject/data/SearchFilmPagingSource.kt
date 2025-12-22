package com.example.androidappfilmproject.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.androidappfilmproject.BuildConfig
import com.example.androidappfilmproject.domain.Film
import retrofit2.HttpException
import java.io.IOException

class SearchFilmPagingSource(
    private val tmdbApi: TmdbApi,
    private val query: String
) : PagingSource<Int, Film>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Film> {
        val page = params.key ?: 1
        return try {
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

            LoadResult.Page(
                data = films,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (films.isEmpty()) null else page + 1
            )
        } catch (e: IOException) {
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Film>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
