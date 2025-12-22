import org.gradle.kotlin.dsl.implementation
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize") // Заменяем alias на id для надежности
    id("kotlin-kapt")
}

// Load properties from local.properties
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

// Load properties from keystore.properties
val keystoreProperties = Properties()
val keystorePropertiesFile = rootProject.file("app/keystore.properties")
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

android {
    namespace = "com.example.androidappfilmproject"
    compileSdk = 36

    signingConfigs {
        create("release") {
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.example.androidappfilmproject"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Make the API key available in BuildConfig
        buildConfigField("String", "TMDB_API_KEY", "\"${localProperties.getProperty("tmdb.api_key") ?: ""}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
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
    implementation("androidx.room:room-runtime:2.8.4")
    implementation("androidx.room:room-ktx:2.8.4") // Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-paging:2.8.4")
    kapt("androidx.room:room-compiler:2.8.4")

    // Lifecycle components
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.10.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.10.0")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.10.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

    //Новые библиотеки для создания анимации для модуля 28
    //Glide
    implementation("com.github.bumptech.glide:glide:5.0.5")
    kapt("com.github.bumptech.glide:compiler:5.0.5")

    //Новые библиотеки для создания анимации для модуля 33
    implementation("androidx.fragment:fragment-ktx:1.8.9")// Для viewModels() во фрагментах
    implementation("androidx.activity:activity-ktx:1.12.2")// Для activityViewModels() в активностях (если нужно)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.10.0")// Для ViewModelScope и других KTX расширений
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.10.0")// Для LiveData KTX расширений

    //Новые библиотеки для работы с БД для модуля 35
    implementation("com.squareup.retrofit2:retrofit:3.0.0") //рертофит
    implementation("com.squareup.retrofit2:converter-gson:3.0.0") //конвертер
    implementation("com.squareup.okhttp3:logging-interceptor:5.3.2") //логгер

    implementation(libs.androidx.paging.runtime.ktx)

    //Библиотека Koin для модуля 36
    implementation("io.insert-koin:koin-android:4.1.1")

    //Библиотеки для модуля 37
    implementation ("com.google.dagger:dagger:2.57.2")
    kapt ("com.google.dagger:dagger-compiler:2.57.2")

    //Библиотека для модуля 38. SwipeRefreshLayout (для fragment_home.xml)
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0")
}
