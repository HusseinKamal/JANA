package com.hussein.jana.core.datastore

import platform.Foundation.NSUserDefaults

private val defaults = NSUserDefaults.standardUserDefaults

actual class SecureStorage actual constructor() {

    actual fun putString(key: String, value: String) {
        defaults.setObject(value, forKey = key)
    }

    actual fun getString(key: String): String? =
        defaults.stringForKey(key)

    actual fun remove(key: String) {
        defaults.removeObjectForKey(key)
    }

    actual fun clear() {
        listOf("access_token", "refresh_token", "user_id").forEach {
            defaults.removeObjectForKey(it)
        }
    }
}
