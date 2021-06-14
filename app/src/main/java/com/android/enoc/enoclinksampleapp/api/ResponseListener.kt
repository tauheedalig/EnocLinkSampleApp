package com.android.enoc.enoclinksampleapp.api


open interface ResponseListener {
    fun onSuccess(response: String)
    fun onFailure(reponse: String)
}