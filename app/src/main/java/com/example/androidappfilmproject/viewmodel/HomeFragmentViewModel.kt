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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

// Создаем класс HomeFragmentViewModel, который является ViewModel для HomeFragment.
@OptIn(ExperimentalCoroutinesApi::class)
class HomeFragmentViewModel(private val interactor: Interactor) : ViewModel() {

    // MutableStateFlow для хранения текущего поискового запроса.
    private val query = MutableStateFlow("")

    // Flow, который предоставляет PagingData с фильмами. Он реагирует на изменения в 'query'.
    val films: Flow<PagingData<Film>> = query.flatMapLatest {
        interactor.getSearchResult(it) // Получаем результаты поиска из Interactor.
    }.cachedIn(viewModelScope) // Кэшируем результаты в ViewModelScope для сохранения при пересоздании Activity.

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
