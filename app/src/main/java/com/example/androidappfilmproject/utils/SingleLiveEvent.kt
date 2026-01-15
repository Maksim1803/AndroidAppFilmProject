package com.example.androidappfilmproject.utils

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

// Кастомный класс LiveData, который отправляет обновление только один раз.
// Это полезно для таких событий, как сообщения SnackBar, навигация или Toast.
class SingleLiveEvent<T> : MutableLiveData<T>() {

    // Переменная для отслеживания наличия ожидающего события.
    private val mPending = AtomicBoolean(false)

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {

        // Проверяем, есть ли уже активные наблюдатели.
        if (hasActiveObservers()) {
            Log.w(TAG, "Multiple observers registered but only one will be notified of changes.")
        }

        // Подписываемся на внутреннюю LiveData.
        super.observe(owner) { t ->
            // Если флаг mPending установлен в true, сбрасываем его и уведомляем наблюдателя.
            if (mPending.compareAndSet(true, false)) {
                observer.onChanged(t)
            }
        }
    }

    @MainThread
    override fun setValue(t: T?) {
        // При установке нового значения устанавливаем флаг mPending в true.
        mPending.set(true)
        super.setValue(t)
    }


    //Используется для случаев, когда T — Void, чтобы сделать вызовы чище
    // Например, если бы создали событие без данных, просто для команды
    // «обновить экран» или «закрыть фрагмент» (подсвечен неиспользуемым)
    @MainThread
    fun call() {
        value = null
    }

    companion object {
        private const val TAG = "SingleLiveEvent"
    }
}
