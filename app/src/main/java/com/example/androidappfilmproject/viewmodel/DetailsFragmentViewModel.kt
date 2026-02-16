package com.example.androidappfilmproject.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import com.example.androidappfilmproject.data.entity.Film
import com.example.androidappfilmproject.domain.Interactor
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.net.URL
import javax.inject.Inject

// Создаем класс DetailsFragmentViewModel, который является ViewModel для DetailsFragment.
class DetailsFragmentViewModel @Inject constructor(
    private val interactor: Interactor
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    // Метод для получения фильма по ID в виде Observable.
    fun getFilmById(id: Int): Observable<Film> {
        return interactor.getFilmById(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    // Метод для обработки клика по иконке "избранное".
    fun onFavoriteClicked(film: Film) {
        val disposable = interactor.toggleFavoriteStatus(film)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
        compositeDisposable.add(disposable)
    }

    // Метод для загрузки картинки (постера) по URL с использованием Single.
    // RxJava 3 не допускает null, поэтому возвращаем Single<Bitmap> и кидаем ошибку,
    // если загрузка не удалась.
    fun loadWallpaper(url: String): Single<Bitmap> {
        return Single.fromCallable {
            val urlObj = URL(url)
            val connection = urlObj.openConnection()
            connection.connectTimeout = 60000
            connection.readTimeout = 60000
            val inputStream = connection.getInputStream()
            BitmapFactory.decodeStream(inputStream) ?: throw Exception("Failed to decode bitmap")
        }.subscribeOn(Schedulers.io())
         .observeOn(AndroidSchedulers.mainThread())
    }
    // Метод для очистки ресурсов при уничтожении ViewModel.
    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
