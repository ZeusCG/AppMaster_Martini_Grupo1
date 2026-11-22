package com.example.appmaster_martini_grupo1.viewmodel

import com.example.appmaster_martini_grupo1.MainDispatcherRule
import com.example.appmaster_martini_grupo1.data.model.ClimaActual
import com.example.appmaster_martini_grupo1.data.model.ClimaResultado
import com.example.appmaster_martini_grupo1.data.repository.ClimaRepository
import com.example.appmaster_martini_grupo1.domain.ConsejoChocolate
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ClimaViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = mockk<ClimaRepository>()

    @Test
    fun load_con_red_expone_success() = runTest {
        coEvery { repository.obtenerClima() } returns ClimaResultado(
            ClimaActual(temperatura = 18.0, humedad = 50, hora = "2026-11-22T10:00"),
            desdeCache = false
        )
        val viewModel = ClimaViewModel(repository)

        viewModel.load()

        val estado = viewModel.uiState.value
        assertTrue(estado is ClimaUiState.Success)
        estado as ClimaUiState.Success
        assertEquals(18.0, estado.temperatura, 0.0)
        assertEquals(ConsejoChocolate.FAVORABLE, estado.consejo)
        assertFalse(estado.esUltimoDato)
    }

    @Test
    fun load_sin_red_con_cache_marca_ultimo_dato() = runTest {
        coEvery { repository.obtenerClima() } returns ClimaResultado(
            ClimaActual(temperatura = 30.0, humedad = 80, hora = "2026-11-21T10:00"),
            desdeCache = true
        )
        val viewModel = ClimaViewModel(repository)

        viewModel.load()

        val estado = viewModel.uiState.value as ClimaUiState.Success
        assertTrue(estado.esUltimoDato)
        assertEquals(30.0, estado.temperatura, 0.0)
        assertEquals(ConsejoChocolate.AVISO, estado.consejo)
    }

    @Test
    fun load_sin_red_ni_cache_expone_error() = runTest {
        coEvery { repository.obtenerClima() } throws RuntimeException("sin red")
        val viewModel = ClimaViewModel(repository)

        viewModel.load()

        assertTrue(viewModel.uiState.value is ClimaUiState.Error)
    }
}
