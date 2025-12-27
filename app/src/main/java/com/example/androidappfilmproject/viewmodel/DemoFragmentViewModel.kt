package com.example.androidappfilmproject.viewmodel

import androidx.lifecycle.ViewModel
import com.example.androidappfilmproject.domain.Film
import com.example.androidappfilmproject.domain.Interactor
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// Создаем класс DemoFragmentViewModel, который является ViewModel для DemoFragment.
class DemoFragmentViewModel @Inject constructor(
    interactor: Interactor
) : ViewModel() {
    // Метод для получения всех фильмов из локальной базы данных (для демо-режима).
    val films: Flow<List<Film>> = interactor.getAllFilmsFromDb()
}
