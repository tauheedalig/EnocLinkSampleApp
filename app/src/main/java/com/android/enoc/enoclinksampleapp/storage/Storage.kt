package com.android.enoc.enoclinksampleapp.storage

/** Created by tauheed on 12, June, 2021
 * ADIB,
 * AbuDhabi, UAE.
 */
interface Storage {
    fun setString(key: String, value: String)
    fun getString(key: String): String
}