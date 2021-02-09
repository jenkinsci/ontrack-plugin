package net.nemerosa.ontrack.jenkins.dsl;

import hudson.model.TaskListener;
import net.nemerosa.ontrack.jenkins.OntrackConfiguration;
import net.nemerosa.ontrack.jenkins.dsl.v3.OntrackDSLV3Facade;
import net.nemerosa.ontrack.jenkins.dsl.v4.OntrackDSLV4Facade;
import org.apache.commons.lang.StringUtils;

import java.io.PrintStream;

public class OntrackDSLConnector {

    public static OntrackDSLFacade createOntrackConnector(final PrintStream logger) {
        return createOntrackConnector((OntrackDSLLogger) logger::println);
    }

    public static OntrackDSLFacade createOntrackConnector(final OntrackDSLLogger logger) {
        OntrackConfiguration config = OntrackConfiguration.getOntrackConfiguration();
        if (config == null) {
            throw new IllegalStateException("Could not find any Ontrack configuration.");
        }
        // Versions
        String version = config.getOntrackVersion();
        if (OntrackConfiguration.VERSION_3.equals(version) || StringUtils.isBlank(version)) {
            return new OntrackDSLV3Facade(config, logger);
        } else if (OntrackConfiguration.VERSION_4.equals(version)) {
            return new OntrackDSLV4Facade(config, logger);
        } else {
            throw new IllegalStateException("Not supporting Ontrack API version " + version);
        }
    }

    public static OntrackDSLFacade createOntrackConnector(final TaskListener listener) {
        return ontrackDSLFacade != null ? ontrackDSLFacade : createOntrackConnector(listener != null ? listener.getLogger() : System.out);
    }

    private static OntrackDSLFacade ontrackDSLFacade = null;

    /**
     * Used for test only - injection of a test instance to connect to Ontrack
     *
     * @param test Test instance to use
     */
    public static void setOntrack(OntrackDSLFacade test) {
        ontrackDSLFacade = test;
    }

}
