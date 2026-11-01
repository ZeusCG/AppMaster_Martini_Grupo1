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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ContentViewModel(
    private val repository: ContentRepository = ContentRepository(),
    private val favoriteRepository: FavoriteRepository = FavoriteRepository()
) : ViewModel() {

    private val allContent: List<Content> = repository.getContent()

    private val _content = MutableStateFlow(allContent)
    val content: StateFlow<List<Content>> = _content.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    val categories: List<String> = allContent.map { it.category }.distinct().sorted()

    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    init {
        applyFilters()
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
        _content.value = allContent.filter { item ->
            val matchesText = text.isEmpty() ||
                    item.title.lowercase().contains(text) ||
                    item.description.lowercase().contains(text)
            val matchesCategory = category == null || item.category == category
            matchesText && matchesCategory
        }
    }

    fun loadFavorites() {
        viewModelScope.launch {
            _favoriteIds.value = favoriteRepository.getFavoriteIds().toSet()
        }
    }

    fun isFavorite(contentId: String): Boolean = contentId in _favoriteIds.value

    fun toggleFavorite(contentId: String) {
        val nowFavorite = !isFavorite(contentId)
        _favoriteIds.value = if (nowFavorite) {
            _favoriteIds.value + contentId
        } else {
            _favoriteIds.value - contentId
        }
        viewModelScope.launch {
            if (nowFavorite) favoriteRepository.add(contentId)
            else favoriteRepository.remove(contentId)
        }
    }

    fun getContentById(id: String): Content? = repository.getContentById(id)

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val favoriteDao = AppDatabase.getInstance(context.applicationContext).favoriteDao()
                ContentViewModel(favoriteRepository = FavoriteRepository(favoriteDao))
            }
        }
    }
}