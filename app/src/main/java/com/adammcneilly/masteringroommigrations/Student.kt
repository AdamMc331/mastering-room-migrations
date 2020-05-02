package com.adammcneilly.masteringroommigrations

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Student(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val firstName: String = "",
    val lastName: String = "",
    val age: Int = 0
)