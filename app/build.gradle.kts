import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.kapt)
    id("kotlin-android")
    id("androidx.navigation.safeargs")
    id("kotlin-parcelize")
    id("kotlin-kapt")
}

apply {
    plugin("realm-android")
}

val localProperties = Properties().apply {
    load(FileInputStream(rootProject.file("local.properties")))
}

val STRING = "String"
val TIMEOUT: Long = localProperties.getProperty("TIMEOUT")?.toLong() ?: 30_000

android {
    namespace = "com.appero.vehiclecomplaint"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.appero.vehiclecomplaint"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("long", "TIMEOUT", TIMEOUT.toString())
    }

    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "\"https://public-api-staging-dot-fair-catcher-256606.el.r.appspot.com\"")
            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        release {
            buildConfigField("String", "BASE_URL", "\"https://public-api-staging-dot-fair-catcher-256606.el.r.appspot.com\"")

            isMinifyEnabled = false
//            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    flavorDimensions += "version"
    productFlavors {
        create("prod") {
            buildConfigField("String", "VERSION", "\"\"")
        }
        create("dev") {
            buildConfigField("String", "VERSION", "\"Dev\"")
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.core)
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
    implementation(libs.material)
    api(libs.androidx.fragment.ktx)
    implementation(libs.androidx.activity)
    api(libs.navigation.fragment)
    api(libs.navigation.ui)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.rx.java)
    implementation(libs.rx.android)
    implementation(libs.rx.kotlin)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson.converter)
    implementation(libs.retrofit.scalars.converter)
    implementation(libs.rx.adapter)
    implementation(libs.okhttp)
    implementation(libs.okhttp.loging.interceptor)
    implementation(libs.glide.img)
    implementation(libs.bundles.androidx.camera)
    implementation(libs.androidx.documentfile)
    implementation(libs.androidx.exifinterface)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    kapt(libs.glide.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}