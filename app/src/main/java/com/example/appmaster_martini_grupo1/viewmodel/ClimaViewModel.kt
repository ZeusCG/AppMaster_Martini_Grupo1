package com.example.appmaster_martini_grupo1.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.appmaster_martini_grupo1.data.local.AppDatabase
import com.example.appmaster_martini_grupo1.data.remote.RetrofitClient
import com.example.appmaster_martini_grupo1.data.repository.ClimaRepository
import com.example.appmaster_martini_grupo1.domain.ConsejoChocolate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ClimaUiState {
    data object Loading : ClimaUiState
    data class Success(
        val temperatura: Double,
        val humedad: Int,
        val consejo: String,
        val hora: String = "",
        val esUltimoDato: Boolean = false
    ) : ClimaUiState

    data class Error(val mensaje: String) : ClimaUiState
}

class ClimaViewModel(
    private val repository: ClimaRepository = ClimaRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<ClimaUiState>(ClimaUiState.Loading)
    val uiState: StateFlow<ClimaUiState> = _uiState.asStateFlow()

    /** Carga el clima. Si falla la red usa el último guardado; si no hay, muestra Error. */
    fun load() {
        _uiState.value = ClimaUiState.Loading
        viewModelScope.launch {
            try {
                val resultado = repository.obtenerClima()
                val clima = resultado.clima
                _uiState.value = ClimaUiState.Success(
                    temperatura = clima.temperatura,
                    humedad = clima.humedad,
                    consejo = ConsejoChocolate.evaluar(clima.temperatura, clima.humedad),
                    hora = clima.hora,
                    esUltimoDato = resultado.desdeCache
                )
            } catch (_: Exception) {
                _uiState.value = ClimaUiState.Error("No se pudo obtener el clima. Revisa tu conexión.")
            }
        }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val db = AppDatabase.getInstance(context.applicationContext)
                ClimaViewModel(ClimaRepository(RetrofitClient.climaApi, db.climaDao()))
            }
        }
    }
}
