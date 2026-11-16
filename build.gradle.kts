plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.room) apply false
}

subprojects {
    configurations.all {
        resolutionStrategy.dependencySubstitution {
            // lifecycle-viewmodel-navigation3 is not on Google Maven for non-Android targets.
            // Redirect to the JetBrains KMP variant which ships all targets.
            substitute(module("androidx.lifecycle:lifecycle-viewmodel-navigation3"))
                .using(module("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-navigation3:2.10.0"))
        }
    }
}
