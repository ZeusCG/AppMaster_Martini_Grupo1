package com.example.appmaster_martini_grupo1.data.local

import com.example.appmaster_martini_grupo1.data.model.Content
import com.example.appmaster_martini_grupo1.data.model.Course
import com.example.appmaster_martini_grupo1.data.model.Recipe
import com.example.appmaster_martini_grupo1.data.model.Video

const val TYPE_RECIPE = "RECIPE"
const val TYPE_VIDEO = "VIDEO"
const val TYPE_COURSE = "COURSE"

private const val ITEM_SEPARATOR = "\n"

fun ContentEntity.toContent(): Content = when (type) {
    TYPE_RECIPE -> Recipe(
        id = id,
        title = title,
        description = description,
        category = category,
        portions = portions ?: 0,
        ingredients = ingredients.toItems(),
        steps = steps.toItems()
    )

    TYPE_VIDEO -> Video(
        id = id,
        title = title,
        description = description,
        category = category,
        durationMinutes = durationMinutes ?: 0,
        url = url.orEmpty()
    )

    TYPE_COURSE -> Course(
        id = id,
        title = title,
        description = description,
        category = category,
        durationHours = durationHours ?: 0,
        level = level.orEmpty(),
        lessons = lessons.toItems()
    )

    else -> error("Tipo de contenido desconocido: $type")
}

fun Content.toEntity(): ContentEntity = when (this) {
    is Recipe -> ContentEntity(
        id = id,
        type = TYPE_RECIPE,
        title = title,
        description = description,
        category = category,
        portions = portions,
        ingredients = ingredients.joinToString(ITEM_SEPARATOR),
        steps = steps.joinToString(ITEM_SEPARATOR)
    )

    is Video -> ContentEntity(
        id = id,
        type = TYPE_VIDEO,
        title = title,
        description = description,
        category = category,
        durationMinutes = durationMinutes,
        url = url
    )

    is Course -> ContentEntity(
        id = id,
        type = TYPE_COURSE,
        title = title,
        description = description,
        category = category,
        durationHours = durationHours,
        level = level,
        lessons = lessons.joinToString(ITEM_SEPARATOR)
    )

    else -> error("Tipo de contenido no soportado: ${this::class.simpleName}")
}

private fun String?.toItems(): List<String> =
    this?.split(ITEM_SEPARATOR)?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()
