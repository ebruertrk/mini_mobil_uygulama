plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.example.donemodevi"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.donemodevi"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.10")
    implementation("com.google.android.gms:play-services-location:21.0.1") // ← KONUM ALMA KÜTÜPHANESİ
}
