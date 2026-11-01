package com.example.appmaster_martini_grupo1.viewmodel

import androidx.lifecycle.ViewModel
import com.example.appmaster_martini_grupo1.data.model.Content
import com.example.appmaster_martini_grupo1.data.repository.ContentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ContentViewModel(
    private val repository: ContentRepository = ContentRepository()
) : ViewModel() {

    private val _content = MutableStateFlow<List<Content>>(emptyList())
    val content: StateFlow<List<Content>> = _content.asStateFlow()

    init {
        loadContent()
    }

    private fun loadContent() {
        _content.value = repository.getContent()
    }

    fun getContentById(id: String): Content? = repository.getContentById(id)
}