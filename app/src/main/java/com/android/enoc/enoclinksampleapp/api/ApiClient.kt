package com.android.enoc.enoclinksampleapp.api

import android.content.Context
import android.webkit.MimeTypeMap
import com.android.enoc.enoclinksampleapp.BuildConfig
import com.android.enoc.enoclinksampleapp.utils.CommonConstants
import com.android.enoc.enoclinksampleapp.utils.Util
import com.google.gson.JsonParseException
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.create
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import javax.inject.Inject


/** Created by tauheed on 07, June, 2021
 * ADIB,
 * AbuDhabi, UAE.
 */

open class ApiClient @Inject constructor(
        @ApplicationContext private val context: Context,
        private val apiInterface: ApiInterface
) {


    open fun executeRequest(request: ApiRequest, responseListener: ResponseListener) {
        if (BuildConfig.DEBUG) {
            executeOfflineRequest(request, responseListener)
        } else {
            val call = makeRetrofitRequest(request)
            call.enqueue(object : Callback<String?> {
                override fun onResponse(call: Call<String?>, response: Response<String?>) {
                    try {
                        if (response.isSuccessful) {
                            responseListener.onSuccess(response.body().toString())
                        } else {
                            responseListener.onFailure(response.errorBody().toString())
                        }

                    } catch (e: JsonParseException) {

                    } catch (e: JSONException) {

                    }
                }

                override fun onFailure(call: Call<String?>, throwable: Throwable) {

                }
            })
        }
    }

    private fun makeRetrofitRequest(request: ApiRequest): Call<String> {

        return when (request.getRequestMethod()) {
            RequestMethod.POST -> apiInterface.postRequest(
                    request.getHeaders(),
                    request.getPath(),
                    request.getQueryParams(),
                    request.getBody()
            )
            RequestMethod.GET -> apiInterface.getRequest(
                    request.getHeaders(),
                    request.getPath(),
                    request.getQueryParams()
            )
            RequestMethod.PUT -> apiInterface.putRequest(
                    request.getHeaders(),
                    request.getPath(),
                    request.getQueryParams(),
                    request.getBody()
            )
            RequestMethod.DELETE -> apiInterface.deleteRequest(
                    request.getHeaders(),
                    request.getPath()
            )
            RequestMethod.PATCH -> apiInterface.patchRequest(
                    request.getHeaders(),
                    request.getPath(),
                    request.getQueryParams(),
                    request.getBody()
            )
            else -> throw IllegalArgumentException("ApiRequest method is not set")
        }
    }

    private fun executeOfflineRequest(request: ApiRequest, responseListener: ResponseListener) {
        val localData: String = Util.readFileFromAssets(context, request.getOfflinePath())
        android.os.Handler().postDelayed(Runnable { responseListener.onSuccess(localData.toString()) }, 3000)

    }


    open fun executeMultiPartRequest(
            apiRequest: ApiRequest,
            responseListener: ResponseListener
    ) {
        if (BuildConfig.DEBUG) {
            executeOfflineRequest(apiRequest, responseListener)
        } else {
            val multipartFileList: MutableList<MultipartBody.Part> = ArrayList()
            if (!apiRequest.getFilePaths().isNullOrEmpty()) {
                for (i in apiRequest.getFilePaths().indices) {
                    if (apiRequest.getFilePaths()[i].isNullOrBlank()) {
                        continue
                    }
                    val filePart: MultipartBody.Part = prepareFilePart(
                            apiRequest.getFileKey(i),
                            apiRequest.getFilePaths()[i]
                    )
                    multipartFileList.add(filePart)
                }
            } else if (!apiRequest.getFileDataMap().isNullOrEmpty()) {
                val fileDataMap = apiRequest.getFileDataMap()
                for ((key, value) in fileDataMap) {
                    val filePart: MultipartBody.Part = getPartFromByteArray(key, value)
                    multipartFileList.add(filePart)
                }
            }
            val requestBody = createPartFromString(apiRequest.getBody())
            if (apiRequest.hasMultipartResponseContent()) {
                executeMultiPartRequestWithContent(
                        apiInterface,
                        apiRequest,
                        requestBody,
                        multipartFileList,
                        responseListener
                )
            } else {
                executeMultiPartRequestWithNoContent(
                        apiInterface,
                        apiRequest,
                        requestBody,
                        multipartFileList,
                        responseListener
                )
            }
        }
    }

    fun executeMultiPartRequestWithContent(
            apiInterface: ApiInterface, apiRequest: ApiRequest, requestBody: RequestBody,
            multipartFileList: List<MultipartBody.Part>, responseListener: ResponseListener
    ) {
        val call = apiInterface.uploadMultipartData(
                apiRequest.getHeaders(),
                requestBody,
                multipartFileList,
                apiRequest.getPath()
        )
        call.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if (response.isSuccessful) {

                    responseListener.onSuccess(response.body().toString())
                } else {
                    responseListener.onSuccess(response.errorBody().toString())
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {

            }
        })
    }

    fun executeMultiPartRequestWithNoContent(
            apiInterface: ApiInterface, apiRequest: ApiRequest, requestBody: RequestBody,
            multipartFileList: List<MultipartBody.Part>, responseListener: ResponseListener
    ) {
        val call = apiInterface.uploadMultipartWithNoResponseContent(
                apiRequest.getHeaders(),
                requestBody,
                multipartFileList,
                apiRequest.getPath()
        )
        call.enqueue(object : Callback<Void?> {
            override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                if (response.isSuccessful) {
                    responseListener.onSuccess(response.body().toString())
                } else {
                    responseListener.onSuccess(response.errorBody().toString())
                }
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {

            }
        })
    }

    fun createPartFromString(requestParams: String): RequestBody {
        return create(
                "application/json".toMediaTypeOrNull(),
                requestParams
        )
    }

    fun getPartFromByteArray(name: String, byteArray: ByteArray): MultipartBody.Part {
        val reqFile: RequestBody =
                create(CommonConstants.MIME_TYPE_IMAGE.toMediaTypeOrNull(), byteArray)
        return MultipartBody.Part.createFormData(
                CommonConstants.KEY_FILES,
                name,
                reqFile
        )
    }

    fun prepareFilePart(fileKey: String, filePath: String): MultipartBody.Part {
        val uploadFile = File(filePath)
        // MultipartBody.Part is used to send also the actual file name
        val extension = MimeTypeMap.getFileExtensionFromUrl(filePath)
        if (extension.isNotEmpty()) {
            val mimeTypeFromExtension =
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            if (!mimeTypeFromExtension.isNullOrBlank()) {
                val requestBody: RequestBody = create(
                        mimeTypeFromExtension.toMediaTypeOrNull(),
                        uploadFile
                )
                return MultipartBody.Part.createFormData(fileKey, uploadFile.getName(), requestBody)
            }
        }
        return MultipartBody.Part.createFormData(fileKey, uploadFile.getName())
    }

}



