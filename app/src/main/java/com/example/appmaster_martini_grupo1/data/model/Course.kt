package com.example.appmaster_martini_grupo1.data.model

data class Course(
    override val id: String,
    override val title: String,
    override val description: String,
    override val category: String = "Cursos",
    val durationHours: Int,
    val level: String,
    val lessons: List<String>
) : Content