package com.example.appmaster_martini_grupo1.data.model

data class Interest(
    val id: String,
    val label: String
)

val defaultInterests: List<Interest> = listOf(
    Interest("recetas", "Recetas"),
    Interest("videos", "Videos"),
    Interest("cursos", "Cursos"),
    Interest("reposteria", "Repostería"),
    Interest("panaderia", "Panadería"),
    Interest("pizzas", "Pizzas"),
    Interest("carnes", "Carnes"),
    Interest("postres", "Postres"),
    Interest("cocteles", "Cócteles")
)