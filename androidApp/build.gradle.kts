plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(project(":shared"))
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.foundation)
            implementation(libs.compose.navigation)
            implementation(libs.compose.icons.extended)
            implementation(libs.koin.android)
            implementation(libs.koin.compose)
            implementation(libs.koin.androidx.compose)
            implementation(libs.kotlinx.datetime)
        }
    }
}

android {
    namespace = "com.notes"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.notes"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        
        // Suppress warning about unsupported compileSdk 35 for AGP 8.5.2
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
