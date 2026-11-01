package com.example.appmaster_martini_grupo1.data.repository

import com.example.appmaster_martini_grupo1.data.model.Content
import com.example.appmaster_martini_grupo1.data.model.Course
import com.example.appmaster_martini_grupo1.data.model.Recipe
import com.example.appmaster_martini_grupo1.data.model.Video

class ContentRepository {
    private val sampleContent: List<Content> = listOf(
        Recipe(
            id = "recipe-001",
            title = "Ganache de chocolate semiamargo",
            description = "Base cremosa para rellenos y baños, ideal para vitrina.",
            category = "Chocolatería",
            portions = 12,
            ingredients = listOf(
                "300 g de chocolate semiamargo",
                "200 ml de crema para batir",
                "30 g de mantequilla sin sal"
            ),
            steps = listOf(
                "Calentar la crema sin llegar a hervir.",
                "Verter sobre el chocolate picado y reposar 1 minuto.",
                "Incorporar la mantequilla y emulsionar hasta brillar."
            )
        ),
        Recipe(
            id = "recipe-002",
            title = "Hojaldre rápido para empanadas",
            description = "Masa de hoja simple para producción de bollería salada.",
            category = "Pastelería",
            portions = 10,
            ingredients = listOf(
                "500 g de harina sin polvos",
                "250 g de mantequilla fría",
                "150 ml de agua helada",
                "5 g de sal"
            ),
            steps = listOf(
                "Formar la masa sin amasar en exceso.",
                "Hacer tres dobleces con descanso en frío.",
                "Estirar y cortar según el formato."
            )
        ),
        Video(
            id = "video-001",
            title = "Templado de chocolate paso a paso",
            description = "Técnica de temperado por siembra para acabado brillante.",
            category = "Videos técnicos",
            durationMinutes = 8,
            url = "https://ejemplo.mastermartini.cl/videos/templado"
        ),
        Video(
            id = "video-002",
            title = "Montaje de vitrina de pastelería",
            description = "Criterios de orden, altura y rotación de productos.",
            category = "Videos técnicos",
            durationMinutes = 12,
            url = "https://ejemplo.mastermartini.cl/videos/vitrina"
        ),
        Course(
            id = "course-001",
            title = "Pastelería de vitrina",
            description = "Curso de especialidad para montaje y exhibición de tortas.",
            category = "Cursos",
            durationHours = 20,
            level = "Intermedio",
            lessons = listOf(
                "Bizcochos base",
                "Rellenos y coberturas",
                "Montaje de vitrina"
            )
        ),
        Course(
            id = "course-002",
            title = "Chocolatería artesanal",
            description = "Bombones, coberturas y control de temperatura del chocolate.",
            category = "Cursos",
            durationHours = 30,
            level = "Avanzado",
            lessons = listOf(
                "Materias primas",
                "Atemperado",
                "Moldes y bombones"
            )
        )
    )

    fun getContent(): List<Content> = sampleContent
    fun getContentById(id: String): Content? = sampleContent.firstOrNull { it.id == id }
}