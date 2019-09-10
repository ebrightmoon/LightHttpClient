package com.ebrightmoon.htppclient.util;

import android.content.Context;


import com.ebrightmoon.htppclient.api.AppConfig;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.CertificatePinner;
import okio.Buffer;

/**
 * 作者：create by  Administrator on 2019/1/25
 * 邮箱：2315813288@qq.com
 */
public class SSLUtils {
    private static final String CERT="MIIGNjCCBR6gAwIBAgIQD01jC00tnmUL9008O3YSZzANBgkqhkiG9w0BAQsFADBe\n" +
            "MQswCQYDVQQGEwJVUzEVMBMGA1UEChMMRGlnaUNlcnQgSW5jMRkwFwYDVQQLExB3\n" +
            "d3cuZGlnaWNlcnQuY29tMR0wGwYDVQQDExRHZW9UcnVzdCBSU0EgQ0EgMjAxODAe\n" +
            "Fw0xODAzMDEwMDAwMDBaFw0xOTAzMDExMjAwMDBaMIGZMQswCQYDVQQGEwJDTjES\n" +
            "MBAGA1UEBwwJ5bm/5bee5biCMUUwQwYDVQQKDDzkuK3lm73kurrmsJHotKLkuqfk\n" +
            "v53pmanogqHku73mnInpmZDlhazlj7jlub/kuJznnIHliIblhazlj7gxGDAWBgNV\n" +
            "BAsMD+S/oeaBr+aKgOacr+mDqDEVMBMGA1UEAwwMKi5waWNjZ2QuY29tMIIBIjAN\n" +
            "BgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAygHJ2+UBqy9FapkGvpBNZw9A4n+S\n" +
            "oMX1Pk8zkQLAzfPOyHsoeiaJSj6Ahmh19EFWWJmwzlYDWCt02tp54uRQ511mj9ZG\n" +
            "ONNUnjoc2N3+YbOaMkmV4gp2Sz2Avwc3+DM3gOqsjUlDquUjJCeqpJRyXjWOj/9v\n" +
            "ck9PwvLQWzbyiaqgbmnXPae0ZZaTjtY3wl9HYQ1kg9uNBqfRtAI1KjGNoDCgA4h+\n" +
            "U+UkowdnxM8uYroPjtxvv6FKekZg05DQ/yBf4nPmSrnL1jVFWaCALtRsxFvEqWaH\n" +
            "K8jMKwhRVou00qiT9wHO0+Pd7uDngt+SLwhdUCQfYaUtL/gmtHoxKq1qEQIDAQAB\n" +
            "o4ICsjCCAq4wHwYDVR0jBBgwFoAUkFj/sJx1qFFUd7Ht8qNDFjiebMUwHQYDVR0O\n" +
            "BBYEFM6wvRP5OETYb+a13IUWtSwXEPxoMCMGA1UdEQQcMBqCDCoucGljY2dkLmNv\n" +
            "bYIKcGljY2dkLmNvbTAOBgNVHQ8BAf8EBAMCBaAwHQYDVR0lBBYwFAYIKwYBBQUH\n" +
            "AwEGCCsGAQUFBwMCMD8GA1UdHwQ4MDYwNKAyoDCGLmh0dHA6Ly9jZHAxLmRpZ2lj\n" +
            "ZXJ0LmNvbS9HZW9UcnVzdFJTQUNBMjAxOC5jcmwwTAYDVR0gBEUwQzA3BglghkgB\n" +
            "hv1sAQEwKjAoBggrBgEFBQcCARYcaHR0cHM6Ly93d3cuZGlnaWNlcnQuY29tL0NQ\n" +
            "UzAIBgZngQwBAgIwdAYIKwYBBQUHAQEEaDBmMCUGCCsGAQUFBzABhhlodHRwOi8v\n" +
            "b2NzcDEuZGlnaWNlcnQuY29tMD0GCCsGAQUFBzAChjFodHRwOi8vY2FjZXJ0cy5n\n" +
            "ZW90cnVzdC5jb20vR2VvVHJ1c3RSU0FDQTIwMTguY3J0MAkGA1UdEwQCMAAwggEG\n" +
            "BgorBgEEAdZ5AgQCBIH3BIH0APIAdwCkuQmQtBhYFIe7E6LMZ3AKPDWYBPkb37jj\n" +
            "d80OyA3cEAAAAWHf+bTPAAAEAwBIMEYCIQD0EG/wJN20/UkPa5pA2gmBi88OIbF2\n" +
            "tB+kRHE68Mxa7gIhAK/u9xWL1yG2JZ1HngAP2qeCM/skBUFrMz74/nZv0eG3AHcA\n" +
            "h3W/51l8+IxDmV+9827/Vo1HVjb/SrVgwbTq/16ggw8AAAFh3/m1VgAABAMASDBG\n" +
            "AiEA83bpsxrrJCTD292UAba4SCPqiM+rWGQ80Fh4i66E/1kCIQD90Nbps1GvhcbR\n" +
            "TOfB7nnfkdGOKPFBtkWjeh5XbqpUtTANBgkqhkiG9w0BAQsFAAOCAQEAKU8hhsRQ\n" +
            "wxBjsi5iW1mfYcQfubwCUo5PlBboMRk80fodH8qoOp2DECFk3TeK4DNY9q/LzuCf\n" +
            "dtdtBKClhxZy8mRUUPTGnMx3T+R1JNz7qf/Pa0JuOgto/tHpWnxbmUX0iyKJVmJX\n" +
            "+HzAWR11YU8N5ZuP3robuACCWu+VMu+RYuWTlfA50n9HIwBf3dzCG4NzpVFMLYGi\n" +
            "zUJkvAySt1EJ5NixMCAuQtZ6oabr24yChY/UmTAeoIqKt9l1UkSxyG6kUd+dR2Za\n" +
            "f++6dnRNGEOVprrS03lJYF6vWmqZnyBbpHA4oyE1OCoP7Gbq6KRnis32zwBqW/g0\n" +
            "aKHe62vqRM4jAw==";

    public static SSLSocketFactory getSslSocketFactory(InputStream[] certificates, InputStream bksFile, String
            password) {
        try {
            TrustManager[] trustManagers = prepareTrustManager(certificates);
            KeyManager[] keyManagers = prepareKeyManager(bksFile, password);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManager trustManager;
            if (trustManagers != null) {
                trustManager = new MyTrustManager(chooseTrustManager(trustManagers));
            } else {
                trustManager = new UnSafeTrustManager();
            }
            sslContext.init(keyManagers, new TrustManager[]{trustManager}, new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        } catch (KeyManagementException e) {
            throw new AssertionError(e);
        } catch (KeyStoreException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * SSL Pinning 获取证书
     * @return certificata
     */
    public static CertificatePinner getCertificata(String fileName) {

        Certificate ca = null;

        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
//            InputStream caInput = VMApplication.getApp().getResources().openRawResource(R.raw.test);
//            InputStream caInput =  new BufferedInputStream(VMApplication.getApp().getAssets().open(fileName));
            InputStream caInput =  new Buffer().writeUtf8(CERT).inputStream();

            try {
                ca = cf.generateCertificate(caInput);
            } finally {
                caInput.close();
            }
        } catch (CertificateException | IOException e) {
            e.printStackTrace();
        }

        String certPin = "";
        if (ca != null) {
            certPin = CertificatePinner.pin(ca);
        }
        CertificatePinner certificatePinner = new CertificatePinner.Builder()
                .add(AppConfig.BASE_URL, certPin)
                .build();

        return certificatePinner;
    }

    public static SSLSocketFactory getSSlFactory(Context context, String fileName) {

        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = new BufferedInputStream(context.getApplicationContext().getAssets().open(fileName));//把证书打包在asset文件夹中
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
            } finally {
                caInput.close();
            }

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Create an SSLContext that uses our TrustManager
            SSLContext s = SSLContext.getInstance("TLSv1", "AndroidOpenSSL");
            s.init(null, tmf.getTrustManagers(), null);

            return s.getSocketFactory();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static TrustManager[] prepareTrustManager(InputStream... certificates) {
        if (certificates == null || certificates.length <= 0) return null;
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;
            for (InputStream certificate : certificates) {
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));
                try {
                    if (certificate != null) certificate.close();
                } catch (IOException e) {
                }
            }
            TrustManagerFactory trustManagerFactory;

            trustManagerFactory = TrustManagerFactory.
                    getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

            return trustManagers;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    private static KeyManager[] prepareKeyManager(InputStream bksFile, String password) {
        try {
            if (bksFile == null || password == null) return null;

            KeyStore clientKeyStore = KeyStore.getInstance("BKS");
            clientKeyStore.load(bksFile, password.toCharArray());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm
                    ());
            keyManagerFactory.init(clientKeyStore, password.toCharArray());
            return keyManagerFactory.getKeyManagers();

        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static X509TrustManager chooseTrustManager(TrustManager[] trustManagers) {
        for (TrustManager trustManager : trustManagers) {
            if (trustManager instanceof X509TrustManager) {
                return (X509TrustManager) trustManager;
            }
        }
        return null;
    }

    public static class UnSafeHostnameVerifier implements HostnameVerifier {
        private String host;

        public UnSafeHostnameVerifier(String host) {
            this.host = host;
        }

        @Override
        public boolean verify(String hostname, SSLSession session) {
            if (this.host == null || "".equals(this.host) || !this.host.contains(hostname)) return false;
            return true;
        }
    }

    private static class UnSafeTrustManager implements X509TrustManager {
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
    }

    private static class MyTrustManager implements X509TrustManager {
        private X509TrustManager defaultTrustManager;
        private X509TrustManager localTrustManager;

        public MyTrustManager(X509TrustManager localTrustManager) throws NoSuchAlgorithmException, KeyStoreException {
            TrustManagerFactory var4 = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            var4.init((KeyStore) null);
            defaultTrustManager = chooseTrustManager(var4.getTrustManagers());
            this.localTrustManager = localTrustManager;
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            try {
                defaultTrustManager.checkServerTrusted(chain, authType);
            } catch (CertificateException ce) {
                localTrustManager.checkServerTrusted(chain, authType);
            }
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
}
