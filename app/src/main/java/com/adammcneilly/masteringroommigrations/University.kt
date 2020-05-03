package com.adammcneilly.masteringroommigrations

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class University(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val schoolName: String = ""
)