package com.example.androidappfilmproject.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

// Класс для работы с SharedPreferences, адаптированный под RxJava
class PreferenceProvider(context: Context) {

    // Ключи и значения по умолчанию выносим в companion object
    companion object {
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_DEFAULT_CATEGORY = "default_category"
        private const val DEFAULT_CATEGORY = "popular"
        private const val KEY_LANGUAGE = "app_language"
        private const val DEFAULT_LANGUAGE = "en-US"
    }

    // Инициализируем SharedPreferences сразу
    private val preference: SharedPreferences =
        context.applicationContext.getSharedPreferences("selections", Context.MODE_PRIVATE)

    // Метод для получения категории (нужен до инициализации subject)
    private fun getDefaultCategory(): String {
        return preference.getString(KEY_DEFAULT_CATEGORY, DEFAULT_CATEGORY) ?: DEFAULT_CATEGORY
    }

    // BehaviorSubject для хранения текущей категории.
    private val categorySubject = BehaviorSubject.createDefault(getDefaultCategory())

    // Храним слушателя как жесткую ссылку
    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == KEY_DEFAULT_CATEGORY) {
            val newCategory = getDefaultCategory()
            categorySubject.onNext(newCategory)
        }
    }

    init {
        // Установка начальных значений при первом запуске
        if (preference.getBoolean(KEY_FIRST_LAUNCH, true)) {
            preference.edit {
                putString(KEY_DEFAULT_CATEGORY, DEFAULT_CATEGORY)
                putString(KEY_LANGUAGE, DEFAULT_LANGUAGE)
                putBoolean(KEY_FIRST_LAUNCH, false)
            }
        }
        // Регистрируем слушатель
        preference.registerOnSharedPreferenceChangeListener(listener)
    }

    // Возвращаем Observable для отслеживания изменений категории
    fun getCategoryObservable(): Observable<String> = categorySubject.hide()

    // Метод для сохранения новой категории
    fun saveDefaultCategory(category: String) {
        preference.edit { putString(KEY_DEFAULT_CATEGORY, category) }
        categorySubject.onNext(category)
    }

    // Метод для получения текущего языка
    fun getLanguage(): String {
        return preference.getString(KEY_LANGUAGE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
    }

    // Метод для сохранения языка
    fun saveLanguage(language: String) {
        preference.edit { putString(KEY_LANGUAGE, language) }
    }
}
