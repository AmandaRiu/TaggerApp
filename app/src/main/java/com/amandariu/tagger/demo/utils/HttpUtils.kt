package com.amandariu.tagger.demo.utils

import android.util.Log

import java.security.GeneralSecurityException
import java.security.cert.CertificateException
import java.security.cert.X509Certificate

import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

import okhttp3.OkHttpClient

/**
 * Creates an OkHttpClient that accepts all certificates. This **SHOULD NOT** be used
 * in production.
 * @return An OkHttpClient that accepts all certificates
 */
val trustAllOkHttpClientBuilder: OkHttpClient.Builder
    get() {
        val sslContext: SSLContext

        try {
            sslContext = SSLContext.getInstance("TLSv1.2")
            sslContext.init(null, trustAllCertsManagerArray, java.security.SecureRandom())
        } catch (se: GeneralSecurityException) {
            Log.e("OkHttpClient", "Error creating custom SSL context", se)
            throw AssertionError()
        }

        return OkHttpClient.Builder()
                .sslSocketFactory(sslContext.socketFactory)
    }

/**
 * Creates a TrustManager that accepts all certificates. This **SHOULD NOT** be used
 * in production.
 * @return A TrustManager that accepts all certificates
 */
private val trustAllCertsManagerArray: Array<TrustManager>
    get() = arrayOf(object : X509TrustManager {
        @Throws(CertificateException::class)
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
        }

        @Throws(CertificateException::class)
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return arrayOf()
        }
    })