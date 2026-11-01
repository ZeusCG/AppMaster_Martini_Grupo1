package com.example.appmaster_martini_grupo1.data.model

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.PrimaryKey

@Entity(
    tableName = "profiles",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Profile(
    @PrimaryKey
    val userId: Long,
    val displayName: String,
    val email: String,
    val phone: String = "",
    val photoUri: String? = null,
    val interestsCsv: String = ""
)