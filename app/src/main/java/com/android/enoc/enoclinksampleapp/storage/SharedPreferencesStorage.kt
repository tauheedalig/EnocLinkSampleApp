package com.android.enoc.enoclinksampleapp.storage

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/** Created by tauheed on 12, June, 2021
 * ADIB,
 * AbuDhabi, UAE.
 */
class SharedPreferencesStorage @Inject constructor(@ApplicationContext context: Context) : Storage {

    private val sharedPreferences = context.getSharedPreferences(
        "Dagger",
        Context.MODE_PRIVATE
    )

    override fun setString(key: String, value: String) {
        with(sharedPreferences.edit()) {
            putString(key, value)
            apply()
        }
    }

    override fun getString(key: String): String {
        return sharedPreferences.getString(key, "")!!
    }
}