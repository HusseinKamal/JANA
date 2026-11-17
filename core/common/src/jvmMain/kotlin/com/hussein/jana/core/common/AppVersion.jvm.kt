package com.hussein.jana.core.common

/**
 * Desktop/JVM: reads the `app.version` system property.
 * Pass it at launch: `java -Dapp.version=2.4.0 -jar jana.jar`
 * or configure it in compose.desktop → jvmArgs in desktopApp/build.gradle.kts.
 * Falls back to "1.0.0" when the property is absent (e.g. IDE run configs).
 */
actual val appVersion: String =
    System.getProperty("app.version", "1.0.0")
