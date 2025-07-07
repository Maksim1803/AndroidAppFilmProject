package com.example.androidappfilmproject

import android.app.Application

//Класс для доступа к базе данных вариант 3 (рабочий)

class App : Application() {

    companion object {
        lateinit var instance: App
            private set
        val favoriteFilms = HashSet<Film>() // Храним избранные фильмы в памяти
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
//Класс для доступа к базе данных вариант 2
//class App : Application() {
//
//    // Объявление переменной для хранения экземпляра базы данных, где
//    // lateinit - указывает, что переменная будет инициализирована позже
//    lateinit var db: AppDatabase // AppDatabase - тип переменной (класс - база данных)
//        private set // обеспечивает доступ к переменной только для чтения извне класса
//
//    // Метод, вызываемый при создании приложения
//    override fun onCreate() {
//        super.onCreate() // Вызов родительского класса
//        instance = this
//        db = Room.databaseBuilder(
//            applicationContext,
//            AppDatabase::class.java,
//            "film_database" // Имя базы данных
//        ).build()
//        // Запускаем корутину для добавления фильмов в базу данных
//        CoroutineScope(Dispatchers.IO).launch {
//            val filmDao = db.filmDao()
//            // Проверяем, есть ли фильмы в базе данных
//            if (filmDao.getAllFilms().firstOrNull()?.isEmpty() == true) {
//                // Добавляем фильмы из filmsDataBase в базу данных
//                HomeFragment().filmsDataBase.forEach { film ->
//                    filmDao.insert(film)
//                }
//            }
//        }
//    }
//
//    // Статический объект для статических переменных и методов
//    companion object {
//        lateinit var instance: App // App - тип переменной (сам класс App)
//            private set
//    }
//}

//Класс для доступа к базе данных вариант 1
//class App : Application() {
//
//    // Объявление переменной для хранения экземпляра базы данных, где
//    // lateinit - указывает, что переменная будет инициализирована позже
//    lateinit var db: AppDatabase // AppDatabase - тип переменной (класс - база данных)
//        private set // обеспечивает доступ к переменной только для чтения извне класса
//
//    // Метод, вызываемый при создании приложения
//    override fun onCreate() {
//        super.onCreate() // Вызов родительского класса
//        instance = this
//        db = Room.databaseBuilder(
//            applicationContext,
//            AppDatabase::class.java,
//            "film_database" // Имя базы данных
//        ).build()
//    }
//    // Статический объект для статических переменных и методов
//    companion object {
//        lateinit var instance: App // App - тип переменной (сам класс App)
//            private set
//    }
//}

