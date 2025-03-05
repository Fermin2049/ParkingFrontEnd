plugins {
    id("com.android.application")
    id("com.google.gms.google-services") // Plugin para Firebase
}

android {
    namespace = "com.fermin2049.parking"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.fermin2049.parking"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // OkHttp (para manejar solicitudes de red y el interceptor)
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    // SweetAlert
    implementation("com.github.f0ris.sweetalert:library:1.5.6")

    // Google Play Services (para autenticación con Google)
    implementation("com.google.android.gms:play-services-auth:21.3.0")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.9.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")

    //recaptcha
    implementation("com.github.hCaptcha.hcaptcha-android-sdk:sdk:4.0.3")

    // Glide
    implementation("androidx.browser:browser:1.8.0")

    // Google Maps
    implementation ("com.google.android.gms:play-services-maps:18.1.0")
    implementation ("com.google.android.gms:play-services-location:21.0.1")



    implementation ("androidx.biometric:biometric:1.2.0-alpha05")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.legacy.support.v4)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.glide)
    implementation(libs.gson)
    implementation(libs.play.services.base)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
