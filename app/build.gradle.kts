import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Locale

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    //плагин для чтения документации проекта
//    id ("org.jetbrains.dokka") version "2.1.0"


    //    id ("kotlin-parcelize")
    //kotlin("kapt")
}

android {
    namespace = "com.example.androidappfilmproject"
    compileSdk = 36

    buildFeatures {
        viewBinding = true

    }

    defaultConfig {
        applicationId = "com.example.androidappfilmproject"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    // Новый синтаксис для kotlin (модуль 33): Используем compilerOptions DSL
    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
}



//dokka {
//
//    // Конфигурация для dokka:
//
//    }

        dependencies {

            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.appcompat)
            implementation(libs.material)
            implementation(libs.androidx.activity)
            implementation(libs.androidx.constraintlayout)
            implementation(libs.androidx.core.animation)
            implementation(libs.androidx.ui.text.android)
            testImplementation(libs.junit)
            androidTestImplementation(libs.androidx.junit)
            androidTestImplementation(libs.androidx.espresso.core)

            //Новые библиотеки
            // RecyclerView
            implementation("androidx.recyclerview:recyclerview:1.4.0")
            // AdapterDelegate
            implementation("com.hannesdorfmann:adapterdelegates4-kotlin-dsl:4.3.2")
            //MaterialDesign
            implementation("com.google.android.material:material:1.13.0")
            //Coordinator layout
            implementation("androidx.coordinatorlayout:coordinatorlayout:1.3.0")

            //Новые библиотеки для создания базы данных модуля 26
            // Room components
            implementation("androidx.room:room-runtime:2.8.3")
            implementation("androidx.room:room-ktx:2.8.3") // Kotlin Extensions and Coroutines support for Room
            implementation("androidx.room:room-paging:2.8.3")
            annotationProcessor("androidx.room:room-compiler:2.8.3")
            //kapt("androidx.room:room-compiler:2.7.2")

            // Lifecycle components
            implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.4")
            implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.9.4")
            implementation("androidx.lifecycle:lifecycle-common-java8:2.9.4")

            // Coroutines
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

            //Новые библиотеки для создания анимации для модуля 28
            //Glide
            implementation("com.github.bumptech.glide:glide:5.0.5")
            annotationProcessor("com.github.bumptech.glide:compiler:5.0.5")

            //Новые библиотеки для создания анимации для модуля 33
            implementation("androidx.fragment:fragment-ktx:1.8.9")// Для viewModels() во фрагментах
            implementation("androidx.activity:activity-ktx:1.11.0")// Для activityViewModels() в активностях (если нужно)
            implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.4")// Для ViewModelScope и других KTX расширений
            implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.9.4")// Для LiveData KTX расширений
        }



