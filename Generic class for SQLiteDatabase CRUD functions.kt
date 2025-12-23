// CrudHelper abstracts SQLite boilerplate and provides a simple API for typical database operations:

// 1. Persists Kotlin objects to the database by passing objects directly to DML statements.

// 2. Returns database results as Kotlin types: scalars, objects (single records), or ArrayLists (recordsets) - ready for immediate
//    use in business logic. No more ContentValues and Cursor manipulation cluttering your business code!

// Most examples place all CRUD functions for different entities in a single class inheriting from SQLiteOpenHelper.
// Worse, they mix business logic with technical code like ContentValues population and Cursor reading.
// Even examples that suggest separate classes for each entity promote endless copy-paste.
// I don't understand why experienced developers writing these tutorials haven't encapsulated common logic in a generic class - 
// first-year Computer Science students know code duplication is bad.

// I reject this approach entirely. For each entity (Emp, Dept, etc.), I create a dedicated controller class (EmpController, DeptController)
// handling only that entity's database operations - rather than dumping everything into one monolithic CustomSQLiteOpenHelper. 
// And I separate technical code from business logic.

// CRUD functions across entities are typically similar - differing only in ContentValues population, Cursor extraction, and SQL specifics.
// Remove ContentValues/Cursor manipulation, and these functions become nearly identical:
// INSERT one record and return the auto-incremented ID; UPDATE/SELECT one record by ID; SELECT ArrayList by WHERE clause.

// This solution refactors common code into an ancestor class, eliminating the need to create many near-identical controller classes.
// XxxController classes become very small - containing only entity-specific logic requiring additional processing beyond standard CRUD calls.
// If your entity needs only standard CRUD functions, skip creating a controller entirely and call 
// functions directly by instantiating the CRUD helper class (it's not abstract).

// STEPS:

// @ Perform the steps, described here: https://tinyurl.com/CursorInterface.

// @ Create the "db" package, where you will put the stuff, related to database manipulations, specific to this application.
// Pay attention that any stuff, which doesn't deal with the entities of this app (and, hence, can be reused in other apps),
// should be placed in the "util" package - even if it DB-related.

// @ In the "db" package, create a Kotlin file named DbInfo and copy the following code into it - just after the "package" directive
// (change the DB name to the actual one):

object DbInfo {
    const val NAME = "<YOUR DB NAME>.db"
    const val VERSION = 1 // increment if you change the database schema
}

// @ In "db" package, create a Kotlin file named DbTable. That object will contain the names of the DB tables, for example:

object DbTable {
    const val EMP = "emp"
    const val DEPT = "dept"
}

// @ In "db" package, create a Kotlin file named DbColumn. That object will contain the names of the DB tables' columns, for example:

object DbColumn {
    const val ID = "_id"
    const val FIRST_NAME = "first_name"
    const val LAST_NAME = "last_name"
    const val DOB = "dob"
    const val IS_ACTIVE = "is_active"
}

// We create one object which will contain all the columns of all the tables (rather than a dedicated object for each table) because
// a same column can exist in many tables, and we want to ensure consistency all over the application (after all, that's why we use constants!).
// That also obeys the DRY principle (Don't Repeat Yourself) - we don't duplicate a same column name constant in many places.
// Obviously, we will use these constants to build the CREATE TABLE statements.
// So, if the column name is "dob", it will be "dob" everywhere - not "dob", "birth_date" and "date_of_birth" in different tables.

// @ In "db" package, create a Kotlin file named CustomSQLiteOpenHelper and copy the following code into it - just after the "package" directive:

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

open class CustomSQLiteOpenHelper(context: Context): SQLiteOpenHelper(context, DbInfo.NAME, null, DbInfo.VERSION) {
    /***********************************************************************************************************************/
    override fun onCreate(db: SQLiteDatabase) {
        this.createDbObjects(db)
    }
    /***********************************************************************************************************************/
    private fun createDbObjects(db: SQLiteDatabase) {
        // Extracted from onCreate() to allow the logic be executed many times from onOpen() in debug purpose.
        // In production, it will be called only once, from onCreate() - comment out calling from onOpen().

        var sql: String

//        var sql: String = "DROP TABLE IF EXISTS " + DbTable.XXX // that allows to call this function many times in debug purposes
//        db.execSQL(sql)

//        sql = "CREATE TABLE " + DbTable.XXX + " (" +
//                DbColumn.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                DbColumn.FIELD_1 + " TEXT NOT NULL, " +
//                DbColumn.FIELD_2 + " INTEGER NOT NULL DEFAULT 0" +
//                ")"
//        db.execSQL(sql)
    }
    /***********************************************************************************************************************/
    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    // Comment out this function when createDbObjects() has been successfully debugged !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    override fun onOpen(db: SQLiteDatabase?) {
        super.onOpen(db)
        this.createDbObjects(db!!)
    }
    /***********************************************************************************************************************/
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //TODO
    }
    /***********************************************************************************************************************/
    override fun close() {
        this.writableDatabase.close()
        super.close()
    }
    /***********************************************************************************************************************/
}

// Interface Crudable

// Forces you to write the "boring" technical code (population of ContentValues and reading from Cursor) separately
// from the "interesting" business logic, which makes that logic easier to write and, later, understand.

// @ In "util" package, create a Kotlin file named Crudabe and copy the following code into it - just after the "package" directive
// (everything is explained in the comments):

import android.content.ContentValues
import android.database.Cursor
import <YOUR BASE PACKAGE>.db.DbColumn

// --------------------------------------------------------------------------------------------------------------------------
// Must be implemented by all model classes representing application entities (e.g., Emp, Dept).
// This allows CrudHelper to manipulate these classes in its CRUD operations.
// The interface enforces separation of technical boilerplate (ContentValues population 
// and Cursor reading) from business logic, making the latter easier to write and maintain.
// --------------------------------------------------------------------------------------------------------------------------

interface Crudable {
    /***********************************************************************************************************************/
    val TABLE_NAME: String
    /***********************************************************************************************************************/
    val ID_COL_NAME: String
        get() = DbColumn.ID // override if the ID column name is not "_id"
    /***********************************************************************************************************************/
    var id: Int?
    // Override this way:
    // override var id: Int? = null
    /***********************************************************************************************************************/
    fun extractContentValues(): ContentValues
    // Called from insert() and update() of CrudHelper.

    // Just copy-paste to the descendant and customize according to the fields in that descendant:
    //    override fun extractContentValues() : ContentValues {
    //        val cv = ContentValues()
    //        // DON'T PUT PK FIELD(S) - PK IS AUTOGENERATED (ON INSERT) OR SUPPLIED WITHIN WHERE CLAUSE (ON UPDATE)
    //        cv.put(DbColumn.FIRST_NAME, this.firstName)
    //        cv.put(DbColumn.LAST_NAME, this.lastName)
    //        cv.put(DbColumn.DOB, this.dob)
    //        cv.put(DbColumn.IS_ACTIVE, this.isActive)
    //        return cv
    //    }
    /***********************************************************************************************************************/
    fun populateFromCursor(cursor: Cursor)
    // Called from retrieveListBySql() of CrudHelper.

    // Just copy-paste to the descendant and customize according to the fields in that descendant:
    //    override fun populateFromCursor(cursor: Cursor) {
    //        this.id = cursor.getInt(DbColumn.ID) // https://tinyurl.com/CursorInterface
    //        this.firstName = cursor.getString(DbColumn.FIRST_NAME)
    //        this.lastName = cursor.getString(DbColumn.LAST_NAME)
    //        this.dob = cursor.getLocalDate(DbColumn.DOB)
    //        this.isActive = cursor.getBoolean(DbColumn.IS_ACTIVE)
    //    }
    /***********************************************************************************************************************/
} // interface Crudable

// Class CrudHelper

// The main working horse of the functionality.
// Encapsulates population of ContentValues and reading from Cursor, so you will never write these loops anymore among your business logic.
// Has the following functions which operate on objects, implementing the Crudable interface:

// * retrieveList() // SELECTs a recordset (ArrayList)
// * retrieveOne() // SELECTs a single record

// // Functions which SELECT one scalar value of the given data type:

// * queryForString() // example: SELECT last_name FROM emp WHERE emp_id = 123
// * queryForLong() // example: SELECT COUNT(*) FROM emp
// * queryForDouble() // example: SELECT salary FROM emp WHERE emp_id = 123
// * queryForBoolean() // example: SELECT is_active FROM emp WHERE emp_id = 123
// * exists() // mimics the EXISTS statement of SQL

// // DML functions:

// * insert()
// * update()
// * upsert() // UPDATEs the record if it exists, INSERTs if doesn't
// * delete()

// @ In "util" package, create a Kotlin file named CrudHelper and copy the following code into it - just after the "package" directive:

import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDoneException
import <YOUR PACKAGE>.db.CustomSQLiteOpenHelper
import kotlin.reflect.KFunction

// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
// Before you add this class to your app, create CustomSQLiteOpenHelper: https://tinyurl.com/SQLiteCRUD
// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

open class CrudHelper(context: Context): CustomSQLiteOpenHelper(context) {
    // ----------------------------------------------------------------------------------------------------------------------
    // Encapsulates the typical CRUD functions applicable to DB entities.
    // In most cases, this class can be instantiated and used directly, with no need to be extended.
    // But if some entity's CRUD logic is less straightforward, you can inherit from CrudHelper and add/override functions:
    // class DeptCrudHelper(context: Context): CrudHelper(context) { ... }
    // ----------------------------------------------------------------------------------------------------------------------
    // Model (entity) classes (like Emp, Dept), for which you want to call CRUD functions, must implement Crudable interface.
    // ----------------------------------------------------------------------------------------------------------------------
    // How to use this class:
    //
    // Instantiate CrudHelper in the activity which will use it (like EmpListActivity and EmpEditActivity) as a property:
    // private val crudHelper = CrudHelper(context = this)
    //
    // That's it! Now, each function of the Activity can call the CRUD functions of crudHelper. For example:
    //
    // val emp = Emp()
    // newAutoincrementedId = crudHelper.insert(emp)
    // ...
    // crudHelper.update(emp)
    // ...
    // crudHelper.delete(emp)
    // empWithWorkerNumber25 = crudHelper.retrieveOne<Emp>(DbTable.EMP, "${DbColumn.WORKER_NUMBER} = 25")
    // val allEmployees = crudHelper.retrieveList<Emp>()
    // val activeEmployees = crudHelper.retrieveList<Emp>(tableName = DbTable.EMP, whereClause = "${DbColumn.IS_ACTIVE}=1")
    // val sql = "SELECT ${DbColumn.LAST_NAME} AS textValue FROM "${DbTable.EMP} ORDER BY ${DbColumn.LAST_NAME}"
    // val empLastNames = crudHelper.retrieveList<CrudableString>(sql)
    //
    // If no fitting function is found in crudHelper, the Activity can call the functions of
    // crudHelper.writableDatabase & crudHelper.readableDatabase directly.
    // example: to run an SQL statement, which returns nothing (or you don't need the returned value), write:
    // crudHelper.writableDatabase.execSQL("...")
    // ----------------------------------------------------------------------------------------------------------------------

    // ----------------------------------------------------------------------------------------------------------------------
    // retrieveList() [SELECTs a recordset]:
    // ----------------------------------------------------------------------------------------------------------------------

    /***********************************************************************************************************************/
    inline fun <reified T: Crudable> retrieveList(sqlSelect: String, selectionArgs: Array<String>? = null): ArrayList<T> {
        // The number, types and order of the fields in the SELECT statement must correspond the fields, copied
        // in extractContentValues() and populateFromCursor() of the class, passed as T.

        // If you need to retrieve a recordset, which doesn't correspond to a particular table (for example,
        // to SELECT FROM a few joined tables, or grab statistics), then create (an pass to this function as <T>)
        // a custom class - just for that purpose. In this case, follow these rules:
        //      1. If the SQL SELECT has computed fields, give them aliases to be accessed by name in code.
        //      2. Override populateFromCursor() as usually (it's used on retrieval).
        //      3. Override TABLE_NAME, id and extractContentValues() this way:

        // override val TABLE_NAME: String
        //  get() = throw Exception("<YourClass>.TABLE_NAME should never be got!")
        //
        // override var id: Int?
        //   get() = throw Exception("<YourClass>.id should never be got!")
        //   set(value) {throw Exception("<YourClass>.id should never be set!")}
        //
        // override fun extractContentValues(): ContentValues = throw Exception("<YourClass>.extractContentValues() should never be called!")

        val entities = ArrayList<T>()
        val db = this.writableDatabase
        if (!db.isOpen) throw Exception("CrudHelper.retrieveList(): DB is closed.")

        val cursor = db.rawQuery(sqlSelect, selectionArgs)
            ?: throw Exception("CrudHelper.retrieveList(): rawQuery() returned null cursor by '$sqlSelect'.")
        cursor.use {
            while (cursor.moveToNext()) {
                // The following two code lines is a dirty trick to create an instance of a generic type.
                // To enable that, the generic parameter is marked as reified. That is possible only
                // in inline functions, so this function and all its callers are converted to inline.
                // https://tinyurl.com/GenericTypeConstructor
                val actualRuntimeClassConstructor: KFunction<T> = T::class.constructors.first()
                val entity: T = actualRuntimeClassConstructor.call()

                entity.populateFromCursor(cursor)
                entities.add(entity)
            }
        }

        return entities
    }
    /***********************************************************************************************************************/
    inline fun <reified T: Crudable> retrieveList
                (tableName: String, whereClause: String? = null, orderByClause: String? = null): ArrayList<T> {
        val sql = StringBuffer("SELECT * FROM $tableName")
        if (whereClause != null) sql.append(" WHERE $whereClause")
        if (orderByClause != null) sql.append(" ORDER BY $orderByClause")
        return this.retrieveList(sql.toString())
    }
    /***********************************************************************************************************************/

    // ----------------------------------------------------------------------------------------------------------------------
    // retrieveOne() [SELECTs one single record]:
    // ----------------------------------------------------------------------------------------------------------------------

    /***********************************************************************************************************************/
    inline fun <reified T: Crudable> retrieveOne
                (sqlSelect: String, selectionArgs: Array<String>? = null, required: Boolean = false): T? {
        val entities: ArrayList<T> = this.retrieveList(sqlSelect, selectionArgs)
        return when (entities.size) {
            1 -> entities[0]
            0 -> {
                if (required) throw Exception("CrudHelper.retrieveOne(): no data found by '$sqlSelect'.")
                null
            }
            else -> throw Exception("CrudHelper.retrieveOne(): ${entities.size} rows returned by '$sqlSelect' while one row expected.")
        }
    }
    /***********************************************************************************************************************/
    inline fun <reified T: Crudable> retrieveOne
                (tableName: String, id: Int, idColName: String = "_id", required: Boolean = true): T? {
        return this.retrieveOne(sqlSelect = "SELECT * FROM $tableName WHERE $idColName=$id", required = required)
    }
    /***********************************************************************************************************************/
    inline fun <reified T: Crudable> retrieveOne
                (tableName: String, whereClause: String, selectionArgs: Array<String>? = null, required: Boolean = true): T? {
        return this.retrieveOne(sqlSelect = "SELECT * FROM $tableName WHERE $whereClause",
            selectionArgs = selectionArgs, required = required)
    }
    /***********************************************************************************************************************/

    // ----------------------------------------------------------------------------------------------------------------------
    // Functions which SELECT one scalar value:
    // ----------------------------------------------------------------------------------------------------------------------

    /***********************************************************************************************************************/
    fun queryForString(sqlSelect: String, required: Boolean = false): String? {
        // Executes a statement that returns a scalar String value. For example, SELECT last_name FROM emp WHERE emp_id = 123
        val result: String

        try {
            val statement = this.readableDatabase.compileStatement(sqlSelect)
            result = statement.simpleQueryForString()
        } catch (e: SQLException /* compileStatement() failed */) {
            throw Exception("CrudHelper.queryForString(): '$sqlSelect' is not a valid SQL statement.")
        } catch (e: SQLiteDoneException /* simpleQueryForString() returned zero rows */) {
            if (required) throw Exception("CrudHelper.queryForString(): no data found by '$sqlSelect'.")
            return null
        }

        return result
    }
    /***********************************************************************************************************************/
    fun queryForLong(sqlSelect: String, required: Boolean = false): Long? {
        // Executes a statement that returns a scalar Long value. For example, SELECT COUNT(*) FROM emp
        val result: Long

        try {
            val statement = this.readableDatabase.compileStatement(sqlSelect)
            result = statement.simpleQueryForLong()
        } catch (e: SQLException /* compileStatement() failed */) {
            throw Exception("CrudHelper.queryForLong(): '$sqlSelect' is not a valid SQL statement.")
        } catch (e: SQLiteDoneException /* simpleQueryForLong() returned zero rows */) {
            if (required) throw Exception("CrudHelper.queryForLong(): no data found by '$sqlSelect'.")
            return null
        }

        return result
    }
    /***********************************************************************************************************************/
    fun queryForDouble(sqlSelect: String, required: Boolean = false): Double? {
        // Executes a statement that returns a scalar String value convertible to Double.
        // example: SELECT salary FROM emp WHERE emp_id = 123
        val resultAsDouble: Double
        val resultAsString = this.queryForString(sqlSelect, required)
        if (resultAsString == null && !required) return null
        // if (result == null && required), then an Exception has already been thrown by queryForString()

        try {
            resultAsDouble = resultAsString!!.toDouble()
        } catch (e: NumberFormatException) {
            throw Exception("CrudHelper.queryForDouble(): The value, retrieved by '$sqlSelect', is $resultAsString. " +
                    "It cannot be converted to Double.")
        }

        return resultAsDouble
    }
    /***********************************************************************************************************************/
    fun queryForBoolean(sqlSelect: String, required: Boolean = false): Boolean? {
        // Executes a statement that returns a scalar Long value which can be treated as Boolean (i.e. 0 or 1).
        // example: SELECT is_active FROM emp WHERE emp_id = 123
        val result = this.queryForLong(sqlSelect, required)
        if (result == null && !required) return null
        // if (result == null && required), then an Exception has already been thrown by queryForLong()

        when (result) {
            1L -> return true
            0L -> return false
        }

        throw Exception("CrudHelper.queryForBoolean(): The value, retrieved by '$sqlSelect', is $result. " +
                "To be treated as Boolean, it must be 0 or 1.")
    }
    /***********************************************************************************************************************/
    fun exists(tableName: String, whereClause: String? = null): Boolean {
        // Mimics the EXISTS statement of SQL.
        val sqlSelect = "SELECT Count(1) FROM $tableName" + if (whereClause != null) " WHERE $whereClause" else ""
        val count = this.queryForLong(sqlSelect, required = false)!!
        return (count > 0)
    }
    /***********************************************************************************************************************/

    // ----------------------------------------------------------------------------------------------------------------------
    // DML:
    // ----------------------------------------------------------------------------------------------------------------------

    /***********************************************************************************************************************/
    open fun insert(entity: Crudable, idAutoIncrement: Boolean = true): Int {
        if (idAutoIncrement && entity.id != null)
            throw Exception("CrudHelper.insert(): entity.id must be null (not ${entity.id}) since idAutoIncrement = true.")
        val cv = entity.extractContentValues()
        val rowId = this.writableDatabase.insert(entity.TABLE_NAME, null, cv)
        if (rowId == -1L) throw Exception("CrudHelper.insert() failed.")
        if (idAutoIncrement) entity.id = rowId.toInt()
        return rowId.toInt()
    }
    /***********************************************************************************************************************/
    open fun update(entity: Crudable, whereClause: String? = null): Int {
        // If whereClause is not supplied, this fun updates by entity.id.
        val cv = entity.extractContentValues()
        val finalWhereClause = whereClause ?: "${entity.ID_COL_NAME}=${entity.id}"
        return writableDatabase.update(entity.TABLE_NAME, cv, finalWhereClause, null)
    }
    /***********************************************************************************************************************/
    open fun upsert(entity: Crudable): Int { // UPDATE if exists, INSERT if doesn't; use with autoincremented ID
        return if (entity.id != null) update(entity) else insert(entity)
    }
    /***********************************************************************************************************************/
    open fun upsert(entity: Crudable, whereClause: String): Int { // UPDATE if exists, INSERT if doesn't; use with a custom PK
        val rowsUpdated = update(entity, whereClause)
        if (rowsUpdated > 0) return rowsUpdated
        return insert(entity)
    }
    /***********************************************************************************************************************/
    open fun delete(entity: Crudable): Int {
        // Deletes the entity by its id. If deleting condition is different (or there is no condition at all), then call directly:
        // <your CrudHelper>.writableDatabase.delete(<table>, <whereClause>, <whereArgs>)
        return this.writableDatabase.delete(entity.TABLE_NAME, "${entity.ID_COL_NAME}=${entity.id}", null)
    }
    /***********************************************************************************************************************/
} // class CrudHelper

// @ In "util" package, create this constant:

const val g_DEBUG_MODE = true // TODO: must be false when promoted to production

// @ Add to onCreate() of the MainActivity:

        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // Comment out this fragment when CustomSQLiteOpenHelper.createDbObjects() has been successfully debugged:
        if (g_DEBUG_MODE) {
            val customSQLiteOpenHelper = CustomSQLiteOpenHelper(this)
            customSQLiteOpenHelper.createDbObjects(customSQLiteOpenHelper.writableDatabase)
        }
        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

// @ You have created the class CustomSQLiteOpenHelper with the function createDbObjects() having commented-out sample code.
// Now it's time to uncomment and customize it, so the function will create your table(s).
// When that function works, comment out the fragment, added to onCreate() of the MainActivity in the previous step (or make g_DEBUG_MODE false).
