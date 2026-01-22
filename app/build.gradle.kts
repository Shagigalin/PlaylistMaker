plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.plugin.parcelize")
    id("kotlin-kapt")
    alias(libs.plugins.androidx.navigation.safe.args)
}

android {
    namespace = "com.example.playlistmaker"
    compileSdk = 35

    buildFeatures {
        viewBinding = true
        dataBinding = true

        defaultConfig {
            applicationId = "com.example.playlistmaker"
            minSdk = 29
            targetSdk = 35
            versionCode = 1
            versionName = "1.0"

            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

            // Добавляем для совместимости с Java 11
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_11
                targetCompatibility = JavaVersion.VERSION_11
            }

            kotlinOptions {
                jvmTarget = "11"
            }
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

        // Добавляем для Safe Args
        buildFeatures {
            viewBinding = true
            dataBinding = true
        }
    }

    dependencies {
        // Core Android
        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.appcompat)
        implementation(libs.material)
        implementation(libs.androidx.activity)
        implementation(libs.androidx.constraintlayout)
        implementation(libs.androidx.preference)
        implementation(libs.androidx.recyclerview)

        // Navigation Component
        implementation(libs.androidx.navigation.fragment.ktx)
        implementation(libs.androidx.navigation.ui.ktx)
        implementation(libs.androidx.navigation.dynamic.features.fragment)

        // ViewPager2 для MediaLibrary
        implementation(libs.androidx.viewpager2)

        // Lifecycle (добавляем, если еще нет)
        implementation(libs.androidx.lifecycle.viewmodel.ktx)
        implementation(libs.androidx.lifecycle.livedata.ktx)
        implementation(libs.androidx.lifecycle.runtime.ktx)

        // Testing
        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)

        // UI & Images
        implementation(libs.compose.icons.core)
        implementation(libs.glide)
        implementation(libs.glide.transformations)
        kapt(libs.glide.compiler)

        // Network
        implementation(libs.retrofit)
        implementation(libs.retrofit.converter.gson)
        implementation(libs.gson)
        implementation(libs.okhttp)
        implementation(libs.okhttp.logging.interceptor)

        // Coroutines
        implementation(libs.kotlinx.coroutines.android)
        implementation(libs.kotlinx.coroutines.core)

        // Koin для Dependency Injection
        implementation(libs.koin.android)
        implementation(libs.koin.androidx.compose)
        implementation(libs.koin.androidx.workmanager)
        implementation(libs.koin.androidx.navigation)

        // Material Components
        implementation("com.google.android.material:material:1.11.0")
    }
}