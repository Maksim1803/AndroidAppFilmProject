package com.example.androidappfilmproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidappfilmproject.domain.Film
import com.example.androidappfilmproject.domain.Interactor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

// Создаем класс FavoritesFragmentViewModel, который является ViewModel для FavoritesFragment.
class FavoritesFragmentViewModel(private val interactor: Interactor) : ViewModel() {

    // Flow, который предоставляет список избранных фильмов из Interactor.
    val favoriteFilms: Flow<List<Film>> = interactor.getFavoriteFilms()

    // Метод для обработки клика по иконке "избранное".
    fun onFavoriteClicked(film: Film) {
        // Запускаем корутину для переключения статуса "избранное" у фильма.
        viewModelScope.launch {
            interactor.toggleFavoriteStatus(film)
        }
    }
}