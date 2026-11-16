package com.hussein.jana.core.common

/**
 * Platform-resolved base URL for all HTTP calls.
 * Each platform provides its own actual implementation.
 */
expect val appBaseUrl: String
