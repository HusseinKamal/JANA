import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

// Single source of truth for the desktop app version.
// Update here when releasing — both the JVM runtime property and the
// native-distribution metadata are derived from this one value.
val janaVersion = "2.4.0"

dependencies {
    implementation(project(":composeApp"))
    implementation(compose.desktop.currentOs)
    implementation(libs.skiko.awt.runtime.windows.x64)
    implementation(libs.kotlinx.coroutinesSwing)
    implementation(libs.koin.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.compose.uiToolingPreview)
}

compose.desktop {
    application {
        mainClass = "com.hussein.jana.MainKt"

        // AppVersion.jvm.kt reads this property at runtime.
        jvmArgs("-Dapp.version=$janaVersion")

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName    = "com.hussein.jana"
            packageVersion = janaVersion           // derived — no duplication
            linux   { iconFile.set(project.file("src/main/resources/jana_icon.svg")) }
            macOS   { iconFile.set(project.file("src/main/resources/jana_icon.svg")) }
            windows { iconFile.set(project.file("src/main/resources/jana_icon.svg")) }
        }
    }
}
