package com.sae.wavetime.interceptor

import com.sae.wavetime.local.AppDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val dataStore: AppDataStore
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val token = dataStore.cachedAccessToken

        val request = original.newBuilder().apply {
            if (!token.isNullOrEmpty()) {
                header("Authorization", "Bearer $token")
            }
        }.build()

        val response = chain.proceed(request)

        if (response.code == 401) {
            CoroutineScope(Dispatchers.IO).launch {
                dataStore.clearAccessToken()
            }
        }

        return response
    }
}