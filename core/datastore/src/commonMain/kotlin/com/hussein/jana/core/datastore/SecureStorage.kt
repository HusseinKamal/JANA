package com.hussein.jana.core.datastore

expect class SecureStorage() {
    fun putString(key: String, value: String)
    fun getString(key: String): String?
    fun remove(key: String)
    fun clear()
}
