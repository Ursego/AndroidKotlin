// If you want the values of your Preferences be automatically reflected in the Summaries (on screen start, and each time the value is changed),
// then inherit yours preference fragment from the class PreferenceFragmentAutomaticSummary, provided below (rather than from PreferenceFragmentCompat).
// The suggested class processes all the Preferences of types EditTextPreference, ListPreference and MultiSelectListPreference (and their descendants)
// on your settings screen - no additional coding required.

// STEPS:

// @ Open your app-level build.gradle file and check if its dependencies section contains the following line (the version 1.1.0 was the latest when this topic
// was created - in the time you read it the number could be higher):

implementation 'androidx.preference:preference:1.1.0'

// If the line is not there, add it. If Android Studio has changed the background color in the added line, change the version to the latest one
// (click the line, press Alt+Enter and select the option "Change to ...").

// @ Create "util" package, if you don't have it yet - it's a package to store pure technical stuff (such as helpers, ancestors, extension functions
// for Kotlin classes), which has no relation to the business of the application and, hence, can be reused as is in different applications).

// @ In "util" package, create class PreferenceFragmentAutomaticSummary and copy to it the source code, provided below (just after the "package" statement):

import android.content.SharedPreferences
import androidx.preference.*

/****************************************************************************************************************************
If you want the values of your Preferences be automatically reflected in the Summaries (on screen start, and each time
the value is changed), then inherit yours preference fragment from this class (rather than from PreferenceFragmentCompat).
PreferenceFragmentAutomaticSummary processes all the Preferences of types EditTextPreference, ListPreference and
MultiSelectListPreference (and their descendants) on your settings screen - no additional coding required.
See https://tinyurl.com/CreateAppSettings
****************************************************************************************************************************/

abstract class PreferenceFragmentAutomaticSummary:
                                            PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onResume() {
        // IF YOU OVERRIDE IT, DON'T FORGET TO CALL IN THE FIRST LINE: super.onResume()
        super.onResume()

        sharedPreferences = preferenceManager.sharedPreferences
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        val preferenceScreen = preferenceScreen
        for (i in 0 until preferenceScreen.preferenceCount) {
            setSummary(getPreferenceScreen().getPreference(i))
        }
    }

    override fun onPause() {
        // IF YOU OVERRIDE IT, DON'T FORGET TO CALL IN THE LAST LINE: super.onPause()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        // IF YOU OVERRIDE IT, DON'T FORGET TO CALL IN THE FIRST LINE: super.onSharedPreferenceChanged(sharedPreferences, key)
        val pref = findPreference<Preference>(key)
        if (pref != null)
            setSummary(pref)
    }

    private fun setSummary(pref: Preference) {
        when (pref) {
            is EditTextPreference -> pref.summary = pref.text
            is ListPreference -> pref.summary = pref.entry
            is MultiSelectListPreference -> pref.summary = pref.values.toTypedArray().contentToString()
//            is PreferenceCategory -> {
//                // Loop through child preferences:
//                for (i in 0 until pref.preferenceCount) {
//                    setSummary(pref.getPreference(i))
//                }
//            }
        }
    }
}

// @ Create "pref" package.

// @ In "pref" package, create class PrefFragment with the following code after the "package" statement
// (later, you will customize it according to the actual settings):

import <APP ROOT PACKAGE>.R
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.*
import <APP ROOT PACKAGE>.util.PreferenceFragmentAutomaticSummary
import <APP ROOT PACKAGE>.appwide.PrefKey
import <APP ROOT PACKAGE>.R

class PrefFragment: PreferenceFragmentAutomaticSummary(), SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val screen = preferenceManager.createPreferenceScreen(preferenceManager.context)

        var prefCategory: PreferenceCategory
        var switchPref: SwitchPreferenceCompat
        var dropDownPref: DropDownPreference

//        // SAMPLE FRAGMENT FOR PreferenceCategory
//        prefCategory = PreferenceCategory(context)
//        prefCategory.key = "batch_defaults"
//        prefCategory.title = getString(R.string.pref_category__batch_defaults)
//        prefCategory.isSingleLineTitle = false
//        screen.addPreference(prefCategory)
//
//        // SAMPLE FRAGMENT FOR SwitchPreferenceCompat
//        switchPref = SwitchPreferenceCompat(context)
//        switchPref.key = PrefKey.DEFAULT_IS_DECAF
//        switchPref.title = getString(R.string.word__decaf)
//        switchPref.isSingleLineTitle = false
//        switchPref.setDefaultValue(true)
//        screen.addPreference(switchPref)
//
//        // SAMPLE FRAGMENT FOR DropDownPreference
//        dropDownPref = DropDownPreference(context)
//        dropDownPref.key = PrefKey.DEFAULT_DARKNESS
//        dropDownPref.title = getString(R.string.word__darkness)
//        dropDownPref.isSingleLineTitle = false
//        dropDownPref.entryValues /* what we save */ = Darkness.toArray() // https://tinyurl.com/PrefsFromConst
//        dropDownPref.entries /* what we show to user */ = Darkness.toDisplayedValuesArray(this.context!!)
//        dropDownPref.setDefaultValue(Darkness.MEDIUM_DARK)
//        screen.addPreference(dropDownPref)

        preferenceScreen = screen
    }
}

// @ In "pref" package, create PrefActivity (the Activity which will be used as the Preferences screen):
// right click "pref" directory > New > Activity > Empty Activity:

// Activity Name: PrefActivity
// Generate Layout File: checked (don't change)
// Layout Name: activity_pref (don't change)
// Package Name: <APP ROOT PACKAGE>.pref (don't change)

// @ Click Finish.

// @ Open the related XML file (res/layout/activity_pref.xml) and change its text to this:

<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:id="@+id/layout_of_activity_pref"
    tools:context=".pref.PrefActivity">
</LinearLayout>

// @ Add to values\strings.xml (it's the title of the Settings screen):

<string name="word__settings">Settings</string>

// @ Go to PrefActivity and make its code (after the "package" statement) this:

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import <APP ROOT PACKAGE>.R

class PrefActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pref)
        setTitle(R.string.word__settings)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.layout_of_activity_pref, PrefFragment())
            .commit()

        // Display "back" icon (left arrow) on the menu bar:
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> finish() // user clicked "back" icon (left arrow) on the menu bar
        }
        return super.onOptionsItemSelected(item!!)
    }
}

// @ Create package "appwide" if you don't have it yet. It will contain the application-wide (i.e. not belonging to one particular business area
// but used in many packages) classes and objects (for example, controllers and constants objects/enums).

// @ In the "appwide" package, create object PrefKey with the following code after the "package" statement:

/****************************************************************************************************************************
Container for constants, by which the application preferences are accessed
****************************************************************************************************************************/

object PrefKey {
//    const val AAA = "aaa" // PrefKey.AAA
//    const val BBB = "bbb" // PrefKey.BBB
//    const val CCC = "ccc" // PrefKey.CCC
}

// Every key, by which the stored preferences are accessed (written and read), should be added to this object as a constant.

// @ If you have preferences of types ListPreference, MultiSelectListPreference or DropDownPreference, and want to populate their lists from constants
// of from a Range, please read https://tinyurl.com/PrefsFromConst

// @ Create a menu, which opens the Settings screen, and add it to each Activity.
// Steps to create a menu are here: https://tinyurl.com/CreateAndroidMenu