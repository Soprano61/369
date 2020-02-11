package com.taxi.android.nexi

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import java.text.SimpleDateFormat
import java.time.Month
import java.time.format.TextStyle
import java.util.*


object DataKeeper {

    private val PREFERENCES = "com.livetex.livetexsdktestapp.PREFS"
    private val APP_ID_KEY = "com.livetex.livetexsdktestapp.application_id"
    private val EMPLOYEE_ID_KEY = "com.livetex.livetexsdktestapp.employeeId"
    private val REG_ID = "livetex.regId"
    private val LAST_MESSAGE = "livetex.lastMessage"
    private val NAME = "client_name"
    private val HH_USER = "livetex.hh.user"
    private val UNREAD_MESSAGES_COUNT = "livetex.hh.unreadMessagesCount"
    private val LANGUAGE_CHOICE = "LANG_CHOICE"
    private val LANGUAGE_ID = "LANGUAGE_ID"
    private val OPERATOR = "OPERATOR"
    private val sdfTime = SimpleDateFormat("HH:mm")
    private var sdfDate = SimpleDateFormat("dd.MM")
    var userIsSender = false
    var operatorIsSender = false

    fun setClientName(context: Context, name: String) {
        val pref = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
        pref.edit().putString(NAME, name).apply()
    }

    fun getClientName(context: Context): String {
        return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).getString(NAME, "").toString()
    }

    fun setOperatorName(context: Context, name: String) {
        val pref = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
        pref.edit().putString(OPERATOR, name).apply()
    }

    fun getOperatorName(context: Context): String {
        return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).getString(OPERATOR, "").toString()
    }

    fun getDate(): String {
        // Текущее время
        val currentDate = Date()
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return timeFormat.format(currentDate)
    }

    fun setHHUserData(context: Context, userData: Set<String>) {
        val pref = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
        pref.edit().putStringSet(HH_USER, userData).apply()
    }

    fun getLanguageID(context: Context): String {
        return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
            .getString(LANGUAGE_ID, "").toString()
    }

    fun saveLanguageID(context: Context, lastMessage: String) {
        val pref = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
        pref.edit().putString(LANGUAGE_ID, lastMessage).apply()
    }

    fun saveAppId(context: Context, appId: String) {
        val pref = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
        pref.edit()
            .putString(APP_ID_KEY, appId)
            .apply()
    }

    fun saveRegId(context: Context, regId: String) {
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
            .edit().putString(REG_ID, regId).apply()
    }

    fun restoreRegId(context: Context): String? {
        return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).getString(REG_ID, "")
    }

    fun saveIsSelectedLanguage(context: Context, choice: Boolean) {
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
            .edit().putBoolean(LANGUAGE_CHOICE, choice).apply()
    }

    fun restoreIsSelectedLanguage(context: Context): Boolean {
        return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).getBoolean(
            LANGUAGE_CHOICE, false)
    }


    @TargetApi(Build.VERSION_CODES.O)
    fun getMouhtName(mounht: Int): String {
        val jan = Month.of(mounht)
        val loc = Locale.forLanguageTag(Locale.getDefault().language)
        return jan.getDisplayName(TextStyle.FULL, loc).substring(0, 3) + ","
    }

    fun formatTimestamp1(timestamp: String?): String {
        return if (timestamp != null && "" != timestamp) {
            var d = java.lang.Double.parseDouble(timestamp).toLong()
            if (timestamp.length <= 11) {
                d = d * 1000
            }
            val time = Date(d)
            sdfTime.format(time)
        } else {
            ""
        }
    }
}
