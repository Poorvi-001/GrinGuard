import java.util.Properties

val localProperties = Properties()
localProperties.load(rootProject.file("local.properties").inputStream())

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.gringuard"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.gringuard"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "GEMINI_API_KEY", "\"${localProperties["GEMINI_API_KEY"]}\"")
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

    buildFeatures {
        buildConfig = true
    }

    aaptOptions {
        noCompress("tflite")
    }
}

dependencies {
    // Android Core
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.activity:activity:1.12.2")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:34.9.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-analytics")

    // Google Services
    implementation("com.google.android.gms:play-services-auth:21.0.0")

    // Gemini AI (for ChatActivity)
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")
    implementation("io.noties.markwon:core:4.6.2")
    implementation("com.google.guava:guava:33.0.0-android")

    // TFLite
    implementation("org.tensorflow:tensorflow-lite:2.14.0")
    implementation("org.tensorflow:tensorflow-lite-support:0.4.4")

    // Charting
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // ML Kit Image Labeling (Tooth validation)
    implementation("com.google.mlkit:image-labeling:17.0.9")
}