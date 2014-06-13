package net.nemerosa.ontrack.jenkins.support.client;

import net.nemerosa.ontrack.jenkins.OntrackConfiguration;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.net.MalformedURLException;
import java.net.URL;

public final class OntrackHttpClientFactory {

    public static CloseableHttpClient createHttpClient() throws MalformedURLException {
        OntrackConfiguration configuration = OntrackConfiguration.getOntrackConfiguration();

        URL url = new URL(configuration.getOntrackUrl());
        HttpHost host = new HttpHost(
                url.getHost(),
                url.getPort(),
                url.getProtocol()
        );

        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                new AuthScope(host),
                new UsernamePasswordCredentials(configuration.getOntrackUser(), configuration.getOntrackPassword())
        );

        return HttpClients.custom()
                .setDefaultCredentialsProvider(credentialsProvider)
                .build();
    }

    private OntrackHttpClientFactory() {
    }

}
