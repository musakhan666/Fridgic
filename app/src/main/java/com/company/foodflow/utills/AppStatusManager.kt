package com.company.foodflow.utills

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth

class AppStatusManager(val context: Context) {

    companion object {
        private const val PREF_NAME = "service_status_pref"

        // Keys for modes and settings
        private const val TUTORIAL = "tutorial"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    /**
     * Tutorial handling
     */
    fun hasShownTutorial(): Boolean {
        return sharedPreferences.getBoolean(TUTORIAL, false)
    }

    fun setTutorialShown() {
        sharedPreferences.edit().putBoolean(TUTORIAL, true).apply()
    }

    fun isLoggedIn(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }
}
