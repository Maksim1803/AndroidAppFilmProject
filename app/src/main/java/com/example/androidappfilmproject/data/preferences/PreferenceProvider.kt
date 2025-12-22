package com.example.androidappfilmproject.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

// Класс для работы с SharedPreferences
class PreferenceProvider(context: Context) {
    private val appContext = context.applicationContext
    private val preference: SharedPreferences = appContext.getSharedPreferences("selections", Context.MODE_PRIVATE)

    private val _categoryFlow = MutableStateFlow(getDefaultCategory())
    val categoryFlow = _categoryFlow.asStateFlow()

    // Создаем слушатель как свойство класса, чтобы на него была ссылка
    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == KEY_DEFAULT_CATEGORY) {
            _categoryFlow.value = getDefaultCategory()
        }
    }

    init {
        if (preference.getBoolean(KEY_FIRST_LAUNCH, true)) {
            preference.edit {
                putString(KEY_DEFAULT_CATEGORY, DEFAULT_CATEGORY)
                putBoolean(KEY_FIRST_LAUNCH, false)
            }
        }
        // Регистрируем слушатель
        preference.registerOnSharedPreferenceChangeListener(listener)
    }

    fun saveDefaultCategory(category: String) {
        preference.edit { putString(KEY_DEFAULT_CATEGORY, category) }
    }

    fun getDefaultCategory(): String {
        return preference.getString(KEY_DEFAULT_CATEGORY, DEFAULT_CATEGORY) ?: DEFAULT_CATEGORY
    }

    companion object {
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_DEFAULT_CATEGORY = "default_category"
        private const val DEFAULT_CATEGORY = "popular"
    }
}
