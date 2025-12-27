package com.example.androidappfilmproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.androidappfilmproject.domain.Film
import com.example.androidappfilmproject.domain.Interactor
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

    // Flow для хранения текущей категории
    private val category = interactor.getCategoryPreferenceFlow()
    // Flow для хранения поискового запроса
    private val query = MutableStateFlow("")

    // Главный Flow, который будет содержать пагинированный список фильмов
    // combine объединяет два потока (категорию и поиск). Как только в любом из них
    // появляется новое значение, он срабатывает.
    val films: Flow<PagingData<Film>> = combine(category, query) { category, query ->
        // Создаем пару, чтобы передать оба значения дальше
        Pair(category, query)
    }.flatMapLatest { (category, query) ->
        // Если поиск пустой - загружаем пагинированный список для выбранной категории
        if (query.isBlank()) {
            interactor.getPagedFilms(category)
        } else {
        // Если поиск не пустой - используем поисковый метод (нужно будет добавить его в Interactor)
            interactor.getSearchResult(query)
        }
    // Кешируем данные
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
}
