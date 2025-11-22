package com.example.androidappfilmproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.androidappfilmproject.domain.Film
import com.example.androidappfilmproject.domain.Interactor
import kotlinx.coroutines.flow.Flow

// Создаем класс FavoritesFragmentViewModel, который является ViewModel для FavoritesFragment.
class FavoritesFragmentViewModel(interactor: Interactor) : ViewModel() {

    // Flow, который предоставляет PagingData с избранными фильмами из Interactor.
    val favoriteFilms: Flow<PagingData<Film>> = interactor.getFavoriteFilmsPaging().cachedIn(viewModelScope)
}
