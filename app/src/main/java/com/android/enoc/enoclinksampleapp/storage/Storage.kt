package com.android.enoc.enoclinksampleapp.storage


interface Storage {
    fun setString(key: String, value: String)
    fun getString(key: String): String
}