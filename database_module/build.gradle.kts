import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// Подключаем файл с версиями
apply(from = "$rootDir/versions.gradle")

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.database_module"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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

    // Room
    val room_version = "2.8.4"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    implementation("androidx.room:room-paging:$room_version")
    implementation("androidx.room:room-rxjava3:$room_version")
    kapt("androidx.room:room-compiler:$room_version")

    // Dagger
    implementation(project.extra["dagger"] as String)
    kapt(project.extra["daggerCompiler"] as String)

    // Paging (нужен для PagingSource в DAO)
    implementation("androidx.paging:paging-runtime-ktx:3.4.1")
    implementation("androidx.paging:paging-rxjava3:3.4.1")

    // RxJava
    implementation("io.reactivex.rxjava3:rxandroid:3.0.2")
    implementation("io.reactivex.rxjava3:rxjava:3.1.12")
}
