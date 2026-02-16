package com.example.androidappfilmproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.rxjava3.cachedIn
import com.example.androidappfilmproject.data.entity.Film
import com.example.androidappfilmproject.domain.Interactor
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

// Создаем класс FavoritesFragmentViewModel, который является ViewModel для FavoritesFragment.
class FavoritesFragmentViewModel @Inject constructor(
    private val interactor: Interactor
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    // Метод для получения избранных фильмов из Interactor в виде Observable.
    val favoriteFilms: Observable<PagingData<Film>> = interactor.getFavoriteFilmsPaging()
        .cachedIn(viewModelScope)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    // Метод для обработки клика по иконке "избранное".
    fun onFavoriteClicked(film: Film) {
        val disposable = interactor.toggleFavoriteStatus(film)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
        compositeDisposable.add(disposable)
    }
   // Метод для очистки ресурсов при уничтожении ViewModel.
    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
