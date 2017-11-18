package com.amandariu.tagger.demo.utils;

import android.util.Log;

import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

/**
 * Various helper methods for working with HTTP.
 *
 * @author Amanda Riu
 */
public abstract class HttpUtils {

    /**
     * Creates an OkHttpClient that accepts all certificates. This <b>SHOULD NOT</b> be used
     * in production.
     * @return An OkHttpClient that accepts all certificates
     */
    public static OkHttpClient.Builder getTrustAllOkHttpClientBuilder() {
        SSLContext sslContext;

        try {
            sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, HttpUtils.getTrustAllCertsManagerArray(), new java.security.SecureRandom());
        } catch (GeneralSecurityException se) {
            Log.e("OkHttpClient", "Error creating custom SSL context", se);
            throw new AssertionError();
        }

        return new OkHttpClient.Builder()
                .sslSocketFactory(sslContext.getSocketFactory());
    }

    /**
     * Creates a TrustManager that accepts all certificates. This <b>SHOULD NOT</b> be used
     * in production.
     * @return A TrustManager that accepts all certificates
     */
    public static TrustManager[] getTrustAllCertsManagerArray() {

        return new TrustManager[] { new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }
        }};
    }


    /**
     * Various header labels as constants
     */
    public static class HeaderContracts {
        public static final String HEADER_CONTENT_TYPE      = "Content-Type";
    }
}
