package com.adammcneilly.masteringroommigrations

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StudentDatabaseMigrationsTest {

    private lateinit var database: SupportSQLiteDatabase

    @JvmField
    @Rule
    val migrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        StudentDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun migrate1to2() {
        database = migrationTestHelper.createDatabase(TEST_DB, 1).apply {
            // Since we've created a database with version 1, we need to insert stuff manually, because
            // the  Room DAO interfaces are expecting the latest schema.
            execSQL(
                """
                INSERT INTO Student VALUES (1, 'Adam',  'McNeilly') 
                """.trimIndent()
            )

            close()
        }

        database = migrationTestHelper.runMigrationsAndValidate(TEST_DB, 2, true, MIGRATION_1_2)

        val resultCursor = database.query("SELECT * FROM Student")

        // Let's make sure we can find the  age column, and that it's equal to our default.
        // We can also validate the name is the one we inserted.
        assertTrue(resultCursor.moveToFirst())

        val ageColumnIndex = resultCursor.getColumnIndex("age")
        val nameColumnIndex = resultCursor.getColumnIndex("firstName")

        val ageFromDatabase = resultCursor.getInt(ageColumnIndex)
        val nameFromDatabase = resultCursor.getString(nameColumnIndex)

        assertEquals(0, ageFromDatabase)
        assertEquals("Adam", nameFromDatabase)
    }

    companion object  {
        private const val TEST_DB = "migration-test"
    }
}