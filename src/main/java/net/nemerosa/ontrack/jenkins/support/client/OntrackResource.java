package net.nemerosa.ontrack.jenkins.support.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringUtils;

import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;

public class OntrackResource {

    private final PrintStream logger;
    private final JsonNode resource;

    public OntrackResource(PrintStream logger, JsonNode resource) {
        this.logger = logger;
        this.resource = resource;
    }

    public OntrackConnector on(String link) throws MalformedURLException {
        String href = resource.path(link).asText();
        if (StringUtils.isBlank(href)) {
            throw new RuntimeException(String.format("Could not find link %s", link));
        } else {
            logger.format("[ontrack] Following link `%s` at `%s`%n", link, href);
            return new OntrackConnector(new URL(href));
        }
    }

}
