package net.nemerosa.ontrack.jenkins.dsl;

import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Result;

import java.util.LinkedHashMap;
import java.util.Map;

public class JenkinsConnector {

    private final Map<String, String> env = new LinkedHashMap<String, String>();
    private final AbstractBuild build;
    private final BuildListener listener;

    public JenkinsConnector(AbstractBuild build, BuildListener listener) {
        this.build = build;
        this.listener = listener;
    }

    public void env(String name, String value) {
        env.put(name, value);
    }

    /**
     * Gets the current result of the build.
     */
    public Result getResult() {
        return build.getResult();
    }

    /**
     * Gets access to the build
     */
    public AbstractBuild getBuild() {
        return build;
    }

    /**
     * Gets access to the listener
     */
    public BuildListener getListener() {
        return listener;
    }

    /**
     * Is the build a success?
     */
    public boolean isSuccess() {
        Result result = getResult();
        return result != null && result.isBetterOrEqualTo(Result.SUCCESS);
    }

    /**
     * Is the build unstable?
     */
    public boolean isUnstable() {
        Result result = getResult();
        return result != null && result.isBetterOrEqualTo(Result.UNSTABLE) && result.isWorseThan(Result.SUCCESS);
    }

    /**
     * Is the build a failure?
     */
    public boolean isFailure() {
        Result result = getResult();
        return result != null && result.isWorseOrEqualTo(Result.FAILURE);
    }

    public Map<String, String> env() {
        return env;
    }

}
