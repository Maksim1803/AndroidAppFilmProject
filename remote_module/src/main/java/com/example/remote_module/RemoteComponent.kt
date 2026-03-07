package com.example.remote_module

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [RemoteModule::class]
)
// Интерфейс RemoteComponent определяет методы для создания
// экземпляра RemoteComponent.
interface RemoteComponent : RemoteProvider
