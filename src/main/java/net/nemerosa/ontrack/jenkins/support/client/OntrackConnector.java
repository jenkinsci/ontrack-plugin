package net.nemerosa.ontrack.jenkins.support.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Map;

public class OntrackConnector {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final URL url;
    private final Map<String, Object> parameters;

    public OntrackConnector(URL url) {
        this(url, Collections.<String, Object>emptyMap());
    }

    public OntrackConnector(URL url, Map<String, Object> parameters) {
        this.url = url;
        this.parameters = parameters;
    }

    public JsonNode get() throws IOException {
        return execute(new HttpGet(getUri()));
    }

    public JsonNode post(JsonNode data) throws IOException {
        HttpPost post = new HttpPost(getUri());
        String json = new ObjectMapper().writeValueAsString(data);
        post.setEntity(new StringEntity(json, ContentType.create("application/json", "UTF-8")));
        return execute(post);
    }

    private String getUri() throws UnsupportedEncodingException {
        StringBuilder uri = new StringBuilder(url.toString());
        boolean start = true;
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            if (start) {
                start = false;
                uri.append("?");
            } else {
                uri.append("&");
            }
            uri.append(entry.getKey());
            uri.append("=");
            uri.append(URLEncoder.encode(String.valueOf(entry.getValue()), "UTF-8"));
        }
        return uri.toString();
    }

    public JsonNode put(JsonNode data) throws IOException {
        HttpPut put = new HttpPut(getUri());
        String json = objectMapper.writeValueAsString(data);
        put.setEntity(new StringEntity(json, ContentType.create("application/json", "UTF-8")));
        return execute(put);
    }

    private JsonNode execute(HttpRequestBase request) throws IOException {
        OntrackHttpClient client = OntrackHttpClientFactory.createHttpClient();
        try {
            HttpResponse response = client.execute(request);
            // Parses the response
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                return parse(response);
            } else {
                // Generic error
                throw new RuntimeException(
                        String.format(
                                "%d HTTP error received when calling %s%n%s",
                                statusCode,
                                request,
                                response.getStatusLine().getReasonPhrase()
                        )
                );
            }
        } finally {
            client.close();
        }
    }

    private JsonNode parse(HttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        String content = entity != null ? EntityUtils.toString(entity, "UTF-8") : null;
        if (content != null) {
            return objectMapper.readTree(content);
        } else {
            return null;
        }
    }

}
