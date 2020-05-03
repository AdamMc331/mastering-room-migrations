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

    @Insert
    fun insertUniversity(university: University): Long

    @Query("SELECT * FROM University")
    fun fetchUniversities(): List<University>
}