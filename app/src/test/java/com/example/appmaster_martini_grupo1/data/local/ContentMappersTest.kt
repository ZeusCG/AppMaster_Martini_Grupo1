package com.example.appmaster_martini_grupo1.data.local

import com.example.appmaster_martini_grupo1.data.model.Course
import com.example.appmaster_martini_grupo1.data.model.Recipe
import com.example.appmaster_martini_grupo1.data.model.Video
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** H-022/H-026: prueba el mapeo Room<->dominio de los tres tipos y el caso de tipo desconocido. */
class ContentMappersTest {

    @Test
    fun receta_va_y_vuelve_sin_perder_datos() {
        val recipe = Recipe(
            id = "r1", title = "Ganache", description = "Base", category = "Chocolatería",
            portions = 4, ingredients = listOf("a", "b"), steps = listOf("p1")
        )

        val vuelta = recipe.toEntity().toContent()

        assertEquals(recipe, vuelta)
    }

    @Test
    fun video_guarda_y_lee_duracion_y_url() {
        val video = Video(
            id = "v1", title = "Templado", description = "Técnica", category = "Videos técnicos",
            durationMinutes = 8, url = "https://x"
        )

        val entity = video.toEntity()

        assertEquals(TYPE_VIDEO, entity.type)
        assertEquals(video, entity.toContent())
    }

    @Test
    fun curso_guarda_y_lee_nivel_y_lecciones() {
        val course = Course(
            id = "c1", title = "Vitrina", description = "Curso", category = "Cursos",
            durationHours = 20, level = "Intermedio", lessons = listOf("l1", "l2")
        )

        val entity = course.toEntity()

        assertEquals(TYPE_COURSE, entity.type)
        assertEquals(course, entity.toContent())
    }

    @Test
    fun tipo_desconocido_falla_en_vez_de_asumir_curso() {
        val entity = ContentEntity(
            id = "x", type = "RARO", title = "T", description = "D", category = "C"
        )

        var fallo = false
        try {
            entity.toContent()
        } catch (_: IllegalStateException) {
            fallo = true
        }

        assertTrue(fallo)
    }
}
