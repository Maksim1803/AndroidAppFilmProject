package com.example.androidappfilmproject.viewmodel

import androidx.lifecycle.ViewModel
import com.example.androidappfilmproject.domain.Film
import com.example.androidappfilmproject.domain.Interactor
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// Изменяем конструктор, чтобы он принимал единый Interactor и был готов к внедрению Dagger
class DemoFragmentViewModel @Inject constructor(
    interactor: Interactor
) : ViewModel() {
    // Метод для получения всех фильмов из локальной базы данных (для демо-режима).
    val films: Flow<List<Film>> = interactor.getAllFilmsFromDb()
}
