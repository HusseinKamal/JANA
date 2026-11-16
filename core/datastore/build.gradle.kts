plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    androidLibrary {
        namespace = "com.hussein.jana.core.datastore"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    iosArm64()
    iosSimulatorArm64()
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.datastore.preferences)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.okio)
        }
        androidMain.dependencies {
            implementation(libs.security.crypto)
        }
        jvmTest.dependencies {
            implementation(libs.kotlin.testJunit)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}
