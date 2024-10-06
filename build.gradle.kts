plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.dagger.hilt) apply false
    alias(libs.plugins.safe.args) apply false
    alias(libs.plugins.org.jetbrains.kotlin.kapt) apply false
//    //id("com.google.devtools.ksp") version "1.9.0-1.0.13" apply false
//    id ("com.google.devtools.ksp") version "1.9.0-1.0.12" apply false
}