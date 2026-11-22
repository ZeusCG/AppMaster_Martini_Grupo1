package com.example.appmaster_martini_grupo1.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordHasherTest {

    private val password = "Martini2026"

    @Test
    fun hash_no_contiene_la_password_en_texto_plano() {
        val hash = PasswordHasher.hash(password)

        assertFalse(hash.contains(password))
        assertNotEquals(password, hash)
    }

    @Test
    fun verify_acepta_la_password_correcta() {
        val hash = PasswordHasher.hash(password)

        assertTrue(PasswordHasher.verify(password, hash))
    }

    @Test
    fun verify_rechaza_la_password_incorrecta() {
        val hash = PasswordHasher.hash(password)

        assertFalse(PasswordHasher.verify("OtraClave1", hash))
    }

    @Test
    fun dos_hashes_de_la_misma_password_son_distintos_por_la_sal() {
        val hashA = PasswordHasher.hash(password)
        val hashB = PasswordHasher.hash(password)

        assertNotEquals(hashA, hashB)
        assertTrue(PasswordHasher.verify(password, hashA))
        assertTrue(PasswordHasher.verify(password, hashB))
    }

    @Test
    fun hash_tiene_tres_partes_iteraciones_sal_y_hash() {
        val partes = PasswordHasher.hash(password).split(":")

        assertEquals(3, partes.size)
    }

    @Test
    fun verify_de_un_texto_corrupto_devuelve_false() {
        assertFalse(PasswordHasher.verify(password, "no-es-un-hash"))
        assertFalse(PasswordHasher.verify(password, "10000:zz:zz"))
        assertFalse(PasswordHasher.verify(password, ""))
    }
}
