package com.example.androidappfilmproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.androidappfilmproject.data.entity.Film
import com.example.androidappfilmproject.domain.Interactor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
// Создаем класс HomeFragmentViewModel, который является ViewModel для HomeFragment.
class HomeFragmentViewModel @Inject constructor(
    private val interactor: Interactor
) : ViewModel() {

    // Канал для передачи состояния прогресс-бара (подсказка 4)
    private val _showProgressBar = Channel<Boolean>(Channel.CONFLATED)
    val showProgressBar: Flow<Boolean> = _showProgressBar.receiveAsFlow()

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

    // Метод для обновления состояния прогресс-бара
    fun toggleProgressBar(isVisible: Boolean) {
        viewModelScope.launch {
            _showProgressBar.send(isVisible)
        }
    }
}
