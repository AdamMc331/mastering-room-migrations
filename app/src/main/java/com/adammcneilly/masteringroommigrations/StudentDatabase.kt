package com.adammcneilly.masteringroommigrations

import android.content.Context
import androidx.room.*
import androidx.room.migration.AutoMigrationSpec

@Database(
    entities = [Student::class],
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(
        from = 2,
        to = 3,
        spec = StudentDatabase.AutoMigrationSpecFrom2To3::class
    )]
)
abstract class StudentDatabase : RoomDatabase() {
    abstract fun studentDAO(): StudentDAO

    @DeleteColumn(tableName = "Student", columnName = "lastName")
    class AutoMigrationSpecFrom2To3 : AutoMigrationSpec

    companion object {
        fun createDatabase(appContext: Context): StudentDatabase {
            return Room.databaseBuilder(
                appContext,
                StudentDatabase::class.java,
                "student-database.db"
            )
                .addMigrations(
                    MIGRATION_3_4,
                    MIGRATION_4_5,
                    MIGRATION_5_6
                )
                .allowMainThreadQueries()
                .build()
        }
    }
}