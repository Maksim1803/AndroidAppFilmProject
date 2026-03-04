package com.example.database_module

import com.example.database_module.di.DatabaseModule
import dagger.BindsInstance
import dagger.Component
import android.content.Context
import javax.inject.Singleton

@Singleton
@Component(modules = [DatabaseModule::class])
// Интерфейс DatabaseComponent определяет методы для создания экземпляра DatabaseComponent.
interface DatabaseComponent : DatabaseProvider {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): DatabaseComponent
    }
}
