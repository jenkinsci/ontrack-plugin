package net.nemerosa.ontrack.jenkins.support.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;

public class OntrackResource {

    private final JsonNode resource;

    public OntrackResource(JsonNode resource) {
        this.resource = resource;
    }

    public OntrackConnector on(String link) throws MalformedURLException {
        String href = resource.path(link).asText();
        if (StringUtils.isBlank(href)) {
            throw new RuntimeException(String.format("Could not find link %s", link));
        } else {
            return new OntrackConnector(new URL(href));
        }
    }

}
