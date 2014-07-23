package net.nemerosa.ontrack.jenkins.support.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringUtils;

import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

public class OntrackResource {

    private final PrintStream logger;
    private final JsonNode resource;

    public OntrackResource(PrintStream logger, JsonNode resource) {
        this.logger = logger;
        this.resource = resource;
    }

    public OntrackConnector on(String link) throws MalformedURLException {
        return on(link, Collections.<String, Object>emptyMap());
    }

    public OntrackConnector on(String link, Map<String, Object> parameters) throws MalformedURLException {
        return on(link, null, parameters);
    }

    public OntrackConnector on(String link, String suffix, Map<String, Object> parameters) throws MalformedURLException {
        String href = resource.path(link).asText();
        if (StringUtils.isBlank(href)) {
            throw new RuntimeException(String.format("Could not find link %s", link));
        } else {
            if (StringUtils.isNotBlank(suffix)) {
                href += suffix;
            }
            logger.format("[ontrack] Following link `%s` at `%s`%n", link, href);
            return new OntrackConnector(new URL(href), parameters);
        }
    }

    public int getId() {
        return resource.path("id").asInt();
    }

}
