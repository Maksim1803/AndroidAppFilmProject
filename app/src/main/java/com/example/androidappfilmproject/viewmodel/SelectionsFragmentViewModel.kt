package com.example.androidappfilmproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.toLiveData
import com.example.androidappfilmproject.domain.Interactor
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

// Класс отвечающий за обработку логики, связанной с выбором категории фильмов.
// Является ViewModel для фрагмента [SelectionsFragment].
// Взаимодействует с [Interactor] для сохранения и получения настроек.
class SelectionsFragmentViewModel @Inject constructor(
    private val interactor: Interactor
) : ViewModel() {

    // LiveData, которая содержит текущую выбранную категорию фильмов.
    // Используем Observable напрямую из интерактора и преобразуем в LiveData.
    val categoryPropertyLifeData: LiveData<String> = interactor.getCategoryPreferenceObservable()
        .toFlowable(BackpressureStrategy.LATEST)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .toLiveData()

    // Метод сохраняет выбранную категорию фильмов в SharedPreferences.
    fun putCategoryProperty(category: String) {
        interactor.saveDefaultCategoryToPreferences(category)
    }
}

