package com.android.enoc.enoclinksampleapp.api

/** Created by tauheed on 10, June, 2021
 * ADIB,
 * AbuDhabi, UAE.
 */
open interface ResponseListener {
    fun onSuccess(response: String)
    fun onFailure(reponse: String)
}