// We should use constants (rather than hardcoding) whenever possible.
// That includes populating of ListPreference, MultiSelectListPreference and DropDownPreference.
// To do it in a generic and elegant way, the objects, which serve as containers for constants groups (or enums), should have functions
// hich return arrays, ready to be assigned to preferences' properties.
// We need the following functions (to make life easier, they are defined in the interface which will be provided soon):

// toArray() - Returns the values of all the constants as an array of the constants type.
//      Used to populate Preference.entryValues when the constants type is String.

// toStringArray() - Returns the values of all the constants as an array of String.
//      Used to populate Preference.entryValues when the constants type is NOT String.
//      In fact, it works for String too, so you can call it always, and forget about toArray().

// toDisplayedValuesArray() - Returns the human-readable descriptions of all the constants.
//      Used to populate Preference.entries - the array of values which are displayed to the user in list-based prefs.
//      By default, grabs the values from R.string, so you need to define an R.string resource for each constant.
//      Otherwise (for example, if you want to hardcode the displayed values or grab them from the DB), override toDisplayedValuesArray() and
//      build the array in a custom way.

// To ensure the same behaviour, all the classes, whose constants are used to populate multi-values Preferences, must implement
// the ConstantsSet<T> interface (where <T> is the constants' type), provided below.
// Its abstract functions have comments with examples of implementation - copy-paste them to the implementing objects and customize.

package <YOUR PACKAGE>

import android.content.Context

/****************************************************************************************************************************
To be implemented by classes whose constants are used to populate multi-values Preferences
(ListPreference, MultiSelectListPreference, DropDownPreference).
****************************************************************************************************************************/

interface ConstantsSet<T> {
    // Example of constants, declared in the implementing class:
    // const val FIRST = 1
    // const val SECOND = 2
    // const val THIRD = 3

    /***********************************************************************************************************************/
    fun toArray(): Array<T>
    // Returns the values of all the constants as an array of the constants type, like {1, 2, 3}
    // Call it to populate Preference.entryValues in your PreferenceFragmentCompat when the constants type is String.

    // Sample implementation:
    // override fun toArray(): Array<String> = arrayOf(FIRST, SECOND, THIRD)

    /***********************************************************************************************************************/
    fun toStringArray(): Array<String?> {
        // Returns the values of all the constants as an array of String, like {"1", "2", "3"}
        // Call it to populate Preference.entryValues in your PreferenceFragmentCompat when the constants type is NOT String.
        val origTypeArray = toArray()
        val stringArray: Array<String?> = arrayOfNulls<String?>(size = origTypeArray.size)
        for ((i, constantValue) in (origTypeArray.withIndex())) {
            stringArray[i] = constantValue.toString()
        }
        return stringArray
    }

    /***********************************************************************************************************************/
    fun toDisplayedValuesArray(context: Context): Array<String?> {
        // Returns array of human-readable texts for each constant, like {"First", "Second", "Third"}.
        // Call it to populate Preference.entries in your PreferenceFragmentCompat.
        val constantValuesArray = toArray()
        val displayedValuesArray: Array<String?> = arrayOfNulls<String?>(size = constantValuesArray.size)
        for ((i, constantValue) in (constantValuesArray.withIndex())) {
            displayedValuesArray[i] = getDisplayedValue(constantValue, context)
        }
        return displayedValuesArray
    }

    /***********************************************************************************************************************/
    fun getDisplayedValue(constantValue: T, context: Context): String {
        // Returns the human-readable text for this constant (like "First", "Second" or "Third").
        val resourceId = getResourceId(constantValue)
        return context.resources.getString(resourceId)
    }

    /***********************************************************************************************************************/
    fun getResourceId(constantValue: T): Int
    // Returns R.string.XXX to obtain the human-readable text for this constant.

    /* Sample implementation:
    override fun getResourceId(constantValue: String): Int {
        return when (constantValue) {
            FIRST -> R.string.whatever__first
            SECOND -> R.string.whatever__second
            THIRD -> R.string.whatever__third
            else -> throw Exception("$constantValue is not a valid value of ${this.javaClass.name}.")
        }
    }

    >>>>>>> If R.string is irrelevant (for example, the human-readable texts are hardcoded or retrieved from DB):

    STEP 1: implement getResourceId() this way
        (constantValue must be of the type, actually passed as T when ConstantsSet<T> was created):

    override fun getResourceId(constantValue: Int): Int =
        throw Exception("Fun ${this.javaClass.name}.getResourceId() should never be called since " +
                "the human readable texts are not stored in R.string.")

    STEP 2 (only if toDisplayedValuesArray() will be called): override getDisplayedValue() which is called from
    toDisplayedValuesArray() per each constant:

    override fun getDisplayedValue(constantValue: Int, context: Context): String {
        return <the text which must be displayed for this constant>
    }

    Or, alternatively, you can override toDisplayedValuesArray() and return the whole array at one stroke:

    override fun toDisplayedValuesArray(context: Context): Array<String?> = arrayOf("First", "Second", "Third")
    */

    /***********************************************************************************************************************/
    fun validate(constantValue : T?) {
        // Call it in setters when the set value is represented by a constant, contained in the implementing object.
        if (constantValue == null) return
        if (!toArray().contains(constantValue))
            throw Exception("'$constantValue' is not a valid value of ${this.javaClass.name}.")
    }
}

// EXAMPLE OF USE

// Step 1:
// Create the constants container (i.e. the object which implements ConstantsSet<T>):

object Darkness : ConstantsSet<String> {
    const val LIGHT = "L"
    const val LIGHT_MEDIUM = "LM"
    const val MEDIUM = "M"
    const val MEDIUM_DARK = "MD"
    const val DARK = "D"
    const val VERY_DARK = "VD"

    override fun toArray(): Array<String> = arrayOf(LIGHT, LIGHT_MEDIUM, MEDIUM, MEDIUM_DARK, DARK, VERY_DARK)

    override fun getResourceId(constantValue: String): Int =
        when (constantValue) {
            LIGHT -> R.string.darkness__light
            LIGHT_MEDIUM -> R.string.darkness__light_medium
            MEDIUM -> R.string.darkness__medium
            MEDIUM_DARK -> R.string.darkness__medium_dark
            DARK -> R.string.darkness__dark
            VERY_DARK -> R.string.darkness__very_dark
            else -> throw Exception("'$constantValue' is not a valid value of ${this.javaClass.name}.")
        }
}

// Step 2:
// Utilize it when you are building the settings screen. The next code snippet illustrates the idea.

// Pay attention that the PrefFragment in that example is inherited not from PreferenceFragmentCompat but from PreferenceFragmentAutomaticSummary
// (which is described here).
// You are not obligated to do that - you can inherit your preference fragment directly from PreferenceFragmentCompat if you want.
// But inheriting from PreferenceFragmentAutomaticSummary gives you automatic displaying of the value, currently stored in each list-based Preference,
// in its Summary - for free.

// Also notice that prefs are created in code rather than in an XML file. That approach has 3 benefits:

// 1. Preference.key can be populated with a constant (so, the preference can be accessed later using the same constant).
//      Otherwise you would be forced to store the key as a string resource, and retrieve it each time you need to read the preference.
//      Or populate the key with a hard-coded value, which is unacceptable.

// 2. The whole logic, creating the Preference, is concentrated in one place (rather than distributed between XML and code).
//      Not all the properties can be populated in XML in an elegant way, so you would populate some of them programmatically anyway.

// 3. You have an absolute freedom what to do.
//      For example, you can set Preference.title dynamically, or show/hide preferences depending on conditions in runtime.

class PrefFragment : PreferenceFragmentAutomaticSummary(), SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val context = preferenceManager.context
        val screen = preferenceManager.createPreferenceScreen(context)
        var dropDownPref: DropDownPreference

        // ...create other prefs...

        // DEFAULT_DARKNESS
        dropDownPref = DropDownPreference(context)
        dropDownPref.key = PrefKey.DEFAULT_DARKNESS
        dropDownPref.title = getString(R.string.word__darkness)
        dropDownPref.entryValues /* what we save */ = Darkness.toArray()
        dropDownPref.entries /* what we show to user */ = Darkness.toDisplayedValuesArray(this.context!!)
        dropDownPref.setDefaultValue(Darkness.MEDIUM_DARK)
        screen.addPreference(dropDownPref)

        // ...create other prefs...

        preferenceScreen = screen
    }
}

// POPULATING A MULTI-VALUES PREFERENCE FROM A RANGE

// Sometimes, we have a legal range of values rather than constants. For example, in my application there was a range of temperatures to which an espresso machine can be set - from 89C to 96C. So, I stored them in an IntRange rather than created 8 constants. In such a case, the container class can implement the interface ConstantsSet<T> as well, but the implementation is slightly different.

// In fact, only two things are different:

// 1. The implementation of toArray() builds the array by looping on the range rather than listing constants in arrayOf(). When ConstantsSet<T> gets that array, the processing keeps going in the same way as if the array would be built from constants.

// 2. It's important to override getDisplayedValue() since the original version of ConstantsSet<T> reads values from R.string, which is probably irrelevant in most ranages.

import android.content.Context
import <YOUR UTIL PACKAGE>.ConstantsSet
import kotlin.math.roundToInt

object Temperature : ConstantsSet<Int> {
    private val range = IntRange(89, 96)

    override fun toArray(): Array<Int> {
        // Extract array from range:
        val arr = Array<Int>(range.count()) { it }
        for ((i, value) in range.withIndex()) {
            arr[i] = value
        }
        return arr
    }

    override fun getDisplayedValue(constantValue: Int, context: Context): String {
        return constantValue.toString() // display the stored value to user rather than read it from R.string
    }

    override fun getResourceId(constantValue: Int): Int =
        throw Exception("Fun ${this.javaClass.name}.getResourceId() should never be called since " +
                "the human readable texts are not stored in R.string.")
}

// In the preference fragment, it is used in exactly the same way as in the case of constants.




