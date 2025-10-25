package com.example.androidappfilmproject.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.androidappfilmproject.domain.Film
import com.example.androidappfilmproject.domain.Interactor

class HomeFragmentViewModel(interactor: Interactor) : ViewModel() {

    val filmsListLiveData = MutableLiveData<List<Film>>()

    init {

        val films = interactor.getFilmsDB()
        filmsListLiveData.postValue(films)
    }
}