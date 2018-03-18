package net.ssdtic.suli

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.PreferenceManager
import android.content.SharedPreferences


class SettingFragment : PreferenceFragmentCompat() {

    companion object {
        const val PREF_KEY_AUTO_SYNC = "pref_key_auto_sync"
        const val PREF_KEY_ADS_DISPLAY = "pref_key_ads_display"
        const val PREF_KEY_VERSION = "pref_key_version"
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // Load the preferences from an XML resource
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val connectionPref = findPreference(SettingFragment.PREF_KEY_VERSION)
        // Set summary to be the user-description for the selected value
        connectionPref.setSummary(BuildConfig.VERSION_NAME)

        // Get the preference value
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(activity)
        // true is initialize value then don't get value
        println("PREF_KEY_AUTO_SYNC : " + sharedPref.getBoolean(SettingFragment.PREF_KEY_AUTO_SYNC, true))
        println("PREF_KEY_ADS_DISPLAY : " + sharedPref.getBoolean(SettingFragment.PREF_KEY_ADS_DISPLAY, true))
    }

    private fun myChangeListener(pref: SharedPreferences, key: String) {
        println("changed preference : " + key)
    }

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key -> myChangeListener(sharedPreferences, key) }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
    }

}
