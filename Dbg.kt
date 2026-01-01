/****************************************************************************************************************************
Dbg is a lightweight debug helper.

Public API:

* Dbg.log(msg: String)
    Writes your message to Logcat using Log.d().

* Dbg.msg(msg: String, context: Context)
    Shows a modal dialog with your message.
    Works only in the emulator, so you can safely forget to remove calls from code without affecting real users.

In addition to your message, both the functions include the caller extracted by scanning the stack trace,
i.e. the function where you call the Dbg.log() or Dbg.msg() - like `MainActivity.onCreate`, etc.
That can be useful if you debug a process which consist of a few functions.
In fact, it's the only reason to prefer Dbg.log() over Log.d().
****************************************************************************************************************************/

package ca.intfast.iftimer.util

import android.content.Context
import android.os.Build
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AlertDialog

object Dbg {
    fun log(msg: String) {
        var caller = getCaller()
        caller = if (caller.isNotEmpty()) " $caller " else ""
        Log.d("#######$caller#######", msg)
    }

    fun msg(msg: String, context: Context) {
        if (!isRunningOnEmulator()) return // extra asscovering if you forget to remove a Dbg.msg() call

        val caller = getCaller()
        val displayMsg = if (caller.isNotEmpty()) "$caller\n\n$msg" else msg

        Handler(context.mainLooper).post {
            AlertDialog.Builder(context)
                .setTitle("Debug")
                .setMessage(displayMsg)
                .setPositiveButton("Close", null)
                .setCancelable(false)
                .show()
        }
    }

    private fun getCaller(): String {
        val stackTrace = Throwable().stackTrace

        val dbgClassName = Dbg::class.java.name

        // Packages/classes that should never be reported as the "caller"
        val skipPrefixes = listOf(
            dbgClassName, // Dbg itself
            "java.",
            "kotlin.",
            "kotlinx.",
            "dalvik.",
            "sun.",
            "com.android.",
            "android.",
            "androidx."
        )

        // Scan the stack trace and pick the first frame that belongs to your app code,
        //   skipping Dbg itself + framework/runtime calls (android/androidx/kotlin/java/etc.).
        for (e in stackTrace) {
            val cn = e.className

            // Skip Dbg itself and any framework/runtime frames
            if (skipPrefixes.any { cn.startsWith(it) }) continue

            val method = e.methodName

            // Clean up generated class names (e.g., MainActivity$onCreate$1)
            val simpleClass = cn.substringAfterLast('.').substringBefore('$')

            // "Top-level" Kotlin functions live in *Kt classes (FileNameKt)
            val isTopLevelFunc = cn.endsWith("Kt") && !cn.contains("$")

            return if (isTopLevelFunc) {
                method
            } else {
                "$simpleClass.$method"
            }
        }

        return ""
    }

    private fun isRunningOnEmulator(): Boolean {
        return (
                Build.FINGERPRINT.startsWith("generic") ||
                        arrayOf("vbox", "test-keys").any { Build.FINGERPRINT.lowercase().contains(it) } ||
                        arrayOf("google_sdk", "Emulator", "Android SDK built for x86").any { Build.MODEL.contains(it) } ||
                        Build.MANUFACTURER.contains("Genymotion") ||
                        (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")) ||
                        Build.PRODUCT.contains("sdk_gphone") ||
                        arrayOf("goldfish", "ranchu").any { Build.HARDWARE.contains(it) }
                )
    }

}
