package com.android.enoc.enoclinksampleapp.ui.main.ui.login

import com.android.enoc.enoclinksampleapp.ui.main.data.model.LoggedInUser

/**
 * User details post authentication that is exposed to the UI
 */
data class LoggedInUserView(
    val userData: LoggedInUser
    //... other data fields that may be accessible to the UI
)