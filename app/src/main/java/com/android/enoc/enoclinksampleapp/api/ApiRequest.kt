package com.android.enoc.enoclinksampleapp.api

import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.Map


/** Created by tauheed on 10, June, 2021
 * ADIB,
 * AbuDhabi, UAE.
 */
class ApiRequest {
    private  var path: String=""
    private  var body: String=""
    private  var offlinePath: String = ""
    private  var requestMethod: String=""
    private  var fileDataMap: Map<String, ByteArray> = HashMap()
    private var allowEncryption = true
    private var hasMultipartResponseContent = false
    private var headers: Map<String, String> = HashMap()
    private var queryParams: Map<String, String> = HashMap()
    private var fieldsMap: Map<String, String> = HashMap()
    private  var filePaths= ArrayList<String>()
    private  var fileKeys= ArrayList<String>()

    fun getPath(): String {
        return path
    }

    fun setPath(path: String) {
        this.path = path
    }

    fun getBody(): String {
        return body
    }

    fun setBody(body: String) {
        this.body = body
    }

    fun getRequestMethod(): String {
        return requestMethod
    }

    fun setRequestMethod(requestMethod: String) {
        this.requestMethod = requestMethod
    }

    fun isAllowEncryption(): Boolean {
        return allowEncryption
    }

    fun setAllowEncryption(allowEncryption: Boolean) {
        this.allowEncryption = allowEncryption
    }

    fun hasMultipartResponseContent(): Boolean {
        return hasMultipartResponseContent
    }

    fun setHasMultipartResponseContent(hasMultipartResponseContent: Boolean) {
        this.hasMultipartResponseContent = hasMultipartResponseContent
    }


    fun getHeaders(): Map<String, String> {
        return headers
    }

    fun setHeaders(headers: Map<String, String>) {
        this.headers = headers
    }

    fun getQueryParams(): Map<String, String> {
        return queryParams
    }

    fun setQueryParams(queryParams: Map<String, String>) {
        this.queryParams = queryParams
    }

    fun getFieldsMap(): Map<String, String> {
        return fieldsMap
    }

    fun setFieldsMap(fieldsMap: Map<String, String>) {
        this.fieldsMap = fieldsMap
    }

    fun getFilePaths(): ArrayList<String> {
        return filePaths
    }

    fun setFilePaths(filePaths: ArrayList<String>) {
        this.filePaths = filePaths
    }

    fun getFileKeys(): ArrayList<String> {
        return fileKeys
    }

    fun setFileKeys(fileKeys: ArrayList<String>) {
        this.fileKeys = fileKeys
    }

    fun getFileKey(index: Int): String {
        return if (fileKeys != null && fileKeys!!.size > index) {
            if (fileKeys!![index].isNullOrEmpty()) fileKeys!![index] else "KEY_DOCUMENT"
        } else "KEY_DOCUMENT"
    }

    fun getOfflinePath(): String? {
        return offlinePath
    }

    fun setOfflinePath(offlinePath: String) {
        this.offlinePath = offlinePath
    }

    override fun toString(): String {
        return "ApiRequest{" +
                "path='" + path + '\'' +
                ", body='" + body + '\'' +
                ", offlinePath='" + offlinePath + '\'' +
                ", requestMethod='" + requestMethod + '\'' +
                ", allowEncryption=" + allowEncryption +
                ", hasMultipartResponseContent=" + hasMultipartResponseContent +
                ", headers=" + headers +
                ", queryParams=" + queryParams +
                ", filePaths=" + filePaths +
                ", fileKeys=" + fileKeys +
                '}'
    }

    fun getFileDataMap(): Map<String, ByteArray> {
        return fileDataMap
    }

    fun setFileDataMap(fileDataMap: Map<String, ByteArray>) {
        this.fileDataMap =fileDataMap
    }


}