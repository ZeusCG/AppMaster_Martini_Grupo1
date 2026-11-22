package com.example.appmaster_martini_grupo1.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.appmaster_martini_grupo1.data.model.Content
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class ContentType { RECIPE, VIDEO, COURSE }

enum class ContentField {
    TITLE,
    CATEGORY,
    DESCRIPTION,
    PORTIONS,
    INGREDIENTS,
    STEPS,
    DURATION_MINUTES,
    URL,
    DURATION_HOURS,
    LEVEL,
    LESSONS
}

data class ContentForm(
    val id: String? = null,
    val type: ContentType = ContentType.RECIPE,
    val title: String = "",
    val category: String = "",
    val description: String = "",
    val portions: String = "",
    val ingredients: String = "",
    val steps: String = "",
    val durationMinutes: String = "",
    val url: String = "",
    val durationHours: String = "",
    val level: String = "",
    val lessons: String = "",
    val titleError: String? = null,
    val categoryError: String? = null,
    val descriptionError: String? = null,
    val portionsError: String? = null,
    val durationError: String? = null,
    val urlError: String? = null,
    val levelError: String? = null
)

data class ManageContentUiState(
    val items: List<Content> = emptyList(),
    val form: ContentForm = ContentForm(),
    val message: String? = null
)

class ManageContentViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ManageContentUiState())
    val uiState: StateFlow<ManageContentUiState> = _uiState.asStateFlow()

    fun startCreate() {
        _uiState.update { it.copy(form = ContentForm(), message = null) }
    }

    fun startEdit(id: String) {
    }

    fun onTypeChange(type: ContentType) {
        _uiState.update { it.copy(form = it.form.copy(type = type)) }
    }

    fun onFieldChange(field: ContentField, value: String) {
        _uiState.update { state ->
            val form = when (field) {
                ContentField.TITLE -> state.form.copy(title = value)
                ContentField.CATEGORY -> state.form.copy(category = value)
                ContentField.DESCRIPTION -> state.form.copy(description = value)
                ContentField.PORTIONS -> state.form.copy(portions = value)
                ContentField.INGREDIENTS -> state.form.copy(ingredients = value)
                ContentField.STEPS -> state.form.copy(steps = value)
                ContentField.DURATION_MINUTES -> state.form.copy(durationMinutes = value)
                ContentField.URL -> state.form.copy(url = value)
                ContentField.DURATION_HOURS -> state.form.copy(durationHours = value)
                ContentField.LEVEL -> state.form.copy(level = value)
                ContentField.LESSONS -> state.form.copy(lessons = value)
            }
            state.copy(form = form)
        }
    }

    fun save() {
    }

    fun delete(id: String) {
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer { ManageContentViewModel() }
        }
    }
}
