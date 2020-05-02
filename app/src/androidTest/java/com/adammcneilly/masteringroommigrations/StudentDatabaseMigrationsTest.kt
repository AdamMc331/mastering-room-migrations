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

    @Test
    fun migrate2to3() {
        database = migrationTestHelper.createDatabase(TEST_DB, 2).apply {
            // Since we've created a database with version 2, we need to insert stuff manually, because
            // the  Room DAO interfaces are expecting the latest schema.
            execSQL(
                """
                INSERT INTO Student VALUES (1, 'Adam',  'McNeilly', 10) 
                """.trimIndent()
            )

            close()
        }

        database = migrationTestHelper.runMigrationsAndValidate(TEST_DB, 3, true, MIGRATION_2_3)

        val resultCursor = database.query("SELECT * FROM Student")

        assertTrue(resultCursor.moveToFirst())

        val idColumnIndex = resultCursor.getColumnIndex("id")
        val ageColumnIndex = resultCursor.getColumnIndex("age")
        val firstNameColumnIndex = resultCursor.getColumnIndex("firstName")
        val lastNameColumnIndex = resultCursor.getColumnIndex("lastName")

        // Make sure the lastName was dropped, and all other columns are good
        assertEquals(-1, lastNameColumnIndex)
        assertEquals(1, resultCursor.getInt(idColumnIndex))
        assertEquals("Adam", resultCursor.getString(firstNameColumnIndex))
        assertEquals(10, resultCursor.getInt(ageColumnIndex))
    }

    @Test
    fun migrate3to4() {
        database = migrationTestHelper.createDatabase(TEST_DB, 3).apply {
            // Since we've created a database with version 3, we need to insert stuff manually, because
            // the  Room DAO interfaces are expecting the latest schema.
            execSQL(
                """
                INSERT INTO Student VALUES (1, 'Adam', 10.0) 
                """.trimIndent()
            )

            close()
        }

        database = migrationTestHelper.runMigrationsAndValidate(TEST_DB, 4, true, MIGRATION_3_4)

        val resultCursor = database.query("SELECT * FROM Student")

        assertTrue(resultCursor.moveToFirst())

        val idColumnIndex = resultCursor.getColumnIndex("id")
        val ageColumnIndex = resultCursor.getColumnIndex("age")
        val firstNameColumnIndex = resultCursor.getColumnIndex("firstName")

        assertEquals(1, resultCursor.getInt(idColumnIndex))
        assertEquals("Adam", resultCursor.getString(firstNameColumnIndex))
        assertEquals(10.0, resultCursor.getDouble(ageColumnIndex), 0.0)
    }

    @Test
    fun migrateAll() {
        // Start at version 1
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

        // Migrate to current version 4
        database = migrationTestHelper.runMigrationsAndValidate(TEST_DB, 4, true, MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)

        // After this, we should have:
        // 1. a default age from migration 1 -> 2
        // 2. no last name from migration 2 -> 3
        // 3. age became a double from migration 3 -> 4
        val resultCursor = database.query("SELECT * FROM Student")

        assertTrue(resultCursor.moveToFirst())

        val idColumnIndex = resultCursor.getColumnIndex("id")
        val ageColumnIndex = resultCursor.getColumnIndex("age")
        val firstNameColumnIndex = resultCursor.getColumnIndex("firstName")
        val lastNameColumnIndex = resultCursor.getColumnIndex("lastName")

        // Make sure the lastName was dropped, and all other columns are good
        assertEquals(-1, lastNameColumnIndex)
        assertEquals(1, resultCursor.getInt(idColumnIndex))
        assertEquals("Adam", resultCursor.getString(firstNameColumnIndex))
        assertEquals(0.0, resultCursor.getDouble(ageColumnIndex), 0.0)
    }

    companion object  {
        private const val TEST_DB = "migration-test"
    }
}