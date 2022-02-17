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
 *
 * Manually added University entity as dataclass and registered it in Student database as an entity,
 * Then added AutoMigration(from=4, to=5) on autoMigrations List
 *
 * this completes the job.
 */


/**
 * Database version 6 removed the University entity.
 *
 * Handled by AutoMigrationSpec with table to be deleted being specified.
 */
