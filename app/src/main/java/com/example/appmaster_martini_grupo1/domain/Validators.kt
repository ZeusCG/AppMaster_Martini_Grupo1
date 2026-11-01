package com.example.appmaster_martini_grupo1.domain

object Validators {

    private val EMAIL_REGEX =
        Regex("^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$")

    private val PHONE_REGEX =
        Regex("^(\\+?56)?9?\\d{8}$")

    private const val MIN_PASSWORD_LENGTH = 8

    fun isValidEmail(text: String): Boolean {
        val value = text.trim()
        return value.isNotEmpty() && EMAIL_REGEX.matches(value)
    }

    fun isValidPhone(text: String): Boolean {
        val value = text.trim().replace(" ", "").replace("-", "")
        return value.isNotEmpty() && PHONE_REGEX.matches(value)
    }

    fun isValidPassword(text: String): Boolean {
        return text.length >= MIN_PASSWORD_LENGTH &&
                text.any { it.isLetter() } &&
                text.any { it.isDigit() }
    }

    fun isValidRut(text: String): Boolean {
        val clean = text.trim().replace(".", "").replace("-", "").uppercase()
        if (clean.length < 2) return false
        val body = clean.dropLast(1)
        val dv = clean.last()
        if (body.any { !it.isDigit() }) return false
        return calculateDv(body) == dv
    }

    private fun calculateDv(body: String): Char {
        var sum = 0
        var factor = 2
        for (i in body.length - 1 downTo 0) {
            sum += (body[i] - '0') * factor
            factor = if (factor == 7) 2 else factor + 1
        }
        return when (val remainder = 11 - (sum % 11)) {
            11 -> '0'
            10 -> 'K'
            else -> ('0' + remainder)
        }
    }
}