import com.android.build.gradle.internal.utils.isKotlinKaptPluginApplied
import org.jetbrains.kotlin.gradle.model.Kapt

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    //    id ("kotlin-parcelize")
    // kotlin("kapt")

}

android {
    namespace = "com.example.androidappfilmproject"
    compileSdk = 35

    buildFeatures  {
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.example.androidappfilmproject"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
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
    implementation ("androidx.recyclerview:recyclerview:1.4.0")
    // AdapterDelegate
    implementation ("com.hannesdorfmann:adapterdelegates4-kotlin-dsl:4.3.2")
    //MaterialDesign
    implementation ("com.google.android.material:material:1.12.0")
    //Coordinator layout
    implementation ("androidx.coordinatorlayout:coordinatorlayout:1.3.0")

    //Новые библиотеки для создания базы данных модуля 26
    // Room components
    implementation ("androidx.room:room-runtime:2.7.2")
    implementation ("androidx.room:room-ktx:2.7.2") // Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-paging:2.7.2")
    annotationProcessor ("androidx.room:room-compiler:2.7.2")
    //kapt("androidx.room:room-compiler:2.7.2")

    // Lifecycle components
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.1")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.9.1")
    implementation ("androidx.lifecycle:lifecycle-common-java8:2.9.1")

    // Coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

    //Новые библиотеки для создания анимации для модуля 28
    //Glide
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")
}

