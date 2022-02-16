package com.adammcneilly.masteringroommigrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * When Migrating from version 1 to version 2, we added the age property on the student
 * entity.
 *
 * This is captured by AutoMigrate option that was introduced in room v2.4.0-alpha01
 *
 * By adding `autoMigrations = [AutoMigration(from = 1 ,to = 2)]` on the StudentDatabase
 * abstract class, room will utilise the exported schema from previous version and compare
 * with the current version definition and implement the changes effectively.
 */


/**
 * When migrating from version 2 to version 3, we removed the last name property on the student
 * entity.
 *
 * Using AutoMigrations, AutoMigrationSpec needs to be provided with an annotation of
 * @DeleteColumn, this takes in the table name to be acted upon and the column in question.
 *
 * Read more on https://developer.android.com/reference/kotlin/androidx/room/DeleteColumn
 */


/**
 * When migration from version 3 to version 4, we changed the data type of a student's age from an
 * int to a double.
 *
 *
 * AutoMigration does the job gracefully of changing the property data type without any intervention.
 */

/**
 * Database version 5 added the new University entity.
 */
val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE University (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, schoolName TEXT NOT NULL)")
    }
}

/**
 * Database version 6 removed the University entity.
 */
val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE University")
    }
}