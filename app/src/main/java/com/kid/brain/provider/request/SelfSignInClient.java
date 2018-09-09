package com.kid.brain.provider.request;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;

/**
 * Created by khiemnt on 11/3/16.
 */
public class SelfSignInClient {
    private Context context;

    public SelfSignInClient(Context context) {
        this.context = context;
    }

    public OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();

//        Certificate certificate = getCertificate();
//        KeyStore keyStore = createKeyStoreTrustedCAs(certificate);
//        TrustManagerFactory managerFactory = createTrustManagerCAs(keyStore);
//        SSLContext sslContext = createSSLSocketFactory(managerFactory);
//
//        okHttpClient.sslSocketFactory(sslContext.getSocketFactory());
        okHttpClient.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return ( hostname.equals("account.hightrusted.com")
                        || hostname.equals("certifyid.hightrusted.com")
                        || hostname.equals("wisigndoc.hightrusted.com")
                        || hostname.equals("wisigndoc.wisekey.com")
                        || hostname.equals("cidapi.wisekey.com")
                        || hostname.equals("signature.hightrusted.com") ) ;
            }
        });

        // If you need an Interceptor to add some header
        //okHttpClient.addInterceptor();
        okHttpClient.readTimeout(60, TimeUnit.SECONDS);
        okHttpClient.connectTimeout(60, TimeUnit.SECONDS);
        return okHttpClient.build();
    }

    /*public SSLSocketFactory socketFactory(){
        Certificate certificate = getCertificate();
        KeyStore keyStore = createKeyStoreTrustedCAs(certificate);
        TrustManagerFactory managerFactory = createTrustManagerCAs(keyStore);
        SSLContext sslContext = createSSLSocketFactory(managerFactory);
        return sslContext.getSocketFactory();
    }

    // creating an SSLSocketFactory that uses our TrustManager
    private SSLContext createSSLSocketFactory(TrustManagerFactory managerFactory) {
        final String PROTOCOL = "TLS";
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance(PROTOCOL);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            assert sslContext != null;
            sslContext.init(null, managerFactory.getTrustManagers(), null);
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return sslContext;
    }

    // creating a TrustManager that trusts the CAs in our KeyStore
    private TrustManagerFactory createTrustManagerCAs(KeyStore keyStore) {
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory managerFactory = null;
        try {
            managerFactory = TrustManagerFactory.getInstance(tmfAlgorithm);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            assert managerFactory != null;
            managerFactory.init(keyStore);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return managerFactory;
    }

    // creating a KeyStore containing our trusted CAs
    private KeyStore createKeyStoreTrustedCAs(Certificate certificate) {
        final String ALIAS_CA = "ca";
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance(keyStoreType);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        try {
            assert keyStore != null;
            keyStore.load(null, null);
        } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
            e.printStackTrace();
        }
        try {
            keyStore.setCertificateEntry(ALIAS_CA, certificate);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return keyStore;
    }

    // creating a Certificate
    public Certificate getCertificate() {
        Certificate certificate = null;
        CertificateFactory certificateFactory = loadCertificateAuthorityFromResources();
        InputStream inputStream = getCAFromResources();
        try {
            certificate = certificateFactory.generateCertificate(inputStream);
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        return certificate;
    }

    // loading CAs from an InputStream
    private CertificateFactory loadCertificateAuthorityFromResources() {
        final String CERT_TYPE = "X.509";
        InputStream certificateAuthority = getCAFromResources();
        CertificateFactory certificateFactory = null;
        try {
            certificateFactory = CertificateFactory.getInstance(CERT_TYPE);
        } catch (CertificateException e) {
            e.printStackTrace();
        }

        try {
            assert certificateFactory != null;
            certificateFactory.generateCertificate(certificateAuthority);
        } catch (CertificateException e) {
            e.printStackTrace();
        } finally {
            try {
                certificateAuthority.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return certificateFactory;
    }


    // loading CAs from Resources
    private InputStream getCAFromResources() {
        return context.getResources().openRawResource(R.raw.certificate);
    }*/
}
