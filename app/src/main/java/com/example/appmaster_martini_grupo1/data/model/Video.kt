package com.example.appmaster_martini_grupo1.data.model

data class Video(
    override val id: String,
    override val title: String,
    override val description: String,
    override val category: String = "Videos técnicos",
    val durationMinutes: Int,
    val url: String
) : Content