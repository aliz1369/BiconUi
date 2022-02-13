package com.tivasoft.biconui.utils

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * class for injecting tokens in REST Requests
 *
 * @param context of application
 *
 */
class TokenInterceptor @Inject constructor(
        @ApplicationContext private val context: Context,
        private val sharedPreferences: SharedPreferences
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        if (original.url.encodedPath.contains("register") ||
                original.url.encodedPath.contains("login") ||
                original.url.encodedPath.contains("verifyActiveCode") ||
                original.url.encodedPath.contains("sendActiveCode")
        ) {
            return chain.proceed(original)
        }

        val token = sharedPreferences.getString("token", "") ?: ""
        val originalHttpUrl = original.url
        val requestBuilder = original.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .url(originalHttpUrl)
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}