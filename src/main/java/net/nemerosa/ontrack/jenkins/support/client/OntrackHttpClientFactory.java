package net.nemerosa.ontrack.jenkins.support.client;

import net.nemerosa.ontrack.jenkins.OntrackConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.net.MalformedURLException;
import java.net.URL;

public final class OntrackHttpClientFactory {

    public static OntrackHttpClient createHttpClient() throws MalformedURLException {
        OntrackConfiguration configuration = OntrackConfiguration.getOntrackConfiguration();
        URL url = new URL(configuration.getOntrackUrl());
        String username = configuration.getOntrackUser();
        String password = configuration.getOntrackPassword();

        HttpHost host = new HttpHost(
                url.getHost(),
                url.getPort(),
                url.getProtocol()
        );

        CookieStore cookieStore = new BasicCookieStore();

        // Defaults
        HttpClientContext httpContext = HttpClientContext.create();
        HttpClientBuilder builder = HttpClientBuilder.create()
                .setConnectionManager(new PoolingHttpClientConnectionManager());

        // Basic authentication
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {

            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(
                    new AuthScope(host),
                    new UsernamePasswordCredentials(username, password)
            );

            AuthCache authCache = new BasicAuthCache();
            authCache.put(host, new BasicScheme());


            httpContext.setCredentialsProvider(credentialsProvider);
            httpContext.setAuthCache(authCache);
            httpContext.setCookieStore(cookieStore);
        }

        // OK
        CloseableHttpClient httpClient = builder.build();
        return new OntrackHttpClient(host, httpClient, httpContext);
    }

    private OntrackHttpClientFactory() {
    }

}
