package com.adammcneilly.masteringroommigrations

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Student(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val firstName: String = "",
    @ColumnInfo(defaultValue = "0.0")
    val age: Double = 0.0
)