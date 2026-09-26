package com.itmart.gharkharch.RemoteDataAndApis;

import android.content.Context;

import com.itmart.gharkharch.R;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

public class OkHTTPClient {

    private OkHTTPClient() {
    }

    private static OkHttpClient client;

    public static OkHttpClient getHttpClient(Context context) {
        if (client == null) {

            try {
                /*
                // Load the certificate from a resource file
                InputStream certificateInputStream = context.getResources().openRawResource(R.raw.certificate);

                // Create a KeyStore containing our certificate
                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                keyStore.load(null); // No password needed for an empty keystore
                keyStore.setCertificateEntry("ca", certificateFactory.generateCertificate(certificateInputStream));

                // Create a TrustManager that trusts the certificate in our KeyStore
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                tmf.init(keyStore);
                X509TrustManager trustManager = (X509TrustManager) tmf.getTrustManagers()[0];

                // Create an SSLContext with our custom TrustManager
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, new X509TrustManager[]{trustManager}, null);
                */

                // Build the OkHttp client
                client = new OkHttpClient.Builder()
//                        .sslSocketFactory(sslContext.getSocketFactory(), trustManager)
                        .build();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return client;
    }
}
