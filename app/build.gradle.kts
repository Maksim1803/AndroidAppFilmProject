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

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

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
            storeFile = if (keystoreProperties.containsKey("storeFile")) file(keystoreProperties["storeFile"] as String) else null
            storePassword = keystoreProperties["storePassword"] as String
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    defaultConfig {
        minSdk = 24
        targetSdk = 35 // Используем стабильный Android 15
        versionCode = 6
        versionName = "1.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64"))
        }
        buildConfigField("String", "TMDB_API_KEY", "\"${localProperties.getProperty("tmdb.api_key") ?: ""}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    flavorDimensions += "version"
    productFlavors {
        create("basic") {
            dimension = "version"
            applicationId = "com.Maksim1803.findafilm.basic"
            versionNameSuffix = "-basic"
        }
        create("pro") {
            dimension = "version"
            applicationId = "com.Maksim1803.findafilm.pro"
            versionNameSuffix = "-pro"
        }
    }
    sourceSets {
        getByName("basic") { java { srcDirs("src/basic/java") } }
        getByName("pro") { java { srcDirs("src/pro/java") } }
    }
    packaging {
        jniLibs {
            // В AGP 9.0+ для поддержки 16 КБ страниц памяти используем 
            // не сжатые (uncompressed) библиотеки с выравниванием (alignment).
            useLegacyPackaging = false
        }
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation("androidx.core:core-splashscreen:1.2.0")
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.animation)
    implementation(libs.androidx.ui.text.android)
    // Удален filament.android так как он не используется и весит много, а также мешает поддержке 16 КБ
    implementation(libs.firebase.config)

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("com.hannesdorfmann:adapterdelegates4-kotlin-dsl:4.3.2")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.3.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0")

    // Room
    val room_version = "2.8.4"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    implementation("androidx.room:room-paging:$room_version")
    implementation("androidx.room:room-rxjava3:$room_version")
    kapt("androidx.room:room-compiler:$room_version")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.10.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.10.0")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.10.0")
    implementation("androidx.lifecycle:lifecycle-reactivestreams-ktx:2.10.0")
    implementation("androidx.fragment:fragment-ktx:1.8.9")
    implementation("androidx.activity:activity-ktx:1.13.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-rx3:1.11.0")

    // Glide
    implementation("com.github.bumptech.glide:glide:5.0.7")
    kapt("com.github.bumptech.glide:compiler:5.0.7")
    implementation("com.github.bumptech.glide:okhttp3-integration:5.0.7")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")
    implementation("com.squareup.retrofit2:adapter-rxjava3:3.0.0")
    implementation("com.squareup.okhttp3:logging-interceptor:5.3.2")

    // Paging
    implementation(libs.androidx.paging.runtime.ktx)
    implementation("androidx.paging:paging-rxjava3:3.5.0")

    // DI
    implementation("io.insert-koin:koin-android:4.2.1")
    implementation("com.google.dagger:dagger:2.59.2")
    kapt("com.google.dagger:dagger-compiler:2.59.2")

    // RxJava
    implementation("io.reactivex.rxjava3:rxandroid:3.0.2")
    implementation("io.reactivex.rxjava3:rxjava:3.1.12")

    implementation(project(":remote_module"))
    implementation(project(":database_module"))

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
