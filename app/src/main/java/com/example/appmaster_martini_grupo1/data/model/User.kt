package com.example.appmaster_martini_grupo1.data.model

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val email: String,
    val password: String,
    val phone: String = "",
    val rut: String = ""
)