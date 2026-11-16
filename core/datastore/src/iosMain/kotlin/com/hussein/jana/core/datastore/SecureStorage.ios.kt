package com.hussein.jana.core.datastore

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.CoreFoundation.CFBridgingRelease
import platform.CoreFoundation.CFDictionaryRef
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFBooleanTrue
import platform.Foundation.NSData
import platform.Foundation.NSMutableDictionary
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData

/**
 * Keychain-backed SecureStorage for iOS.
 *
 * All items are stored under the service label [KEYCHAIN_SERVICE] using
 * kSecClassGenericPassword so that:
 *  - Data is protected by the Keychain (encrypted at rest, governed by
 *    kSecAttrAccessible policies, excluded from unencrypted iCloud backups
 *    when using the default kSecAttrAccessibleWhenUnlocked policy).
 *  - [clear] deletes every item under the JANA service label without
 *    hardcoding individual key names, matching the semantics of the
 *    Android EncryptedSharedPreferences.clear() implementation.
 */
private const val KEYCHAIN_SERVICE = "com.hussein.jana.secure"

@OptIn(ExperimentalForeignApi::class)
actual class SecureStorage actual constructor() {

    /** Build a base Keychain query dict scoped to [KEYCHAIN_SERVICE]. */
    private fun baseQuery(key: String? = null): NSMutableDictionary {
        val dict = NSMutableDictionary()
        // kSecClass / kSecClassGenericPassword are CFStringRef (toll-free bridged to NSString)
        dict.setObject(kSecClassGenericPassword!!, forKey = kSecClass!!)
        dict.setObject(KEYCHAIN_SERVICE, forKey = kSecAttrService!!)
        if (key != null) dict.setObject(key, forKey = kSecAttrAccount!!)
        return dict
    }

    actual fun putString(key: String, value: String) {
        // Remove any existing entry first to avoid errSecDuplicateItem.
        remove(key)
        val valueData: NSData = NSString.stringWithString(value)
            .dataUsingEncoding(NSUTF8StringEncoding) ?: return
        val query = baseQuery(key)
        query.setObject(valueData, forKey = kSecValueData!!)
        SecItemAdd(query as CFDictionaryRef, null)
    }

    actual fun getString(key: String): String? {
        val query = baseQuery(key)
        query.setObject(kCFBooleanTrue!!, forKey = kSecReturnData!!)
        query.setObject(kSecMatchLimitOne!!, forKey = kSecMatchLimit!!)
        return memScoped {
            val result = alloc<CFTypeRefVar>()
            val status = SecItemCopyMatching(query as CFDictionaryRef, result.ptr)
            if (status == errSecSuccess) {
                // Transfer Keychain-retained object to ARC ownership.
                val nsData = CFBridgingRelease(result.value) as? NSData
                nsData?.let { NSString.create(it, NSUTF8StringEncoding) as? String }
            } else {
                null
            }
        }
    }

    actual fun remove(key: String) {
        SecItemDelete(baseQuery(key) as CFDictionaryRef)
    }

    actual fun clear() {
        // Delete ALL Keychain items stored under the JANA service label —
        // no hardcoded key names needed. This is symmetric with Android's
        // EncryptedSharedPreferences.edit().clear() which also wipes the
        // entire backing store.
        SecItemDelete(baseQuery(null) as CFDictionaryRef)
    }
}
