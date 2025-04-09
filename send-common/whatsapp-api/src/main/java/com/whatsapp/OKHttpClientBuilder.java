package com.whatsapp;

import okhttp3.OkHttpClient;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.*;
import java.util.concurrent.TimeUnit;

class OKHttpClientBuilder {

    public static X509Certificate[] A01;

    public static OkHttpClient.Builder buildOKHttpClient() {
        try {
            TrustManager[] trustAllCerts = buildTrustManagersNot();
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, null);
            MySSLSocketFactory factory = new MySSLSocketFactory(sslContext);
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(factory, (X509TrustManager) trustAllCerts[0]);
            builder.connectTimeout(30, TimeUnit.SECONDS);
            builder.readTimeout(30, TimeUnit.SECONDS);
            builder.retryOnConnectionFailure(true);//重试连接
            return builder;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
            return new OkHttpClient.Builder();
        }
    }

    public static TrustManager[] buildTrustManagersNot() {
        return new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }
                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
        };
    }

}
