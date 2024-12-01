plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.android.detekt)
    alias(libs.plugins.kapt)
}

android {
    namespace = "com.lesincs.entaintechassessment.core"
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
    api(libs.bundles.ktor.client)
    implementation(libs.hilt)
    implementation(libs.timber)

    kapt(libs.hilt.compiler)

    testApi(libs.ktor.mock)
}