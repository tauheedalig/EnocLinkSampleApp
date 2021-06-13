package com.android.enoc.enoclinksampleapp.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*




interface ApiInterface {
    @GET("{end_point}")
    fun getRequest(
       @HeaderMap map: Map<String, String>,
       @Path(value = "end_point", encoded = true) endPoint: String,
       @QueryMap queryMap: Map<String, String>
    ): Call<String>

    @POST("{end_point}")
    fun postRequest(
       @HeaderMap map: Map<String, String>,
       @Path(value = "end_point", encoded = true) endPoint: String,
       @Body json: String
    ): Call<String>

    @POST("{end_point}")
    fun postRequest(
       @HeaderMap map: Map<String, String>,
       @Path(value = "end_point", encoded = true) endPoint: String,
       @QueryMap(encoded = true) queryMap: Map<String, String>,
       @Body json: String
    ): Call<String>

    @PUT("{end_point}")
    fun putRequest(
       @HeaderMap map: Map<String, String>,
       @Path(value = "end_point", encoded = true) endPoint: String,
       @QueryMap(encoded = true) queryMap: Map<String, String>,
       @Body json: String
    ): Call<String>

    @DELETE("{end_point}")
    fun deleteRequest(
       @HeaderMap map: Map<String, String>,
       @Path(value = "end_point", encoded = true) endPoint: String
    ): Call<String>

    @Multipart
    @POST("{end_point}")
    fun uploadMultipartData(
       @HeaderMap map: Map<String, String>,
       @Part("data") data: RequestBody,
       @Part file: List<MultipartBody.Part>,
       @Path(value = "end_point", encoded = true) endPoint: String
    ): Call<String>

    @Multipart
    @POST("{end_point}")
    fun uploadMultipartWithNoResponseContent(
       @HeaderMap map: Map<String, String>,
       @Part("data") data: RequestBody,
       @Part file: List<MultipartBody.Part>,
       @Path(
          value = "end_point",
          encoded = true
       ) endPoint: String
    ): Call<Void>

    @PATCH("{end_point}")
    fun patchRequest(
       @HeaderMap map: Map<String, String>,
       @Path(value = "end_point", encoded = true) endPoint: String,
       @QueryMap(encoded = true) queryMap: Map<String, String>,
       @Body json: String
    ): Call<String>
}