# Room Migration Cheat Sheet

Database migrations in Room initially required developers to write SQL statements manually that are executed in the database.

Currently, Room provides options to automate your migrations since version [2.4.0-alpha01](https://developer.android.com/jetpack/androidx/releases/room#2.2.0-alpha01), which allows developers to utilise prebuilt classes and annotations to perform automated migrations on simple changes involving adding, deleting, renaming columns and databases.

As for complex schema changes, Room may not be able to generate appropriate migrations paths automatically, this includes scenarios such as splitting a table. In such cases, a developer is required to manually define migration path by implementing a `Migration` class. This cheat sheet serves as a reminder of the SQL required for various migrations, automated and manual.

# Types Of Migrations

There are a number of changes to your database that could require a migration between versions. We can have one or more of these changes in a given migration:

1. [Adding a field to an existing table.](#Adding-A-Field-To-An-Existing-Table)
2. [Removing a field from an existing table.](#Removing-A-Field-From-An-Existing-Table)
3. [Changing the data type or column name of a field from an existing table.](#Changing-The-Data-Type-Of-A-Field)
4. [Adding a new table to the database.](#Adding-A-New-Entity)
5. [Removing a table from the database.](#Removing-An-Entity)

If you have found yourself writing a database migration that's not in the above list, create an Issue and/or Pull Request so we can get it added to the list. :)

# Understanding Table And Column Names

It's important to remember how Room entities map to the table name and column names used in the database.

If we have an entity like this:

```kotlin
@Entity
data class Student(
    val id: Long = 0L,
    val firstName: String = ""
)
```

Then we have a table named `Student`, with two columns named `id` and `firstName`. It maps directly from class and property names. If you want granular control over this, you can add those properties inside the annotations:

```kotlin
@Entity(tableName = "student_table")
data class Student(
    val id: Long = 0L,
    @ColumnInfo(name = "first_name")
    val firstName: String = ""
)
```

# Adding A Field To An Existing Table

## Using AutoMigration

With automated Migrations, you need to add `autoMigrations = [AutoMigration(from = x, to = y)` to your `@Database` annotation, here, Room will
use your exported schema from previous version to check the changes and determine which new column exists in the later versions and needs to be added.

```kotlin
// Database class before the version update.
@Database(
  version = 1,
  entities = [Student::class]
)
abstract class StudentDatabase : RoomDatabase() {

}

// Database class after the version update.
@Database(
  version = 2,
  entities = [Student::class],
  autoMigrations = [
    AutoMigration (from = 1, to = 2)
  ]
)
abstract class StudentDatabase : RoomDatabase() {

}
```

In the event that the new field has NOT NULL constraint, you will be required to provide a value for use in the already existing entries in the table.
Previously before Room version 2.2.0, default values were provided via SQL, which resulted in default values that Room was not aware of.

As of version [2.2.0-alpha01](https://developer.android.com/jetpack/androidx/releases/room#2.2.0-alpha01), Room provides for `@ColumnInfo(defaultValue = "...")` annotation which Room knows of it's existence.

```kotlin
@Entity(tableName = "student_table")
data class Student(
    val id: Long = 0L,
    @ColumnInfo(name = "first_name")
    val firstName: String = ""
    @ColumnInfo(defaultValue = "0")
    val age : Int = 0
)
```


## The manual way

To add a new column, we can use the `ALTER TABLE` command:

```kotlin
database.execSQL("ALTER TABLE table_name ADD COLUMN columnName TYPE [NULL|NOT NULL] [DEFAULT default]")
```

A default is required if your new type is not nullable:

```kotlin
database.execSQL("ALTER TABLE Student ADD COLUMN age INTEGER NOT NULL DEFAULT 0")
```

If your new type is nullable, a default is not required:

```kotlin
database.execSQL("ALTER TABLE Student ADD COLUMN nickName TEXT NULL")
```

You can see a pull request diff of this type of migration [here](https://github.com/AdamMc331/mastering-room-migrations/pull/1).

# Removing A Field From An Existing Table

## AutoMigration
This is similar to adding a column, however, as the developer, you need to explicitly tell Room which column needs to be purged.
This is where the [`AutoMigrationSpec`](https://developer.android.com/reference/kotlin/androidx/room/migration/AutoMigrationSpec) class comes in, this class gives Room additional information that it needs to correctly generate migration paths.

Step 1. Create a class that extends AutoMigrationSpec inside your database class, then annotate it with @DeleteColumn providing more info inside the annotation.

```kotlin
@DeleteColumn(tableName = "table_name", columnName  "columnToDelete")
class RemoveColumnSpec : AutoMigrationSpec
```

Step 2: Add the class to AutoMigration entry.

```kotlin
AutoMigration(from = x, to = y, spec = RemoveColumnSpec::class)
```

That's all, your column will be deleted.

## Manual way

SQLite does not support removing a column from a table directly. Instead we need to do the following:

1. Create a backup table that has the schema we actually want.
2. Copy everything from the original table into the backup.
3. Delete the original table.
4. Rename the backup table to the same name as the original.

Here is an example of this type of change:

```kotlin
database.execSQL("CREATE TABLE Student_backup (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, firstName TEXT NOT NULL, age INTEGER NOT NULL)")
database.execSQL("INSERT INTO Student_backup (id, firstName, age) SELECT id, firstName, age FROM Student")
database.execSQL("DROP TABLE Student")
database.execSQL("ALTER TABLE Student_backup RENAME TO Student")
```

You can see a pull request implementing and testing this type of migration [here](https://github.com/AdamMc331/mastering-room-migrations/pull/2).

# Changing The Data Type Of A Field

## Using AutoMigration

Here, you only need to add `AutoMigration(from = x, to = y)` to your `@Database` and that's all. Room will do the manual hard work under the hood for you.


## Manual Way

Similar to the removing a field example, SQLite doesn't support the action of changing a data type. Instead, we need to create a back up table, copy everything over, and rename it.

Keep in mind the type of data is important in this change. In the [sample pull request](https://github.com/AdamMc331/mastering-room-migrations/pull/3) we are moving from an integer to a floating point number. Moving from a number to a string type may be more complicated.

Here is an example of this change:

```kotlin
database.execSQL("CREATE TABLE Student_backup (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, firstName TEXT NOT NULL, age REAL NOT NULL)")
database.execSQL("INSERT INTO Student_backup (id, firstName, age) SELECT id, firstName, age FROM Student")
database.execSQL("DROP TABLE Student")
database.execSQL("ALTER TABLE Student_backup RENAME TO Student")
```

# Adding A New Entity

## AutoMigration

Once you have your entity defined in your project, go ahead and add `autoMigrations = [AutoMigration(from = x, to = y)` to your `@Database` annotation.

## Manual Way

When adding a new entity to your project, all you need is the relevant create table statement:

```kotlin
database.execSQL("CREATE TABLE University (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, schoolName TEXT NOT NULL)")
```

If you have trouble determining what the right syntax is, you can always look at the [json file that room generates](app/schemas/com.adammcneilly.masteringroommigrations.StudentDatabase/5.json).

You can find the pull request demonstrating that [here](https://github.com/AdamMc331/mastering-room-migrations/pull/4).

# Removing An Entity

When removing an entity from your project, the only SQL needed is a drop table statement:

```kotlin
database.execSQL("DROP TABLE University")
``` 

You can find a pull request for that [here](https://github.com/AdamMc331/mastering-room-migrations/pull/5).

Read more on AutoMigrations [here](https://developer.android.com/training/data-storage/room/migrating-db-versions#automated).
