package com.example.androidappfilmproject.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidappfilmproject.data.entity.Film
import com.example.androidappfilmproject.domain.Interactor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import javax.inject.Inject

// Создаем класс DetailsFragmentViewModel, который является ViewModel для DetailsFragment.
class DetailsFragmentViewModel @Inject constructor(
    private val interactor: Interactor
) : ViewModel() {

    // Метод для получения фильма по ID.
    fun getFilmById(id: Int): Flow<Film?> {
        return interactor.getFilmById(id)
    }

    // Метод для обработки клика по иконке "избранное".
    fun onFavoriteClicked(film: Film) {
        viewModelScope.launch {
            interactor.toggleFavoriteStatus(film)
        }
    }

    // Метод для загрузки картинки (постера) по URL.
    // Используем withContext(Dispatchers.IO) для переключения на фоновый поток.
    suspend fun loadWallpaper(url: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val urlObj = URL(url)
            val connection = urlObj.openConnection()
            // Делаем таймауты до 10 сек., т.к. постеры в "original" качестве могут быть тяжелыми.
            connection.connectTimeout = 60000
            connection.readTimeout = 60000
            val inputStream = connection.getInputStream()
            // Декодируем поток в Bitmap
            BitmapFactory.decodeStream(inputStream)
        } catch (_: Exception) {
            // В случае ошибки (например, нет интернета или таймаут) возвращаем null.
            null
        }
    }
}
