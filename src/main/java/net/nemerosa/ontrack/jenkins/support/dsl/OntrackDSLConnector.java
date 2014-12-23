package net.nemerosa.ontrack.jenkins.support.dsl;

import hudson.model.BuildListener;
import net.nemerosa.ontrack.client.OTHttpClientLogger;
import net.nemerosa.ontrack.dsl.Ontrack;
import net.nemerosa.ontrack.dsl.OntrackConnection;
import net.nemerosa.ontrack.jenkins.OntrackConfiguration;
import org.apache.commons.lang.StringUtils;

public class OntrackDSLConnector {

    public static Ontrack createOntrackConnector(final BuildListener listener) {
        OntrackConfiguration config = OntrackConfiguration.getOntrackConfiguration();
        OntrackConnection connection = OntrackConnection.create(config.getOntrackUrl());
        // Logging
        if (listener != null) {
            connection = connection.logger(new OTHttpClientLogger() {
                public void trace(String message) {
                    listener.getLogger().println(message);
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
        // Building the Ontrack root
        return connection.build();
    }

}
