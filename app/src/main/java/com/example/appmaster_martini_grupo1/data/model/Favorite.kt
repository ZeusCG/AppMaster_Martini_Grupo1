package com.example.appmaster_martini_grupo1.data.model

import androidx.room3.Entity

@Entity(tableName = "favorites", primaryKeys = ["userId", "contentId"])
data class Favorite(
    val userId: Long,
    val contentId: String
)
