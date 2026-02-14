package com.example.androidappfilmproject.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

// Класс для работы с SharedPreferences, адаптированный под RxJava
class PreferenceProvider(context: Context) {

    // Инициализируем Context
    private val appContext = context.applicationContext

    // Инициализируем SharedPreferences
    private val preference: SharedPreferences =
        appContext.getSharedPreferences("selections", Context.MODE_PRIVATE)

    // BehaviorSubject для хранения текущей категории. Сразу инициализируем текущим значением.
    private val categorySubject = BehaviorSubject.createDefault(getDefaultCategory())

    // Храним слушателя как жесткую ссылку, чтобы его не удалил Garbage Collector
    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == KEY_DEFAULT_CATEGORY) {
            val newCategory = getDefaultCategory()
            categorySubject.onNext(newCategory)
        }
    }

    // Метод для инициализации PreferenceProvider
    init {
        // Установка начальных значений при первом запуске
        if (preference.getBoolean(KEY_FIRST_LAUNCH, true)) {
            preference.edit {
                putString(KEY_DEFAULT_CATEGORY, DEFAULT_CATEGORY)
                putBoolean(KEY_FIRST_LAUNCH, false)
            }
        }
        // Регистрируем слушатель в SharedPreferences
        preference.registerOnSharedPreferenceChangeListener(listener)
    }

    // Возвращаем Observable для отслеживания изменений категории
    fun getCategoryObservable(): Observable<String> = categorySubject.hide()

    // Метод для сохранения новой категории
    fun saveDefaultCategory(category: String) {
        preference.edit { putString(KEY_DEFAULT_CATEGORY, category) }
        categorySubject.onNext(category)
    }

    // Метод для получения категории по умолчанию
    private fun getDefaultCategory(): String {
        return preference.getString(KEY_DEFAULT_CATEGORY, DEFAULT_CATEGORY) ?: DEFAULT_CATEGORY
    }

    // Объект для хранения ключей в SharedPreferences (выбор категории фильмов)
    companion object {
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_DEFAULT_CATEGORY = "default_category"
        private const val DEFAULT_CATEGORY = "top_rated"
    }
}
