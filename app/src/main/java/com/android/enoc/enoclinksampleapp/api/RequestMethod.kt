package com.android.enoc.enoclinksampleapp.api

import androidx.annotation.StringDef
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy


/** Created by tauheed on 10, June, 2021
 * ADIB,
 * AbuDhabi, UAE.
 */
public class RequestMethod {


    @Retention(RetentionPolicy.SOURCE)
    @StringDef(POST_FORM_URL_ENCODED, GET, POST, PATCH, PUT, DELETE)
    annotation class RequestTypes
    companion object {
        const val POST_FORM_URL_ENCODED = "POST_FORM_URL_ENCODED"
        const val GET = "GET"
        const val POST = "POST"
        const val PATCH = "PATCH"
        const val PUT = "PUT"
        const val DELETE = "DELETE"
    }

}