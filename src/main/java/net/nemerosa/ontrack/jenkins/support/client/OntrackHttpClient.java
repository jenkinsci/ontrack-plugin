package net.nemerosa.ontrack.jenkins.support.client;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;

public class OntrackHttpClient {

    private final HttpHost host;
    private final CloseableHttpClient httpClient;
    private final HttpClientContext httpContext;

    public OntrackHttpClient(HttpHost host, CloseableHttpClient httpClient, HttpClientContext httpContext) {
        this.host = host;
        this.httpClient = httpClient;
        this.httpContext = httpContext;
    }

    public HttpResponse execute(HttpRequestBase request) throws IOException {
        return httpClient.execute(host, request, httpContext);
    }

    public void close() throws IOException {
        httpClient.close();
    }
}
