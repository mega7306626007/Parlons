plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.francofun"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.francofun"
        minSdk = 26
        targetSdk = 34
        versionCode = 3
        versionName = "3.0"
    }

    buildTypes {
        release { isMinifyEnabled = false }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { compose = true }
    composeOptions { kotlinCompilerExtensionVersion = "1.5.14" }
    // Do NOT restrict ABIs — all 4 are needed for the Vosk/ONNX native
    // libraries to work across real devices (§9.5).
    // ndk { abiFilters += listOf(...) }   <-- must NOT be added
    androidResources { noCompress += listOf("onnx", "zip", "vosk", "txt") }
    packaging {
        resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" }
        jniLibs { useLegacyPackaging = false }
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-compose:1.9.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    // Offline speech pair (§9.1): Vosk STT + ONNX Runtime (Piper TTS voice).
    // Model binaries live in app/src/main/assets (user-supplied, §9.3).
    implementation("com.alphacephei:vosk-android:0.3.47")
    implementation("com.microsoft.onnxruntime:onnxruntime-android:1.18.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    debugImplementation("androidx.compose.ui:ui-tooling")
}
