package net.nemerosa.ontrack.jenkins.dsl;

import hudson.model.TaskListener;
import net.nemerosa.ontrack.dsl.Ontrack;
import net.nemerosa.ontrack.dsl.OntrackConnection;
import net.nemerosa.ontrack.dsl.OntrackLogger;
import net.nemerosa.ontrack.jenkins.OntrackConfiguration;
import org.apache.commons.lang.StringUtils;

import java.io.PrintStream;

public class OntrackDSLConnector {

    public static Ontrack createOntrackConnector(final PrintStream logger) {
        OntrackConfiguration config = OntrackConfiguration.getOntrackConfiguration();
        OntrackConnection connection = OntrackConnection.create(config.getOntrackUrl());
        // Logging
        if (logger != null) {
            connection = connection.logger(new OntrackLogger() {
                @Override
                public void trace(String message) {
                    logger.println(message);
                }
            });
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

    public static Ontrack createOntrackConnector(final TaskListener listener) {
        return ontrack != null ? ontrack : createOntrackConnector(listener != null ? listener.getLogger() : null);
    }

    private static Ontrack ontrack = null;

    /**
     * Used for test only - injection of a test instance to connect to Ontrack
     */
    public static void setOntrack(Ontrack test) {
        ontrack = test;
    }

}
