package com.example.androidappfilmproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidappfilmproject.domain.Film
import com.example.androidappfilmproject.domain.Interactor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Изменяем конструктор, чтобы он принимал наш новый единый Interactor и был готов к внедрению Dagger
class DetailsFragmentViewModel @Inject constructor(
    private val interactor: Interactor
) : ViewModel() {

    // Метод для получения фильма по ID.
    fun getFilmById(id: Int): Flow<Film> {
        return interactor.getFilmById(id)
    }

    // Метод для обработки клика по иконке "избранное".
    fun onFavoriteClicked(film: Film) {
        viewModelScope.launch {
            interactor.toggleFavoriteStatus(film)
        }
    }
}
