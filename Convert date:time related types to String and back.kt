package <YOUR UTIL PACKAGE>

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/****************************************************************************************************************************
Object which facilitates conversion of LocalDate, LocalTime & LocalDateTime to String and back.
Encapsulates the fuss with date/time formats and parsing.
Useful when a time/date value, which should be manipulated in code in its original type,
is stored somewhere as String (for example, in Preferences or SQLite DB).
Chronos is the personification of time in pre-Socratic philosophy and later literature (https://en.wikipedia.org/wiki/Chronos)
https://tinyurl.com/ChronosObj
****************************************************************************************************************************/

object Chronos {
    val dFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE // or: DateTimeFormatter.ofPattern("<your pattern>")
    val tFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_TIME // or: DateTimeFormatter.ofPattern("<your pattern>")
    val dtFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME // or: DateTimeFormatter.ofPattern("<your pattern>")

    // LocalDate:

    fun toLocalDate(sVal: String?): LocalDate? {
        if (sVal == null) return null
        try {
            return LocalDate.parse(sVal, dFormatter)
        } catch (e: DateTimeParseException) {
            throw Exception("Cannot parse '$sVal' to LocalDate.")
        }
    }

    fun toString(dVal: LocalDate?, pattern: String? = null): String? {
        if (dVal == null) return null
        val f = if (pattern.isNullOrBlank()) dFormatter else DateTimeFormatter.ofPattern(pattern)
        return f.format(dVal)
    }

    // LocalTime:

    fun toLocalTime(sVal: String?): LocalTime? {
        if (sVal == null) return null
        try {
            return LocalTime.parse(sVal, tFormatter)
        } catch (e: DateTimeParseException) {
            throw Exception("Cannot parse '$sVal' to LocalTime.")
        }
    }

    fun toString(tVal: LocalTime?, pattern: String? = null): String? {
        if (tVal == null) return null
        val f = if (pattern.isNullOrBlank()) tFormatter else DateTimeFormatter.ofPattern(pattern)
        return f.format(tVal)
    }

    // LocalDateTime:

    fun toLocalDateTime(sVal: String?): LocalDateTime? {
        if (sVal == null) return null
        try {
            return LocalDateTime.parse(sVal, dtFormatter)
        } catch (e: DateTimeParseException) {
            throw Exception("Cannot parse '$sVal' to LocalDateTime.")
        }
    }

    fun toString(dtVal: LocalDateTime?, pattern: String? = null): String? {
        if (dtVal == null) return null
        val f = if (pattern.isNullOrBlank()) dtFormatter else DateTimeFormatter.ofPattern(pattern)
        return f.format(dtVal)
    }
}
