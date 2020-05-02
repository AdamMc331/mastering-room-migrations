package com.adammcneilly.masteringroommigrations

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StudentDatabaseTest {

    private lateinit var database: StudentDatabase

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        database = Room.inMemoryDatabaseBuilder(
            context,
            StudentDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.clearAllTables()
        database.close()
    }

    @Test
    fun insertReadStudent() {
        val testStudent = Student(
            firstName = "Adam",
            lastName = "McNeilly"
        )

        val newId = database.studentDAO().insertStudent(testStudent)

        val expectedStudent = testStudent.copy(id = newId)
        val studentList = database.studentDAO().fetchStudents()
        assertEquals(1, studentList.size)
        assertEquals(expectedStudent, studentList.first())
    }
}