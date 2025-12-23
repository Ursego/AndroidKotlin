// 1. The ContentValues class has putters for most types, but not for LocalDate, LocalTime and LocalDateTime. So, when we send ContentValues to methods insert() and update() of SQLiteDatabase, we need to worry about correct packing of these types into String considering formats.

// 2. The Cursor interface doesn't have getters for LocalDate, LocalTime and LocalDateTime. So, reading them from a cursor, we need to worry about correct unpacking of these types from String considering formats.

// 3. Getters of Cursor (such as getInt, getString etc.) accept column index rather than column name, so we write extra code, which is against the Kotlin't philosophy):

emp.id = cursor.getInt(cursor.getColumnIndex(DbColumn.ID))
emp.lastName= cursor.getString(cursor.getColumnIndex(DbColumn.LAST_NAME))

// rather than

emp.id = cursor.getInt(DbColumn.ID)
emp.lastName= cursor.getString(DbColumn.LAST_NAME)

// 4. Cursor doesn't have a getter for Boolean, so we need to extract it from Int (as it is stored in SQLite):

emp.isActive = (cursor.getInt(cursor.getColumnIndex(DbColumn.IS_ACTIVE)) == 1) // no exception if it was mistakenly inserted as 625!

// rather than

emp.isActive = cursor.getBoolean(DbColumn.IS_ACTIVE)

// HOW TO SOLVE ALL THESE PROBLEMS?

// Fortunately, we can add extended functions in Kotlin (even functions with implementation to interfaces).

// STEPS:

// @ Create object Chronos as described here. ??? Kotlin: Convert date/time related types to String and back https://web.archive.org/web/20200927184020/https://code.intfast.ca/viewtopic.php?f=17&t=825&sid=af9d11e38bc9bd5dda6db8c0d418d463

// @ In "util" package, created in first step, create a Kotlin file named ExtensionFunctions with the following code (just after the "package" statement):

import android.content.ContentValues
import android.database.Cursor
import java.time.LocalDate
import java.time.LocalTime
import java.time.LocalDateTime

// -----------------------------------------------------------------------------------------------------------------
// ------- Extend ContentValues: add putters for additional data types (see ??? Extend Cursor interface and ContentValues class.kt):
// -----------------------------------------------------------------------------------------------------------------

fun ContentValues.put(key: String, value : LocalDate?) = this.put(key, Chronos.toString(value))
fun ContentValues.put(key: String, value : LocalTime?) = this.put(key, Chronos.toString(value))
fun ContentValues.put(key: String, value : LocalDateTime?) = this.put(key, Chronos.toString(value))

// -----------------------------------------------------------------------------------------------------------------
// ------- Extend Cursor: add getters for additional data types, and allow to get by column name
// ------- rather than column index (see ??? Extend Cursor interface and ContentValues class.kt):
// -----------------------------------------------------------------------------------------------------------------

fun Cursor.getShort(columnName: String): Short? = this.getShort(this.getColumnIndex(columnName))
fun Cursor.getInt(columnName: String): Int? = this.getInt(this.getColumnIndex(columnName))
fun Cursor.getLong(columnName: String): Long? = this.getLong(this.getColumnIndex(columnName))
fun Cursor.getFloat(columnName: String): Float? = this.getFloat(this.getColumnIndex(columnName))
fun Cursor.getDouble(columnName: String): Double? = this.getDouble(this.getColumnIndex(columnName))
fun Cursor.getString(columnName: String): String? = this.getString(this.getColumnIndex(columnName))
fun Cursor.getBlob(columnName: String): ByteArray? = this.getBlob(this.getColumnIndex(columnName))

fun Cursor.getBoolean(columnName: String): Boolean {
    val i = this.getInt(this.getColumnIndex(columnName))
    when (i) {
        1 -> return true
        0 -> return false
    }
    throw Exception("Value of field $columnName is $i. To be treated as Boolean, it must be 0 or 1.")
}

fun Cursor.getLocalDate(columnName: String): LocalDate? {
    val s = this.getString(this.getColumnIndex(columnName)) ?: return null
    return Chronos.toLocalDate(s)
}

fun Cursor.getLocalTime(columnName: String): LocalTime? {
    val s = this.getString(this.getColumnIndex(columnName)) ?: return null
    return Chronos.toLocalTime(s)
}

fun Cursor.getLocalDateTime(columnName: String): LocalDateTime? {
    val s = this.getString(this.getColumnIndex(columnName)) ?: return null
    return Chronos.toLocalDateTime(s)
}