package com.example.appmaster_martini_grupo1.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.appmaster_martini_grupo1.data.local.AppDatabase
import com.example.appmaster_martini_grupo1.data.model.Content
import com.example.appmaster_martini_grupo1.data.repository.ContentRepository
import com.example.appmaster_martini_grupo1.data.repository.FavoriteRepository
import com.example.appmaster_martini_grupo1.data.session.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ContentUiState {
    data object Loading : ContentUiState
    data class Success(val items: List<Content>) : ContentUiState
    data class Error(val message: String) : ContentUiState
}

class ContentViewModel(
    private val repository: ContentRepository = ContentRepository(),
    private val favoriteRepository: FavoriteRepository = FavoriteRepository()
) : ViewModel() {

    // Última lista completa (de Room o, en tests, de la semilla en memoria).
    private var allContent: List<Content> = repository.seed()

    private val _uiState = MutableStateFlow<ContentUiState>(ContentUiState.Loading)
    val uiState: StateFlow<ContentUiState> = _uiState.asStateFlow()

    private val _content = MutableStateFlow<List<Content>>(emptyList())
    val content: StateFlow<List<Content>> = _content.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    // Se calcula al vuelo: cuando `content` cambia, la pantalla se recompone y las lee actualizadas.
    val categories: List<String>
        get() = allContent.map { it.category }.distinct().sorted()

    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    init {
        applyFilters()
        if (repository.isPersistent) {
            observeContent()
        } else {
            _uiState.value = ContentUiState.Success(_content.value)
        }
    }

    private fun observeContent() {
        viewModelScope.launch {
            try {
                repository.observeAll().collect { items ->
                    allContent = items
                    applyFilters()
                    _uiState.value = ContentUiState.Success(_content.value)
                }
            } catch (_: Exception) {
                _uiState.value = ContentUiState.Error("No se pudo cargar el catálogo.")
            }
        }
    }

    fun onQueryChange(value: String) {
        _query.value = value
        applyFilters()
    }

    fun onCategorySelected(category: String?) {
        _selectedCategory.value = category
        applyFilters()
    }

    private fun applyFilters() {
        val text = _query.value.trim().lowercase()
        val category = _selectedCategory.value
        val filtered = allContent.filter { item ->
            val matchesText = text.isEmpty() ||
                    item.title.lowercase().contains(text) ||
                    item.description.lowercase().contains(text)
            val matchesCategory = category == null || item.category == category
            matchesText && matchesCategory
        }
        _content.value = filtered
        if (_uiState.value is ContentUiState.Success) {
            _uiState.value = ContentUiState.Success(filtered)
        }
    }

    fun loadFavorites() {
        val uid = UserSession.userId.value ?: return
        viewModelScope.launch {
            _favoriteIds.value = favoriteRepository.getFavoriteIds(uid).toSet()
        }
    }

    fun isFavorite(contentId: String): Boolean = contentId in _favoriteIds.value

    fun toggleFavorite(contentId: String) {
        val uid = UserSession.userId.value ?: return
        val nowFavorite = !isFavorite(contentId)
        _favoriteIds.value = if (nowFavorite) {
            _favoriteIds.value + contentId
        } else {
            _favoriteIds.value - contentId
        }
        viewModelScope.launch {
            if (nowFavorite) favoriteRepository.add(uid, contentId)
            else favoriteRepository.remove(uid, contentId)
        }
    }

    /** Búsqueda síncrona sobre la lista ya cargada (la usa la pantalla de detalle). */
    fun getContentById(id: String): Content? = allContent.firstOrNull { it.id == id }

    /** Inserta o actualiza contenido (lo usará la pantalla de gestión, D1.3). */
    fun saveContent(content: Content) {
        viewModelScope.launch { repository.upsert(content) }
    }

    /** Elimina contenido por id (lo usará la pantalla de gestión, D1.3). */
    fun deleteContent(id: String) {
        viewModelScope.launch { repository.delete(id) }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val db = AppDatabase.getInstance(context.applicationContext)
                ContentViewModel(
                    repository = ContentRepository(db.contentDao()),
                    favoriteRepository = FavoriteRepository(db.favoriteDao())
                )
            }
        }
    }
}
