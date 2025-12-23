// STEPS:

// @ Create object AppPrefs as described here: https://tinyurl.com/SharedPreferences.
// It allows to store LocalDateTime values in the application's SharedPreferences in a convenient way
// (we need to remember the moment when the Chronometer started - that moment will be used to calculate the new base
// when the Chronometer's parent Activity is resuming).

// @ In the "util" package, created in first step, create a Kotlin file named ExtensionFunctions (if you don't have it)
// and add the following code to it:

import android.content.Context
import android.os.SystemClock
import android.widget.Chronometer
import java.time.Duration
import java.time.LocalDateTime

// -----------------------------------------------------------------------------------------------------------------
// ------- Extend Chronometer functions to allow persistence :
// -----------------------------------------------------------------------------------------------------------------

// The key, by which the base time of the Chronometer (as a LocalDateTime) will be stored in SharedPreferences,
// to be then retrieved onResume() and used to calculate the new base to make the Chronometer keeping counting:
fun Chronometer.generateStartLdtPrefKey(instanceName: String?): String =
                                        "-+=|[Chronometer ${instanceName ?: ""} START LDT]|=+-" // LDT = LocalDateTime

fun Chronometer.start(instanceName: String? = null /* omit if only one Chronometer in app */,
                      startLdt: LocalDateTime? /* null = "start from now" */,
                      context: Context) {
    // Starts Chronometer and makes it count from the provided moment (startLdt); if null, then from the curr. moment (now).
    // Call it (without startLdt) instead of start(<no args>) when user clicks Chronometer's START button.

    val startLdtPrefKey = generateStartLdtPrefKey(instanceName)

    if (startLdt == null) /* start from now */{
        // Remember this moment, so resume(), called from the Activity onResume(), will make the Chronometer counting from it:
        AppPrefs.put(startLdtPrefKey, LocalDateTime.now()!!, context)
        this.base = SystemClock.elapsedRealtime() // milliseconds since boot, including time spent in sleep
        this.start()
        return
    }

    // Count form the moment, provided in startLdt:

    // Remember startLdt, so resume(), called from the Activity onResume(), will make the Chronometer counting from it:
    AppPrefs.put(startLdtPrefKey, startLdt, context)

    val now = LocalDateTime.now()!!
    var deltaInMilli = Duration.between(startLdt, now)!!.toMillis()

    val secondsInMilli = 1000L
    val minutesInMilli = secondsInMilli * 60
    val hoursInMilli = minutesInMilli * 60
    val daysInMilli = hoursInMilli * 24

    deltaInMilli %= daysInMilli

    val elapsedHours = deltaInMilli / hoursInMilli
    deltaInMilli %= hoursInMilli

    val elapsedMinutes = deltaInMilli / minutesInMilli
    deltaInMilli %= minutesInMilli

    val elapsedSeconds = deltaInMilli / secondsInMilli

    val elapsedTimeMilliseconds =
        elapsedHours * 60 * 60 * 1000 + elapsedMinutes * 60 * 1000 + elapsedSeconds * 1000

    this.base = SystemClock.elapsedRealtime() - elapsedTimeMilliseconds
    this.start()
} // Chronometer.start(String, LocalDateTime, Context)

fun Chronometer.finish() {
    // Must be called instead of stop() when user clicks STOP button.
    this.stop()
    this.base = SystemClock.elapsedRealtime() // reset displayed time to "00:00"
} // Chronometer.finish()

fun Chronometer.resume(instanceName: String? = null /* omit if only one Chronometer in app */, context: Context) {
    // Must be called from onResume() of the Chronometer's parent Activity
    val startLdtPrefKey = generateStartLdtPrefKey(instanceName)
    val startLdt = AppPrefs.getLocalDateTime(startLdtPrefKey, context)
        ?:
        return // it's null because start(context: Context) has never been called

    start(instanceName, startLdt, context)
} // resume()

// @ Create the Chronometer on the Activity. Give it a name (ID). Let's say, you called it myChronometer.

// @ Write in the function which starts myChronometer (for example, when user clicks the START button):

myChronometer.start(this)

// @ Write in the function which stops myChronometer (for example, when user clicks the STOP button):

myChronometer.finish(this)

// @ Write in onResume() of myChronometer's parent Activity (that's where the magic happens! :lol: ):

myChronometer.resume(this)

// @ If you want to listen to time changes in your Chronometer (i.e. to do some action when some time has been achieved), do these steps.

// IF YOU HAVE MORE THAN ONE CHRONOMETER IN YOUR APPLICATION...

// ...then ALWAYS pass the chronometer name (i.e. the name of the pointer variable) to start() and resume().
// Internally, that name will be used as the part of the key, by which the start time of each Chronometer is stored in the SharedPreferences:

firstChronometer.start("firstChronometer", this)
secondChronometer.start("secondChronometer", this)
...
firstChronometer.resume("firstChronometer", this)
secondChronometer.resume("secondChronometer", this)

// If you have more than one Chronometer in your app, you must give them names (IDs) unique per app - even if they are placed on different Activities.
// If you want to give them a same name, then pass to start() and resume() other strings, which identify each Chronometer
// (like "ActivityName.ChronometerName").