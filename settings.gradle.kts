rootProject.name = "JANA"

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        // JetBrains dev Maven — publishes KMP variants of AndroidX artifacts
        // (needed for lifecycle-viewmodel-navigation3 which has no iOS artifact on Google Maven)
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

// ---- App shells ----
include(":androidApp")
include(":desktopApp")
include(":composeApp")
project(":composeApp").projectDir = file("shared")

// ---- Core modules ----
include(":core:common")
include(":core:designsystem")
include(":core:navigation")
include(":core:network")
include(":core:datastore")
include(":core:database")
include(":core:di")

// ---- Shared models ----
include(":shared-models")

// ---- Feature modules ----
include(":feature:splash")
include(":feature:home")
