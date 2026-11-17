import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

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

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.hussein.jana"
            packageVersion = "1.0.0"
            // Platform-specific icon files referenced from desktopApp/src/main/resources/
            linux   { iconFile.set(project.file("src/main/resources/jana_icon.svg")) }
            macOS   { iconFile.set(project.file("src/main/resources/jana_icon.svg")) }
            windows { iconFile.set(project.file("src/main/resources/jana_icon.svg")) }
        }
    }
}
