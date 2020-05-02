package com.adammcneilly.masteringroommigrations

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface StudentDAO {
    @Insert
    fun insertStudent(student: Student): Long

    @Query("SELECT * FROM Student")
    fun fetchStudents(): List<Student>
}