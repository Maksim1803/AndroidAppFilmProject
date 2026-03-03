package com.example.androidappfilmproject.data

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.rxjava3.observable
import com.example.androidappfilmproject.BuildConfig
import com.example.androidappfilmproject.R
import com.example.database_module.db.AppDatabase
import com.example.database_module.entity.Film
import com.example.remote_module.TmdbApi
import com.example.remote_module.entity.TmdbResults
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

// Создаем класс MainRepository, который является единой точкой доступа к данным
@OptIn(ExperimentalPagingApi::class)
class MainRepository(
    private val context: Context,
    private val appDatabase: AppDatabase,
    private val tmdbApi: TmdbApi
) {

    // Инициализируем DAO для работы с таблицей фильмов
    private val filmDao = appDatabase.filmDao()

    // Используем BehaviorSubject для хранения и передачи последнего состояния загрузки
    private val loadingStatus = BehaviorSubject.createDefault(false)

    // Обновляет текущее состояние загрузки
    fun setLoadingStatus(isLoading: Boolean) = loadingStatus.onNext(isLoading)

    // Предоставляет поток состояния загрузки
    fun getLoadingStatus(): Observable<Boolean> = loadingStatus.hide()

    // Запрашивает список фильмов напрямую из API (RxJava)
    fun getFilmsFromApiRx(category: String): Observable<TmdbResults> {
        return tmdbApi.getFilms(
            category = category,
            apiKey = BuildConfig.TMDB_API_KEY,
            language = "ru-RU",
            page = 1
        )
    }

    // Получает поток данных PagingData с помощью RemoteMediator
    fun getFilms(category: String): Observable<PagingData<Film>> {
        val filmRemoteMediator = FilmRemoteMediator(
            context = context,
            tmdbApi = tmdbApi,
            appDatabase = appDatabase,
            category = category
        )
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            remoteMediator = filmRemoteMediator,
            pagingSourceFactory = { filmDao.getFilmsPagingSource(category) }
        ).observable
    }

    // Поиск фильмов через API с поддержкой пагинации
    fun getSearchResult(query: String): Observable<PagingData<Film>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { SearchFilmPagingSource(tmdbApi, query) }
        ).observable
    }

    // Обновляет данные фильма в БД
    fun updateFilm(film: Film): Completable = Completable.fromAction { filmDao.update(film) }

    // Удаляет фильм из локальной БД
    fun deleteFilm(film: Film): Completable = Completable.fromAction { filmDao.delete(film) }

    // Получает список избранных фильмов из БД с поддержкой пагинации
    fun getFavoriteFilmsPaging(): Observable<PagingData<Film>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { filmDao.getFavoriteFilmsPagingSource() }
        ).observable
    }

    // Получает данные конкретного фильма по ID из БД
    fun getFilmById(id: Int): Observable<Film> = filmDao.getFilmById(id)

    // Метод для получения всех статичных фильмов (Демо-режим)
    fun getAllFilmsFromDb(): Observable<List<Film>> = Observable.just(filmsDataBase)

    // Список фильмов для Демо-режима.
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
