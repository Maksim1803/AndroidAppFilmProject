plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id ("kotlin-parcelize")
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
}