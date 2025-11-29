package com.example.androidappfilmproject.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.androidappfilmproject.AppDatabase
import com.example.androidappfilmproject.BuildConfig
import com.example.androidappfilmproject.R
import com.example.androidappfilmproject.domain.Film
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

// Создаем класс MainRepository, который является единой точкой доступа к данным
// (из сети или из базы данных).
class MainRepository(private val appDatabase: AppDatabase, private val tmdbApi: TmdbApi) {
    // Метод для получения списка фильмов с использованием Paging 3.
    fun getFilms(): Flow<PagingData<Film>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = {
                FilmPagingSource(
                    tmdbApi = tmdbApi,
                    apiKey = BuildConfig.TMDB_API_KEY,
                    language = "ru-RU"
                )
            }
        ).flow
    }

    // Метод для получения результатов поиска фильмов.
    fun getSearchResult(query: String): Flow<PagingData<Film>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = {
                SearchPagingSource( // Используем PagingSource для поиска фильмов в сети.
                    tmdbApi = tmdbApi,
                    apiKey = BuildConfig.TMDB_API_KEY,
                    language = "ru-RU",
                    query = query
                )
            }
        ).flow
    }

    // Метод для переключения статуса "избранное" у фильма.
    suspend fun toggleFavoriteStatus(film: Film) {
        val updatedFilm = film.copy(isInFavorites = !film.isInFavorites)
    // Вставляем обновленный фильм в базу данных.
        appDatabase.filmDao().insert(updatedFilm)
    }

    // Метод для получения избранных фильмов с использованием Paging 3.
    fun getFavoriteFilmsPaging(): Flow<PagingData<Film>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { appDatabase.filmDao().getFavoriteFilmsPagingSource() } // Источник данных - база данных.
        ).flow
    }

    // Метод для получения фильма по его ID.
    fun getFilmById(id: Int): Flow<Film> {
        return appDatabase.filmDao().getFilmById(id)
    }

    // Метод для получения всех фильмов из локальной базы данных (для демонстрации).
    fun getAllFilmsFromDb(): Flow<List<Film>> {
        return flow { emit(filmsDataBase) }
    }
    // Список фильмов из приложения для вкладки "Демо" (локальная БД)
    private val filmsDataBase: List<Film> = listOf(
        Film(
            id = 1,
            title = "Начало",
            poster = R.drawable.nachalo.toString(),
            description = "Вору, который крадёт секреты с помощью технологии обмена снами, поручают задачу: внедрить идею в сознание гендиректора, но его трагическое прошлое может привести всё к катастрофе.",
            rating = 9.7,
            isInFavorites = false
        ),
        Film(
            id = 2,
            title = "Грешники",
            poster = R.drawable.greshniki.toString(),
            description = "Пытаясь оставить позади свою неспокойную жизнь, братья-близнецы возвращаются в родной город, и убеждаются, что здесь их ждёт ещё большее зло.",
            rating = 5.7,
            isInFavorites = false
        ),
        Film(
            id = 3,
            title = "Один дома",
            poster = R.drawable.homealone.toString(),
            description = "Американское семейство отправляется из Чикаго в Европу, но в спешке сборов бестолковые родители забывают дома одного из своих детей. Юное создание, однако, не теряется и демонстрирует чудеса изобретательности...",
            rating = 8.7,
            isInFavorites = false
        ),
        Film(
            id = 4,
            title = "Под огнем",
            poster = R.drawable.podognem.toString(),
            description = "Взвод «морских котиков» отправляется на опасную миссию в Ирак, и рассказывает о хаосе и братстве на войне, вспоминая об этом событии.",
            rating = 5.3,
            isInFavorites = false
        ),
        Film(
            id = 5,
            title = "Субстанция",
            poster = R.drawable.substance.toString(),
            description = "Увядающая знаменитость принимает препарат с чёрного рынка, который воспроизводит клетки и создаёт более молодую и привлекательную версию самой себя.",
            rating = 6.7,
            isInFavorites = false
        ),
        Film(
            id = 6,
            title = "Запертый",
            poster = R.drawable.locked.toString(),
            description = "Вор, забравшийся в роскошный внедорожник, понимает, что попал в изощрённую игру в психологический хоррор.",
            rating = 7.5,
            isInFavorites = false
        ),
        Film(
            id = 7,
            title = "Компаньон",
            poster = R.drawable.companion.toString(),
            description = "Отдых на выходных с друзьями превращается в хаос после того, как выясняется, что один из гостей не тот, за кого себя выдает.",
            rating = 4.7,
            isInFavorites = false
        ),
        Film(
            id = 8,
            title = "Ущелье",
            poster = R.drawable.ushelie.toString(),
            description = "Когда появляется зло, они должны действовать сообща, чтобы выжить и противостоять тому, что внутри.",
            rating = 6.7,
            isInFavorites = false
        ),
        Film(
            id = 9,
            title = "Интерстеллар",
            poster = R.drawable.interstellar.toString(),
            description = "Когда человечество своим образом жизни пришло к продовольственному кризису, коллектив учёных отправляется сквозь черную дыру (которая соединяет области пространства-времени через большое расстояние) в путешествие, чтобы найти планету с подходящими для человечества условиями жизни.",
            rating = 8.8,
            isInFavorites = false
        ),
        Film(
            id = 10,
            title = "Хищник",
            poster = R.drawable.hishnik.toString(),
            description = "Для освобождения американских граждан элитная группа спецназа направлена в джунгли. Там, видавшим многое бойцам, предстоит встретиться с невероятным ужасом.",
            rating = 8.3,
            isInFavorites = false
        ),
        Film(
            id = 11,
            title = "Топган",
            poster = R.drawable.topgun.toString(),
            description = "Очень крутой боевик с Томом Крузом в главной роли. ",
            rating = 8.7,
            isInFavorites = false
        ),
        Film(
            id = 12,
            title = "Анора",
            poster = R.drawable.anora.toString(),
            description = "Молодая стриптизерша из Бруклина знакомится с сыном олигарха и выходит за него замуж. Как только новость доходит до его родителей, её сказке приходит конец.",
            rating = 5.5,
            isInFavorites = false
        ),
        Film(
            id = 13,
            title = "Индиана Джонс",
            poster = R.drawable.indianajones.toString(),
            description = "Крутой, можно сказать христоматийный приключенческий боевик",
            rating = 8.7,
            isInFavorites = false
        )
    )
}
