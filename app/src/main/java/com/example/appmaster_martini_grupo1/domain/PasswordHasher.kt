package com.example.appmaster_martini_grupo1.domain

import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordHasher {

    private const val ALGORITHM = "PBKDF2WithHmacSHA1"

    private const val ITERATIONS = 100_000
    private const val KEY_LENGTH_BITS = 256
    private const val SALT_BYTES = 16
    private const val SEPARATOR = ":"
    private const val HEX = "0123456789abcdef"

    fun hash(password: String): String {
        val salt = ByteArray(SALT_BYTES).also { SecureRandom().nextBytes(it) }
        val hash = pbkdf2(password, salt, ITERATIONS, KEY_LENGTH_BITS)
        return listOf(ITERATIONS.toString(), toHex(salt), toHex(hash)).joinToString(SEPARATOR)
    }

    fun verify(password: String, stored: String): Boolean {
        val parts = stored.split(SEPARATOR)
        if (parts.size != 3) return false
        val iterations = parts[0].toIntOrNull() ?: return false
        val salt = fromHex(parts[1]) ?: return false
        val expected = fromHex(parts[2]) ?: return false
        if (expected.isEmpty()) return false
        val actual = pbkdf2(password, salt, iterations, expected.size * 8)
        return constantTimeEquals(expected, actual)
    }

    private fun pbkdf2(password: String, salt: ByteArray, iterations: Int, keyLengthBits: Int): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, keyLengthBits)
        return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).encoded
    }

    private fun constantTimeEquals(a: ByteArray, b: ByteArray): Boolean {
        if (a.size != b.size) return false
        var result = 0
        for (i in a.indices) result = result or (a[i].toInt() xor b[i].toInt())
        return result == 0
    }

    private fun toHex(bytes: ByteArray): String {
        val sb = StringBuilder(bytes.size * 2)
        for (b in bytes) {
            val v = b.toInt() and 0xFF
            sb.append(HEX[v ushr 4]).append(HEX[v and 0x0F])
        }
        return sb.toString()
    }

    private fun fromHex(hex: String): ByteArray? {
        if (hex.isEmpty() || hex.length % 2 != 0) return null
        val out = ByteArray(hex.length / 2)
        for (i in out.indices) {
            val hi = Character.digit(hex[i * 2], 16)
            val lo = Character.digit(hex[i * 2 + 1], 16)
            if (hi < 0 || lo < 0) return null
            out[i] = ((hi shl 4) or lo).toByte()
        }
        return out
    }
}
