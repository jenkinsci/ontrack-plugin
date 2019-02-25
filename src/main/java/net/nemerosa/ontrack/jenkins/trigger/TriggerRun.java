package net.nemerosa.ontrack.jenkins.trigger;

import hudson.model.Result;

import javax.annotation.CheckForNull;
import java.io.IOException;

/**
 * Defines the interaction between {@link TriggerHelper} and a Jenkins run. Allows for mocking.
 */
public interface TriggerRun {

    /**
     * Result of the build
     */
    @CheckForNull
    Result getResult();

    /**
     * Gets an environment variable
     */
    @CheckForNull
    String getEnvironment(String name) throws IOException, InterruptedException;

}
