plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
}

kotlin {
    androidLibrary {
        namespace = "com.hussein.jana.core.di"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    iosArm64()
    iosSimulatorArm64()
    jvm()

    sourceSets {
        commonMain.dependencies {
            api(project(":core:common"))
            api(project(":core:network"))
            api(project(":core:navigation"))
            api(project(":core:datastore"))
            api(project(":core:database"))
            api(libs.koin.core)
            // Expose transitive types needed by CoreModule
            api(libs.ktor.client.core)
            api(libs.datastore.preferences)
        }
        androidMain.dependencies {
            implementation(libs.koin.android)
        }
    }
}
