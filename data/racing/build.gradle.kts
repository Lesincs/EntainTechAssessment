plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.android.detekt)
    alias(libs.plugins.kapt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.lesincs.entaintechassessment.data"
    compileSdk = 34

    kotlinOptions {
        jvmTarget = "17"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(project(":core:network"))
    implementation(libs.hilt)
    implementation(libs.timber)
    implementation(libs.kotlin.serialization)

    kapt(libs.hilt.compiler)

    testImplementation(libs.ktor.mock)
    testImplementation(libs.junit)
    testImplementation(libs.kotest.assertions)
    testImplementation(libs.kotinx.courtines.test)
    testImplementation(libs.mockk)
}