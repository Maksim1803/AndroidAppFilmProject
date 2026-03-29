import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.implementation
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
    id("kotlin-kapt")
    alias(libs.plugins.google.gms.google.services)
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
            // Проверка на null для предотвращения ошибок сборки, если файл отсутствует
            storeFile =
                if (keystoreProperties.containsKey("storeFile")) file(keystoreProperties["storeFile"] as String) else null
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

        ndk {
            // Нужные архитектуры для специфических устройств (эмуляторов)
            abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64"))
        }
        // Make the API key available in BuildConfig
        buildConfigField(
            "String",
            "TMDB_API_KEY",
            "\"${localProperties.getProperty("tmdb.api_key") ?: ""}\""
        )
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
    // Создание платной и бесплатной версий для задания модуля 52
    flavorDimensions += "version"
    productFlavors {
        create("basic") {
            dimension = "version"
            applicationIdSuffix = ".basic"
            versionNameSuffix = "-basic"
        }
        create("pro") {
            dimension = "version"
            applicationIdSuffix = ".pro"
            versionNameSuffix = "-pro"
        }
    }
    sourceSets {
        getByName("basic") {
            java {
                srcDirs("src\\basic\\java", "src\\basic\\java")
            }
        }
        getByName("pro") {
            java {
                srcDirs("src\\pro\\java", "src\\pro\\java")
            }
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
    implementation(libs.filament.android)
    implementation(libs.firebase.config)
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
    implementation("androidx.room:room-rxjava3:2.8.4")
    kapt("androidx.room:room-compiler:2.8.4")

    // Lifecycle components
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.10.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.10.0")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.10.0")
    implementation("androidx.lifecycle:lifecycle-reactivestreams-ktx:2.10.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-rx3:1.10.2") // Для asObservable()

    //Новые библиотеки для создания анимации для модуля 28
    //Glide
    implementation("com.github.bumptech.glide:glide:5.0.5")
    kapt("com.github.bumptech.glide:compiler:5.0.5")

    //Новые библиотеки для создания анимации для модуля 33
    implementation("androidx.fragment:fragment-ktx:1.8.9")// Для viewModels() во фрагментах
    implementation("androidx.activity:activity-ktx:1.12.4")// Для activityViewModels() в активностях (если нужно)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.10.0")// Для ViewModelScope и других KTX расширений
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.10.0")// Для LiveData KTX расширений

    //Новые библиотеки для работы с БД для модуля 35
    implementation("com.squareup.retrofit2:retrofit:3.0.0") //рертофит
    implementation("com.squareup.retrofit2:converter-gson:3.0.0") //конвертер
    implementation("com.squareup.retrofit2:adapter-rxjava3:3.0.0")
    implementation("com.squareup.okhttp3:logging-interceptor:5.3.2") //логгер

    implementation(libs.androidx.paging.runtime.ktx)
    implementation("androidx.paging:paging-rxjava3:3.4.1")

    //Библиотека Koin для модуля 4.1.1
    implementation("io.insert-koin:koin-android:4.1.1")

    //Библиотеки для модуля 37
    implementation ("com.google.dagger:dagger:2.59.1")
    kapt ("com.google.dagger:dagger-compiler:2.59.1")

    //Библиотека для модуля 38. SwipeRefreshLayout (for fragment_home.xml)
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0")

    //Библиотека для модуля 42 (Coroutines - корутины).
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

    //Библиотеки для модулей 44 и 45 (RxJava)
    implementation("io.reactivex.rxjava3:rxandroid:3.0.2")
    implementation("io.reactivex.rxjava3:rxjava:3.1.12")

    //Библиотеки для модуля 46(Remote module) и задания со звездочкой (Database module)
    implementation(project(":remote_module"))
    implementation(project(":database_module"))

}
