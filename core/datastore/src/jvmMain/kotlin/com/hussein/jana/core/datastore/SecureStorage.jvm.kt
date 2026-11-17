package com.hussein.jana.core.datastore

import java.util.prefs.Preferences

/**
 * Desktop/JVM SecureStorage backed by [java.util.prefs.Preferences].
 *
 * ⚠️  **NOT encrypted.** Values are stored in the OS preferences store
 * (Windows registry / macOS plist / Linux XML file) in plain text.
 * This is intentionally minimal for development and internal tooling.
 *
 * **Do NOT route production credentials or tokens through the Desktop
 * target** until a DPAPI (Windows) / Keychain Services (macOS) backed
 * implementation replaces this stub.
 */
private val node: Preferences = Preferences.userRoot().node("jana/secure")

actual class SecureStorage actual constructor() {

    actual fun putString(key: String, value: String) {
        node.put(key, value)
        node.flush()
    }

    actual fun getString(key: String): String? {
        val v = node.get(key, "")
        return v.ifEmpty { null }
    }

    actual fun remove(key: String) {
        node.remove(key)
        node.flush()
    }

    actual fun clear() {
        node.clear()
        node.flush()
    }
}
