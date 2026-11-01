package com.example.appmaster_martini_grupo1.data.model

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "favorites")
data class Favorite(
    @PrimaryKey val contentId: String
)