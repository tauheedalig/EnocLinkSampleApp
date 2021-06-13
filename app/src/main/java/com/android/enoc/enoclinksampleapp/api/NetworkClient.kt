package com.android.enoc.enoclinksampleapp.api

import androidx.annotation.NonNull
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


/** Created by tauheed on 08, June, 2021
 * ADIB,
 * AbuDhabi, UAE.
 */


@Module
@InstallIn(SingletonComponent::class)
object NetworkClient {


    private val TIMEOUT_DURATION = 120000 // 2 minutes


    @Provides
    @Singleton
    fun getApiInterface(retrofit: Retrofit): ApiInterface {
        return retrofit.create(ApiInterface::class.java)
    }

    @NonNull
    private fun getOkHttpClient(): OkHttpClient {

        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
            .followRedirects(true)
            .readTimeout(TIMEOUT_DURATION.toLong(), TimeUnit.MILLISECONDS)
            .connectTimeout(TIMEOUT_DURATION.toLong(), TimeUnit.MILLISECONDS)
            .build()

    }


    private fun getRetrofitInstance(webHostUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(webHostUrl)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(getOkHttpClient())
            .build()
    }


    @Provides
    @Singleton
    fun getClient(): Retrofit {
        return getRetrofitInstance("https://enoc.com")
    }

}