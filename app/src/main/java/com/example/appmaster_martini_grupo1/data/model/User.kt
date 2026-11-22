package com.example.appmaster_martini_grupo1.data.model

import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

/** H-012: el email es único (antes se podían registrar dos cuentas con el mismo correo). */
@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val email: String,
    val password: String,
    val phone: String = "",
    val rut: String = ""
)
