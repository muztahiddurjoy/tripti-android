package com.muztahidrahman.tripti.api

import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import javax.net.ssl.SSLSocketFactory

object SSLBypass {

    // Create an unsafe trust manager that trusts all certificates
    fun createUnsafeTrustManager(): X509TrustManager {
        return object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                // Trust all client certificates
            }

            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                // Trust all server certificates
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        }
    }

    // Create an unsafe SSL socket factory
    fun createUnsafeSSLSocketFactory(): SSLSocketFactory {
        try {
            val trustAllCerts = arrayOf<TrustManager>(createUnsafeTrustManager())
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            return sslContext.socketFactory
        } catch (e: Exception) {
            throw RuntimeException("Failed to create unsafe SSL socket factory", e)
        }
    }

    // Create a hostname verifier that verifies all hostnames
    val unsafeHostnameVerifier = { hostname: String, session: javax.net.ssl.SSLSession -> true }
}