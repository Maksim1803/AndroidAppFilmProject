package com.example.androidappfilmproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.androidappfilmproject.data.entity.Film
import com.example.androidappfilmproject.domain.Interactor
import com.example.androidappfilmproject.utils.SingleLiveEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

// Создаем класс HomeFragmentViewModel, который является ViewModel для HomeFragment.
@OptIn(ExperimentalCoroutinesApi::class)
class HomeFragmentViewModel @Inject constructor(
    private val interactor: Interactor
) : ViewModel() {

    // LiveData для списка фильмов из БД (Модуль 41)
    // Используется как альтернативный способ получения данных (всех сразу),
    // в то время как переменная 'films' ниже реализует постраничную загрузку (Paging).
    val filmsListLiveData: LiveData<List<Film>> = interactor.getFilmsFromDB()

    // LiveData для отображения прогресс-бара (Модуль 41)
    val showProgressBar: MutableLiveData<Boolean> = MutableLiveData()

    // SingleLiveEvent для отображения ошибок (Модуль 41)
    val errorEvent: SingleLiveEvent<String> = SingleLiveEvent()

    // Flow для хранения текущей категории
    private val category = interactor.getCategoryPreferenceFlow()

    // Flow для хранения поискового запроса
    private val query = MutableStateFlow("")

    // Главный Flow для пагинации
    val films: Flow<PagingData<Film>> = combine(category, query) { category, query ->
        Pair(category, query)
    }.flatMapLatest { (category, query) ->
        if (query.isBlank()) {
            interactor.getPagedFilms(category)
        } else {
            interactor.getSearchResult(query)
        }
    }.cachedIn(viewModelScope)

    // Метод для установки нового поискового запроса
    fun setQuery(newQuery: String) {
        query.value = newQuery
    }

    // Метод для обработки клика по иконке "избранное".
    fun onFavoriteClicked(film: Film) {
        viewModelScope.launch {
            interactor.toggleFavoriteStatus(film)
        }
    }

    // Метод для удаления фильма из кэша.
    fun removeFilmFromCache(film: Film) {
        viewModelScope.launch {
            interactor.removeFilmFromCache(film)
        }
    }
}
