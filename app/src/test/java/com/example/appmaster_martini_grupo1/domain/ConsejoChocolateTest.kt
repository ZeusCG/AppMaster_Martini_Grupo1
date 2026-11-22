package com.example.appmaster_martini_grupo1.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class ConsejoChocolateTest {

    @Test
    fun condiciones_normales_son_favorables() {
        assertEquals(ConsejoChocolate.FAVORABLE, ConsejoChocolate.evaluar(temperatura = 18.0, humedad = 50))
    }

    @Test
    fun humedad_alta_da_aviso() {
        assertEquals(ConsejoChocolate.AVISO, ConsejoChocolate.evaluar(temperatura = 18.0, humedad = 70))
    }

    @Test
    fun temperatura_alta_da_aviso() {
        assertEquals(ConsejoChocolate.AVISO, ConsejoChocolate.evaluar(temperatura = 25.0, humedad = 40))
    }

    @Test
    fun muy_caluroso_y_humedo_da_aviso() {
        assertEquals(ConsejoChocolate.AVISO, ConsejoChocolate.evaluar(temperatura = 30.0, humedad = 80))
    }
}
