package net.nemerosa.ontrack.jenkins.dsl;

import hudson.model.BuildListener;
import net.nemerosa.ontrack.dsl.Ontrack;
import net.nemerosa.ontrack.dsl.OntrackConnection;
import net.nemerosa.ontrack.dsl.OntrackLogger;
import net.nemerosa.ontrack.jenkins.OntrackConfiguration;
import org.apache.commons.lang.StringUtils;

import java.io.PrintStream;

public class OntrackDSLConnector {

    public static Ontrack createOntrackConnector(final PrintStream logger) {
        return createOntrackConnector(new OntrackLogger() {
            @Override
            public void trace(String message) {
                logger.println(message);
            }
        });
    }

    public static Ontrack createOntrackConnector(final OntrackLogger logger) {
        OntrackConfiguration config = OntrackConfiguration.getOntrackConfiguration();
        if (config == null) {
            throw new IllegalStateException("Could not find any Ontrack configuration.");
        }
        OntrackConnection connection = OntrackConnection.create(config.getOntrackUrl());
        // Logging
        if (logger != null) {
            connection = connection.logger(logger);
        }
        // Authentication
        String user = config.getOntrackUser();
        if (StringUtils.isNotBlank(user)) {
            connection = connection.authenticate(
                    user,
                    config.getOntrackPassword()
            );
        }
        // Retries
        if (config.getOntrackMaxTries() > 1) {
            connection = connection
                    .maxTries(config.getOntrackMaxTries())
                    .retryDelaySeconds(config.getOntrackRetryDelaySeconds());
        }
        // Building the Ontrack root
        return connection.build();
    }

    public static Ontrack createOntrackConnector(final BuildListener listener) {
        return createOntrackConnector(listener != null ? listener.getLogger() : null);
    }

}
