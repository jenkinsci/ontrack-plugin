package net.nemerosa.ontrack.jenkins.support.dsl;

import hudson.model.Result;

import java.util.LinkedHashMap;
import java.util.Map;

public class JenkinsConnector {

    private final Map<String, String> env = new LinkedHashMap<String, String>();
    private final Result result;

    public JenkinsConnector(Result result) {
        this.result = result;
    }

    public void env(String name, String value) {
        env.put(name, value);
    }

    /**
     * Gets the current result of the build.
     */
    public Result getResult() {
        return result;
    }

    /**
     * Is the build a success?
     */
    public boolean isSuccess() {
        return result != null && result.isBetterOrEqualTo(Result.SUCCESS);
    }

    /**
     * Is the build unstable?
     */
    public boolean isUnstable() {
        return result != null && result.isBetterOrEqualTo(Result.UNSTABLE) && result.isWorseThan(Result.SUCCESS);
    }

    /**
     * Is the build a failure?
     */
    public boolean isFailure() {
        return result != null && result.isWorseOrEqualTo(Result.FAILURE);
    }

    public Map<String, String> env() {
        return env;
    }

}
