package com.example.androidappfilmproject.domain

import com.example.androidappfilmproject.data.MainRepository

class Interactor(val repo: MainRepository) {
    fun getFilmsDB(): List<Film> = repo.filmsDataBase
}