plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.safe.args)
    id("kotlin-kapt")
//    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
}

android {
    namespace = "com.parental.control.displaytime.kids.safety"
    compileSdk = 34

    signingConfigs.create("release") {
        storeFile =
            File("/Users/mudassirsatti/AndroidStudioProjects/ParentalLock/parental_lock.jks")
        storePassword = "lockparental"
        keyAlias = "lock0"
        keyPassword = "lockparental"
    }

    defaultConfig {
        applicationId = "com.parental.control.displaytime.kids.safety"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        setProperty("archivesBaseName", "Parental_Lock-v$versionCode($versionName)")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
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
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.fragment.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.multidex)
    implementation(libs.hilt.android)
    implementation(libs.ssp.android)
    implementation(libs.sdp.android)
    implementation(libs.glide)
    implementation(libs.dexter)
    implementation(libs.gson)
    implementation(libs.eventbus)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.hilt.compiler)
    implementation(libs.lottie)
    implementation(libs.androidx.datastore.preferences)
    kapt(libs.androidx.room.compiler.v250)

}