package com.example.appmaster_martini_grupo1.data.repository

import com.example.appmaster_martini_grupo1.data.local.ContentSeed
import com.example.appmaster_martini_grupo1.data.model.Content

/**
 * Fuente de contenido del catálogo. Por ahora entrega la semilla en memoria;
 * D1.2 lo reemplaza por lectura desde Room (ContentDao).
 */
class ContentRepository {

    fun getContent(): List<Content> = ContentSeed.items

    fun getContentById(id: String): Content? = ContentSeed.items.firstOrNull { it.id == id }
}
