package com.example.appmaster_martini_grupo1.data.model

data class Recipe(
    override val id: String,
    override val title: String,
    override val description: String,
    override val category: String = "Recetas",
    val portions: Int,
    val ingredients: List<String>,
    val steps: List<String>
) : Content