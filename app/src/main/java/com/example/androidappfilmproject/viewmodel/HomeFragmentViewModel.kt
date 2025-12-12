package com.example.androidappfilmproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.androidappfilmproject.App
import com.example.androidappfilmproject.domain.Film
import com.example.androidappfilmproject.domain.FilmInteractor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
// Создаем класс HomeFragmentViewModel, который является ViewModel для HomeFragment.
class HomeFragmentViewModel() : ViewModel() {

    // Поле для FilmInteractor, который будет предоставлен Dagger.
    @Inject
    lateinit var interactor: FilmInteractor

    // MutableStateFlow для хранения текущего поискового запроса.
    private val query = MutableStateFlow("")

    // Flow, который будет содержать пагинированный список фильмов для отображения.
    val films: Flow<PagingData<Film>>

    init {
        // Инициируем Dagger для внедрения зависимостей.
        App.instance.dagger.inject(this)
        // flatMapLatest используется для того, чтобы при изменении query Flow автоматически переключался на новый Flow от getSearchResult.
        films = query.flatMapLatest {
            interactor.getSearchResult(it)
        }.cachedIn(viewModelScope) // cachedIn кеширует данные, чтобы они не запрашивались заново при пересоздании Activity/Fragment.
    }

    // Метод для установки нового поискового запроса.
    fun setQuery(newQuery: String) {
        query.value = newQuery
    }

    // Метод для обработки клика по иконке "избранное".
    fun onFavoriteClicked(film: Film) {
        // Запускаем корутину для переключения статуса "избранное" у фильма.
        viewModelScope.launch {
            interactor.toggleFavoriteStatus(film)
        }
    }
}
