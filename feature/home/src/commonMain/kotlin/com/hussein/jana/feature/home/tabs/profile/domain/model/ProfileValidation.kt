package com.hussein.jana.feature.home.tabs.profile.domain.model

/** Field-level validation errors; the UI maps each one to a localized message. */
enum class ProfileFieldError {
    NAME_REQUIRED,
    NAME_TOO_SHORT,
    EMAIL_INVALID,
    PHONE_REQUIRED,
    PHONE_INVALID,
}

object StudentProfileValidator {
    private const val MIN_NAME_LENGTH = 2
    private const val MIN_PHONE_DIGITS = 8
    private const val MAX_PHONE_DIGITS = 15
    private val EMAIL_REGEX = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
    private val PHONE_CHARS_REGEX = Regex("^[0-9+\\-() ]+$")

    fun validateName(name: String): ProfileFieldError? {
        val trimmed = name.trim()
        return when {
            trimmed.isEmpty() -> ProfileFieldError.NAME_REQUIRED
            trimmed.length < MIN_NAME_LENGTH -> ProfileFieldError.NAME_TOO_SHORT
            else -> null
        }
    }

    /** Email is optional: blank is valid. */
    fun validateEmail(email: String): ProfileFieldError? {
        val trimmed = email.trim()
        return if (trimmed.isEmpty() || EMAIL_REGEX.matches(trimmed)) null else ProfileFieldError.EMAIL_INVALID
    }

    fun validatePhone(phone: String): ProfileFieldError? {
        val trimmed = phone.trim()
        if (trimmed.isEmpty()) return ProfileFieldError.PHONE_REQUIRED
        val digits = trimmed.count { it.isDigit() }
        return if (PHONE_CHARS_REGEX.matches(trimmed) && digits in MIN_PHONE_DIGITS..MAX_PHONE_DIGITS) {
            null
        } else {
            ProfileFieldError.PHONE_INVALID
        }
    }
}
