package com.example.androidappfilmproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.androidappfilmproject.domain.Interactor
import kotlinx.coroutines.launch
import javax.inject.Inject

class SelectionsFragmentViewModel @Inject constructor(
    private val interactor: Interactor
) : ViewModel() {

    val categoryPropertyLifeData = interactor.getCategoryPreferenceFlow().asLiveData()

    fun putCategoryProperty(category: String) {
        // Выполняем запись в настройки в скоупе корутины
        viewModelScope.launch {
            interactor.saveDefaultCategoryToPreferences(category)
        }
    }
}
