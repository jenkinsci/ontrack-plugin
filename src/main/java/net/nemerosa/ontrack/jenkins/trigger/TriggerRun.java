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
     *
     * @return Result of the build or null if not available
     */
    @CheckForNull
    Result getResult();

    /**
     * Gets an environment variable
     *
     * @param name Name of the environment variable
     * @return Value of the environment variable or null if not available
     * @throws IOException          If an I/O exception occurs
     * @throws InterruptedException If the call is interrupted
     */
    @CheckForNull
    String getEnvironment(String name) throws IOException, InterruptedException;

}
