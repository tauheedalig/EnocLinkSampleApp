package com.android.enoc.enoclinksampleapp.repository

import com.android.enoc.enoclinksampleapp.api.ApiClient
import com.android.enoc.enoclinksampleapp.api.ApiRequest
import com.android.enoc.enoclinksampleapp.api.RequestMethod
import com.android.enoc.enoclinksampleapp.api.ResponseListener
import com.android.enoc.enoclinksampleapp.ui.main.data.model.LoggedInUser
import com.android.enoc.enoclinksampleapp.utils.ApiConstants
import com.google.gson.JsonObject
import org.json.JSONObject
import java.util.*
import javax.inject.Inject

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class MasterRepository @Inject constructor(private val apiClient: ApiClient) {

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }

    fun logout() {
        user = null
    }

    fun login(username: String, password: String, responseListener: ResponseListener) {

            try {
                // TODO: handle loggedInUser authentication
                var jsonObject = JsonObject()
                jsonObject.addProperty("email", username)
                jsonObject.addProperty("password", password)
                val apiRequest = ApiRequest()
                apiRequest.setOfflinePath("authentication.json")
                apiRequest.setBody(jsonObject.toString())
                apiRequest.setRequestMethod(RequestMethod.POST)

                apiClient.executeRequest(apiRequest,responseListener)

            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }

    fun getUserInfo(userId: String, token: String, responseListener: ResponseListener) {

        try {

            val apiRequest = ApiRequest()
            apiRequest.setOfflinePath("get_user_profile.json")
            apiRequest.setRequestMethod(RequestMethod.GET)
            apiRequest.setPath(userId)
            apiRequest.getHeaders().plus(pair = Pair("authorization","Bearer "+token))

            apiClient.executeRequest(apiRequest,responseListener)

        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    fun uploadImage(imageBytes: ByteArray, fileName: String, token:String, responseListener: ResponseListener) {

        val apiRequest =ApiRequest()
        apiRequest.setHasMultipartResponseContent(true);
        apiRequest.setOfflinePath("upload_image.json")
        apiRequest.setPath(ApiConstants.UPLOAD_IMAGE)
        apiRequest.setBody(JSONObject().toString());
        apiRequest.setFilePaths(ArrayList<String>());
        apiRequest.setFileKeys(ArrayList<String>());
        apiRequest.setFileDataMap(apiRequest.getFileDataMap().plus(Pair(fileName,imageBytes)))
        apiRequest.setHeaders(apiRequest.getHeaders().plus(pair = Pair("authorization","Bearer "+token)))
        apiClient.executeMultiPartRequest(apiRequest,responseListener)
    }


}