package com.adammcneilly.masteringroommigrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * When Migrating from version 1 to version 2, we added the age property on the student
 * entity.
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Student ADD COLUMN age INTEGER NOT NULL DEFAULT 0")
    }
}

/**
 * When migrating from version 2 to version 3, we removed the last name property on the student
 * entity.
 *
 * Note that we can't alter a SQLite table to remove a column: https://www.techonthenet.com/sqlite/tables/alter_table.php
 *
 * Which means we must create a backup, move everything, bring it back, and then we're good.
 */
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE Student_backup (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, firstName TEXT NOT NULL, age INTEGER NOT NULL)")
        database.execSQL("INSERT INTO Student_backup (id, firstName, age) SELECT id, firstName, age FROM Student")
        database.execSQL("DROP TABLE Student")
        database.execSQL("ALTER TABLE Student_backup RENAME TO Student")
    }
}

/**
 * When migration from version 3 to version 4, we changed the data type of a student's age from an
 * int to a double.
 *
 * Note that we can't alter a SQLite table to remove a column: https://www.techonthenet.com/sqlite/tables/alter_table.php
 *
 * Which means we must create a backup, move everything, bring it back, and then we're good.
 */
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE Student_backup (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, firstName TEXT NOT NULL, age REAL NOT NULL)")
        database.execSQL("INSERT INTO Student_backup (id, firstName, age) SELECT id, firstName, age FROM Student")
        database.execSQL("DROP TABLE Student")
        database.execSQL("ALTER TABLE Student_backup RENAME TO Student")
    }
}

/**
 * Database version 5 added the new [University] entity.
 */
val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE University (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, schoolName TEXT NOT NULL)")
    }
}