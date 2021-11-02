package com.web.template.Utils.Zapi;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;

import java.net.URL;

/*
    @desc      This class holds http helper methods
    @author    Ostan Dsouza
    @Date      02/06/2020
 */

public class RestClient {
    private static CloseableHttpClient httpclient;
    private static HttpClientContext context;

    public static HttpClientContext getContext(String hostAddressWithProtocol, String userName, String password) {
        try {
            HttpResponse response = null;
            URL url;
            url = new URL(hostAddressWithProtocol);
            HttpHost targetHost = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));

            AuthCache authCache = new BasicAuthCache();
            authCache.put(targetHost, new BasicScheme());

            context = HttpClientContext.create();
            context.setCredentialsProvider(credsProvider);
            context.setAuthCache(authCache);
            return context;
        }
        catch(Exception e) {
            System.out.println(e);
        }
        return context;
    }

    public static CloseableHttpClient getHttpclient() {
        try {
            int connectTimeout = 10;
            int dataWaitTimeout = 3600;
            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(dataWaitTimeout * 1000)
                    .setConnectionRequestTimeout(dataWaitTimeout * 1000)
                    .setSocketTimeout(dataWaitTimeout * 1000).build();
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build(),
                    SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            httpclient = HttpClients.custom().setSSLSocketFactory(sslsf)/*.setDefaultRequestConfig(config)*/.build();
            return httpclient;
        }
        catch(Exception e) {
            System.out.println(e);
        }
        return httpclient;
    }
}
