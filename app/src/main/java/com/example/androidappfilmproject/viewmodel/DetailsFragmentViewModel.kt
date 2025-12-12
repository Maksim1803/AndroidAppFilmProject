package com.example.androidappfilmproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidappfilmproject.domain.Film
import com.example.androidappfilmproject.domain.FilmInteractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

// Создаем класс DetailsFragmentViewModel, который является ViewModel для DetailsFragment.
class DetailsFragmentViewModel(private val interactor: FilmInteractor) : ViewModel() {

    // Метод для получения фильма по ID.
    fun getFilmById(id: Int): Flow<Film> {
        return interactor.getFilmById(id)
    }

    // Метод для обработки клика по иконке \"избранное\".
    fun onFavoriteClicked(film: Film) {
        // Запускаем корутину для переключения статуса \"избранное\" у фильма.
        viewModelScope.launch {
            interactor.toggleFavoriteStatus(film)
        }
    }
}
