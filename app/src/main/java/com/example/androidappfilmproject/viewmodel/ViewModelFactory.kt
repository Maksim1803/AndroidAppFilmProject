package com.example.androidappfilmproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider

// Класс для создания экземпляров ViewModel.
// Использует Dagger для получения провайдеров ViewModel и их создания.
// Это позволяет внедрять зависимости в ViewModel.
class ViewModelFactory @Inject constructor(
    // Map, где ключ - это класс ViewModel, а значение - провайдер для создания этого ViewModel.
    private val viewModelProviders: @JvmSuppressWildcards Map<Class<out ViewModel>, Provider<ViewModel>>
) : ViewModelProvider.Factory {

    // Создает и возвращает экземпляр ViewModel для указанного класса.
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val provider = viewModelProviders[modelClass]
            ?: throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
        @Suppress("UNCHECKED_CAST")
        return provider.get() as T
    }
}
