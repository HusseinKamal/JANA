import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidLibrary {
        namespace = "com.hussein.jana.core.designsystem"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
        androidResources {
            enable = true
        }
    }

    iosArm64()
    iosSimulatorArm64()
    jvm()

    sourceSets {
        commonMain.dependencies {
            api(libs.compose.runtime)
            api(libs.compose.foundation)
            api(libs.compose.material3)
            api(libs.compose.ui)
            api(libs.compose.components.resources)
            api(libs.compose.material.icons.extended)
            api(libs.coil.compose)
            api(libs.coil.network)
        }
        androidMain.dependencies {
            api(libs.compose.uiToolingPreview)
            implementation(libs.androidx.appcompat)
        }
    }
}

compose.resources {
    packageOfResClass = "com.hussein.jana.core.designsystem"
    publicResClass = true
}

afterEvaluate {
    val prepareCommonMain = tasks.named("prepareComposeResourcesTaskForCommonMain")
    val mergeAssets = tasks.named("mergeAndroidMainAssets")

    val copyComposeResourcesToAndroid =
        tasks.register<Copy>("copyCommonComposeResourcesToAndroidAssets") {
            dependsOn(prepareCommonMain)
            from(
                layout.buildDirectory.dir(
                    "generated/compose/resourceGenerator/preparedResources/commonMain/composeResources"
                )
            )
            into(
                layout.buildDirectory.dir(
                    "intermediates/assets/androidMain/mergeAndroidMainAssets/composeResources/com.hussein.jana.core.designsystem"
                )
            )
        }

    mergeAssets.configure {
        dependsOn(copyComposeResourcesToAndroid)
    }
}
