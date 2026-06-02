plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id ("kotlin-parcelize")
    id("kotlin-kapt")                        // ← add this
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.app.gradmo"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.app.gradmo"
        minSdk = 28
        targetSdk = 36
        versionCode = 4
        versionName = "1.0.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        ndk {
            abiFilters += listOf("arm64-v8a")
        }
    }
    buildFeatures {
        dataBinding = true
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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
    kotlinOptions {
        jvmTarget = "11"
    }
    packaging {
        resources {
            excludes += setOf(
                "META-INF/INDEX.LIST",
                "META-INF/DEPENDENCIES",
                "META-INF/gradle/incremental.annotation.processors"
            )
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    //Hilt - Dagger
    kapt(libs.hilt.android.compiler)
    implementation(libs.hilt.android)
    //Retrofit
    implementation (libs.retrofit)
    implementation(libs.retrofit2.converter.gson)
    implementation (libs.okhttp)
    implementation (libs.logging.interceptor)

    //glide
    implementation(libs.glide)
    annotationProcessor(libs.compiler)

    implementation("me.relex:circleindicator:2.1.6")

//    implementation("com.amazonaws:aws-android-sdk-s3:2.72.0")
//    implementation("com.amazonaws:aws-android-sdk-core:2.72.0")

//    image cropper
//    implementation("com.github.yalantis:ucrop:2.2.10")

    implementation ("com.google.android.gms:play-services-location:21.0.1")

    implementation("com.razorpay:checkout:1.6.40")
    implementation("us.zoom.videosdk:zoomvideosdk-core:2.5.5")          // mandatory
//    implementation("us.zoom.videosdk:zoomvideosdk-annotation:2.5.5")     // screen share annotation (optional)
//    implementation("us.zoom.videosdk:zoomvideosdk-videoeffects:2.5.5")   // virtual background (optional)
    implementation("androidx.media3:media3-exoplayer:1.3.1")
    implementation("androidx.media3:media3-ui:1.3.1")

}