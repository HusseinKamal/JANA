plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.kotlinSerialization)
    // Note: KSP 2.1.0-1.0.29 is incompatible with com.android.kotlin.multiplatform.library
    // (ClassCastException in KotlinMultiplatformAndroidCompilationImpl).
    // KSP / Room code generation is deferred to a future iteration with compatible versions.
}

kotlin {
    androidLibrary {
        namespace = "com.hussein.jana.core.database"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    iosArm64()
    iosSimulatorArm64()
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
        }
    }
}
