package com.hussein.jana.core.datastore

import java.util.prefs.Preferences

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
