package com.example.androidappfilmproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.androidappfilmproject.domain.Interactor
import kotlinx.coroutines.launch
import javax.inject.Inject

// Класс отвечающий за обработку логики, связанной с выбором категории фильмов.
// Является ViewModel для фрагмента [SelectionsFragment].
// Взаимодействует с [Interactor] для сохранения и получения настроек.
class SelectionsFragmentViewModel @Inject constructor(
    private val interactor: Interactor
) : ViewModel() {

    // LiveData, которая содержит текущую выбранную категорию фильмов.
    // Преобразуется из Flow, предоставляемого Interactor, для удобной работы в UI слое.
    val categoryPropertyLifeData = interactor.getCategoryPreferenceFlow().asLiveData()

    // Метод сохраняет выбранную категорию фильмов в SharedPreferences.
    fun putCategoryProperty(category: String) {
        // Выполняем запись в настройки в скоупе корутины
        viewModelScope.launch {
            interactor.saveDefaultCategoryToPreferences(category)
        }
    }
}
