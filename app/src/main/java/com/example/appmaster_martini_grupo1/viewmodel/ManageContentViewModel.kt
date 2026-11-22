package com.example.appmaster_martini_grupo1.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.appmaster_martini_grupo1.data.local.AppDatabase
import com.example.appmaster_martini_grupo1.data.model.Content
import com.example.appmaster_martini_grupo1.data.model.Course
import com.example.appmaster_martini_grupo1.data.model.Recipe
import com.example.appmaster_martini_grupo1.data.model.Video
import com.example.appmaster_martini_grupo1.data.repository.ContentRepository
import com.example.appmaster_martini_grupo1.data.repository.FavoriteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

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

class ManageContentViewModel(
    private val repository: ContentRepository = ContentRepository()
) : ViewModel() {

    private var allContent: List<Content> = repository.seed()

    private val _uiState = MutableStateFlow(ManageContentUiState(items = allContent))
    val uiState: StateFlow<ManageContentUiState> = _uiState.asStateFlow()

    init {
        if (repository.isPersistent) observeContent()
    }

    private fun observeContent() {
        viewModelScope.launch {
            repository.observeAll().collect { list ->
                allContent = list
                _uiState.update { it.copy(items = list) }
            }
        }
    }

    fun startCreate() {
        _uiState.update { it.copy(form = ContentForm(), message = null) }
    }

    fun startEdit(id: String) {
        val item = allContent.firstOrNull { it.id == id } ?: return
        _uiState.update { it.copy(form = item.toForm(), message = null) }
    }

    fun onTypeChange(type: ContentType) {
        _uiState.update { it.copy(form = it.form.copy(type = type)) }
    }

    fun onFieldChange(field: ContentField, value: String) {
        _uiState.update { state ->
            val form = when (field) {
                ContentField.TITLE -> state.form.copy(title = value, titleError = null)
                ContentField.CATEGORY -> state.form.copy(category = value, categoryError = null)
                ContentField.DESCRIPTION -> state.form.copy(description = value, descriptionError = null)
                ContentField.PORTIONS -> state.form.copy(portions = value, portionsError = null)
                ContentField.INGREDIENTS -> state.form.copy(ingredients = value)
                ContentField.STEPS -> state.form.copy(steps = value)
                ContentField.DURATION_MINUTES -> state.form.copy(durationMinutes = value, durationError = null)
                ContentField.URL -> state.form.copy(url = value, urlError = null)
                ContentField.DURATION_HOURS -> state.form.copy(durationHours = value, durationError = null)
                ContentField.LEVEL -> state.form.copy(level = value, levelError = null)
                ContentField.LESSONS -> state.form.copy(lessons = value)
            }
            state.copy(form = form)
        }
    }

    fun save() {
        val form = _uiState.value.form
        val validated = form.validate()
        if (validated.hasErrors()) {
            _uiState.update { it.copy(form = validated) }
            return
        }

        val wasEditing = form.id != null
        val content = validated.toContent(newId = "${form.type.name.lowercase()}-${UUID.randomUUID()}")
        val updatedList = allContent.filterNot { it.id == content.id } + content

        allContent = updatedList
        _uiState.update {
            it.copy(
                items = updatedList,
                form = ContentForm(),
                message = if (wasEditing) "Contenido actualizado" else "Contenido creado"
            )
        }

        if (repository.isPersistent) {
            viewModelScope.launch { repository.upsert(content) }
        }
    }

    fun delete(id: String) {
        allContent = allContent.filterNot { it.id == id }
        _uiState.update { it.copy(items = allContent, message = "Contenido eliminado") }

        if (repository.isPersistent) {
            viewModelScope.launch { repository.delete(id) }
        }
    }

    companion object {
        const val MAX_TITLE = 80
        const val MAX_CATEGORY = 40
        const val MAX_DESCRIPTION = 300

        fun factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val db = AppDatabase.getInstance(context.applicationContext)
                ManageContentViewModel(
                    repository = ContentRepository(
                        contentDao = db.contentDao(),
                        favoriteRepository = FavoriteRepository(db.favoriteDao())
                    )
                )
            }
        }
    }
}

// --- Validación del formulario ---

private fun ContentForm.hasErrors(): Boolean =
    listOf(titleError, categoryError, descriptionError, portionsError, durationError, urlError, levelError)
        .any { it != null }

private fun ContentForm.validate(): ContentForm {
    val titleError = when {
        title.isBlank() -> "Ingresa el título"
        title.trim().length > ManageContentViewModel.MAX_TITLE ->
            "Máximo ${ManageContentViewModel.MAX_TITLE} caracteres"
        else -> null
    }
    val categoryError = when {
        category.isBlank() -> "Ingresa la categoría"
        category.trim().length > ManageContentViewModel.MAX_CATEGORY ->
            "Máximo ${ManageContentViewModel.MAX_CATEGORY} caracteres"
        else -> null
    }
    val descriptionError = if (description.trim().length > ManageContentViewModel.MAX_DESCRIPTION) {
        "Máximo ${ManageContentViewModel.MAX_DESCRIPTION} caracteres"
    } else null

    val portionsError = if (type == ContentType.RECIPE && portions.isNotBlank() && (portions.toIntOrNull() == null || portions.toInt() <= 0)) {
        "Ingresa un número mayor a 0"
    } else null

    val durationText = if (type == ContentType.VIDEO) durationMinutes else if (type == ContentType.COURSE) durationHours else ""
    val durationError = if (durationText.isNotBlank() && (durationText.toIntOrNull() == null || durationText.toInt() <= 0)) {
        "Ingresa un número mayor a 0"
    } else null

    val urlError = if (type == ContentType.VIDEO && url.isNotBlank() && !url.trim().startsWith("http")) {
        "El enlace debe empezar con http"
    } else null

    return copy(
        titleError = titleError,
        categoryError = categoryError,
        descriptionError = descriptionError,
        portionsError = portionsError,
        durationError = durationError,
        urlError = urlError
    )
}

private fun ContentForm.toContent(newId: String): Content {
    val idFinal = id ?: newId
    return when (type) {
        ContentType.RECIPE -> Recipe(
            id = idFinal,
            title = title.trim(),
            description = description.trim(),
            category = category.trim(),
            portions = portions.toIntOrNull() ?: 0,
            ingredients = ingredients.toLines(),
            steps = steps.toLines()
        )

        ContentType.VIDEO -> Video(
            id = idFinal,
            title = title.trim(),
            description = description.trim(),
            category = category.trim(),
            durationMinutes = durationMinutes.toIntOrNull() ?: 0,
            url = url.trim()
        )

        ContentType.COURSE -> Course(
            id = idFinal,
            title = title.trim(),
            description = description.trim(),
            category = category.trim(),
            durationHours = durationHours.toIntOrNull() ?: 0,
            level = level.trim(),
            lessons = lessons.toLines()
        )
    }
}

private fun Content.toForm(): ContentForm = when (this) {
    is Recipe -> ContentForm(
        id = id, type = ContentType.RECIPE, title = title, category = category,
        description = description, portions = portions.toString(),
        ingredients = ingredients.joinToString("\n"), steps = steps.joinToString("\n")
    )

    is Video -> ContentForm(
        id = id, type = ContentType.VIDEO, title = title, category = category,
        description = description, durationMinutes = durationMinutes.toString(), url = url
    )

    is Course -> ContentForm(
        id = id, type = ContentType.COURSE, title = title, category = category,
        description = description, durationHours = durationHours.toString(),
        level = level, lessons = lessons.joinToString("\n")
    )

    else -> ContentForm()
}

private fun String.toLines(): List<String> =
    split("\n").map { it.trim() }.filter { it.isNotEmpty() }
