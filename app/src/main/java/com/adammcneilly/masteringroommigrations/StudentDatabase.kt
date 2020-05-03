package com.adammcneilly.masteringroommigrations

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Student::class], version = 6)
abstract class StudentDatabase : RoomDatabase() {
    abstract fun studentDAO(): StudentDAO

    companion object {
        fun createDatabase(appContext: Context): StudentDatabase {
            return Room.databaseBuilder(
                appContext,
                StudentDatabase::class.java,
                "student-database.db"
            )
                .addMigrations(
                    MIGRATION_1_2,
                    MIGRATION_2_3,
                    MIGRATION_3_4,
                    MIGRATION_4_5,
                    MIGRATION_5_6
                )
                .allowMainThreadQueries()
                .build()
        }
    }
}