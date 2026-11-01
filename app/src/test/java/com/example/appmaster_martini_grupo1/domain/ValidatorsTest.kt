package com.example.appmaster_martini_grupo1.domain

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidatorsTest {
    @Test
    fun email_valido() {
        assertTrue(Validators.isValidEmail("cliente@mastermartini.cl"))
    }

    @Test
    fun email_invalido() {
        assertFalse(Validators.isValidEmail("cliente@"))
        assertFalse(Validators.isValidEmail("sin-arroba.cl"))
        assertFalse(Validators.isValidEmail(""))
    }

    @Test
    fun telefono_valido() {
        assertTrue(Validators.isValidPhone("912345678"))
        assertTrue(Validators.isValidPhone("+56912345678"))
    }

    @Test
    fun telefono_invalido() {
        assertFalse(Validators.isValidPhone("123"))
        assertFalse(Validators.isValidPhone("abcdefgh"))
    }

    @Test
    fun password_valida() {
        assertTrue(Validators.isValidPassword("Martini2026"))
    }

    @Test
    fun password_invalida() {
        assertFalse(Validators.isValidPassword("corta1"))
        assertFalse(Validators.isValidPassword("sinnumero"))
        assertFalse(Validators.isValidPassword("12345678"))
    }

    @Test
    fun rut_valido() {
        assertTrue(Validators.isValidRut("12345678-5"))
        assertTrue(Validators.isValidRut("12.345.678-5"))
    }

    @Test
    fun rut_invalido() {
        assertFalse(Validators.isValidRut("12345678-4"))
        assertFalse(Validators.isValidRut("abc"))
        assertFalse(Validators.isValidRut(""))
    }
}