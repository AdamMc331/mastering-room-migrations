package com.adammcneilly.masteringroommigrations

import android.content.Context
import androidx.room.*
import androidx.room.migration.AutoMigrationSpec

@Database(
    entities = [Student::class, University::class],
    version = 5,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(
            from = 2,
            to = 3,
            spec = StudentDatabase.AutoMigrationSpecFrom2To3::class
        ),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
        AutoMigration(
            from = 5, to = 6,
            spec = StudentDatabase.AutoMigrationSpecFrom5To6::class
        )]
)
abstract class StudentDatabase : RoomDatabase() {
    abstract fun studentDAO(): StudentDAO

    @DeleteColumn(tableName = "Student", columnName = "lastName")
    class AutoMigrationSpecFrom2To3 : AutoMigrationSpec

    @DeleteTable(tableName = "University")
    class AutoMigrationSpecFrom5To6 : AutoMigrationSpec

    companion object {
        fun createDatabase(appContext: Context): StudentDatabase {
            return Room.databaseBuilder(
                appContext,
                StudentDatabase::class.java,
                "student-database.db"
            )
                .allowMainThreadQueries()
                .build()
        }
    }
}