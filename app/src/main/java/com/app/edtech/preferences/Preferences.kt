package com.app.edtech.preferences

import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object Preferences {
    fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(TAG, Context.MODE_PRIVATE)
    }
    fun removeAllPreference(context: Context) {
        val settings: SharedPreferences = getSharedPreferences(context)
        val editor = settings.edit()
        editor.clear()
        editor.commit()
    }
    fun removeAllPreferencesExcept(context: Context, excludeKeys: List<String>) {
        val settings = getSharedPreferences(context)
        val allEntries = settings.all

        val editor = settings.edit()
        for (entry in allEntries.entries) {
            if (!excludeKeys.contains(entry.key)) {
                editor.remove(entry.key) // Remove keys not in the exclusion list
            }
        }
        editor.apply() // Apply changes
    }
    inline fun <reified T> setCustomModelPreference(context: Context?, key: String?, value: T?) {
        val settings = getSharedPreferences(context!!)
        val editor = settings.edit()
        val jsonString = Gson().toJson(value)
        editor.putString(key, jsonString)
        editor.apply()
    }
    inline fun <reified T> getCustomModelPreference(context: Context?, key: String?): T? {
        val settings = getSharedPreferences(context!!)
        val jsonString = settings.getString(key, null)
        return if (jsonString.isNullOrEmpty()) {
            null
        } else {
            Gson().fromJson(jsonString, object : TypeToken<T>() {}.type)
        }
    }

    fun setStringPreference(context: Context?, key: String?, value: String?) {
        val settings: SharedPreferences = getSharedPreferences(context!!)
        val editor = settings.edit()
        editor.putString(key, value)
        editor.commit()
    }


    fun getStringPreference(context: Context?, key: String?): String? {
        val pref: SharedPreferences = getSharedPreferences(context!!)
        return pref.getString(key, "")
    }

    fun setStringListPreference(context: Context?, key: String?, value: MutableList<String>?) {
        val settings: SharedPreferences = getSharedPreferences(context!!)
        val editor = settings.edit()

        // Convert the list of strings to a JSON string
        val jsonString = Gson().toJson(value)
        editor.putString(key, jsonString)

        editor.apply()
    }


    fun getStringListPreference(context: Context?, key: String?): MutableList<String>? {
        val pref: SharedPreferences = getSharedPreferences(context!!)
        val jsonString = pref.getString(key, null)

        // Convert the JSON string back to a list of strings
        return Gson().fromJson(jsonString, object : TypeToken<MutableList<String>>() {}.type)
    }

}