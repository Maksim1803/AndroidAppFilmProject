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
class HomeFragmentViewModel @Inject constructor(
    private val interactor: Interactor
) : ViewModel() {

    // Канал для передачи состояния прогресс-бара (подсказка 4)
    private val _showProgressBar = Channel<Boolean>(Channel.CONFLATED)
    val showProgressBar: Flow<Boolean> = _showProgressBar.receiveAsFlow()

    private val category = interactor.getCategoryPreferenceFlow()
    private val query = MutableStateFlow("")

    val films: Flow<PagingData<Film>> = combine(category, query) { category, query ->
        Pair(category, query)
    }.flatMapLatest { (category, query) ->
        if (query.isBlank()) {
            interactor.getPagedFilms(category)
        } else {
            interactor.getSearchResult(query)
        }
    }.cachedIn(viewModelScope)

    fun setQuery(newQuery: String) {
        query.value = newQuery
    }

    fun onFavoriteClicked(film: Film) {
        viewModelScope.launch {
            interactor.toggleFavoriteStatus(film)
        }
    }

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
