package com.example.androidappfilmproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.androidappfilmproject.domain.Film
import com.example.androidappfilmproject.domain.Interactor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Создаем класс FavoritesFragmentViewModel, который является ViewModel для FavoritesFragment.
class FavoritesFragmentViewModel @Inject constructor(
    private val interactor: Interactor
) : ViewModel() {

    // Метод для получения избранных фильмов из Interactor.
    val favoriteFilms: Flow<PagingData<Film>> = interactor.getFavoriteFilmsPaging().cachedIn(viewModelScope)

    // Метод для обработки клика по иконке "избранное".
    fun onFavoriteClicked(film: Film) {
        // Запускаем корутину для переключения статуса "избранное" у фильма.
        viewModelScope.launch {
            interactor.toggleFavoriteStatus(film)
        }
    }
}
