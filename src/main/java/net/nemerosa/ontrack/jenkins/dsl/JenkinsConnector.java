package net.nemerosa.ontrack.jenkins.dsl;

import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Result;
import net.nemerosa.ontrack.jenkins.OntrackPluginSupport;

import java.io.IOException;
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
     * Gets the current run info, associated to the build or the current stage
     * for a pipeline.
     * <p>
     * The returned map can be directly as a run info parameter for a build
     * or a validation run.
     */
    public Map<String, ?> getRunInfo() throws IOException, InterruptedException {
        return OntrackPluginSupport.getRunInfo(build, listener);
    }

    /**
     * Gets the current result of the build.
     *
     * @return Result of the result (can be null)
     */
    public Result getResult() {
        return build.getResult();
    }

    /**
     * Gets access to the build
     *
     * @return Associated build
     */
    public AbstractBuild getBuild() {
        return build;
    }

    /**
     * Gets access to the listener
     *
     * @return Build listener
     */
    public BuildListener getListener() {
        return listener;
    }

    /**
     * Is the build a success?
     *
     * @return true if the build is a success
     */
    public boolean isSuccess() {
        Result result = getResult();
        return result != null && result.isBetterOrEqualTo(Result.SUCCESS);
    }

    /**
     * Is the build unstable?
     *
     * @return true if the build is unstable
     */
    public boolean isUnstable() {
        Result result = getResult();
        return result != null && result.isBetterOrEqualTo(Result.UNSTABLE) && result.isWorseThan(Result.SUCCESS);
    }

    /**
     * Is the build a failure?
     *
     * @return true if the build is failed
     */
    public boolean isFailure() {
        Result result = getResult();
        return result != null && result.isWorseOrEqualTo(Result.FAILURE);
    }

    public Map<String, String> env() {
        return env;
    }

}
