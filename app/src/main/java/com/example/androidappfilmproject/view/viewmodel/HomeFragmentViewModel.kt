package com.example.androidappfilmproject.view.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.androidappfilmproject.domain.Film
import com.example.androidappfilmproject.domain.Interactor

class HomeFragmentViewModel: ViewModel() {
    val filmsListLiveData = MutableLiveData<List<Film>>()
    private lateinit var interactor: Interactor
    init {
        //TODO init interactor

        val films = interactor.getFilmsDB()
        filmsListLiveData.postValue(films)
    }
}

