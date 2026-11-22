package com.example.appmaster_martini_grupo1.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed interface ClimaUiState {
    data object Loading : ClimaUiState
    data class Success(
        val temperatura: Double,
        val humedad: Int,
        val consejo: String
    ) : ClimaUiState

    data class Error(val mensaje: String) : ClimaUiState
}

class ClimaViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<ClimaUiState>(ClimaUiState.Loading)
    val uiState: StateFlow<ClimaUiState> = _uiState.asStateFlow()

    fun load() {
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer { ClimaViewModel() }
        }
    }
}
