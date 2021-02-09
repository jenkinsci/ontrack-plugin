package net.nemerosa.ontrack.jenkins.dsl;

import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import net.nemerosa.ontrack.jenkins.OntrackPluginSupport;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class JenkinsConnector {

    private final Map<String, String> env = new LinkedHashMap<>();
    private final Run build;
    private final TaskListener listener;
    private final Consumer<Map<String, Object>> runInfoAdapter;

    public JenkinsConnector(Run build, TaskListener listener, Consumer<Map<String, Object>> runInfoAdapter) {
        this.build = build;
        this.listener = listener;
        this.runInfoAdapter = runInfoAdapter;
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
     *
     * @return Run info
     * @throws IOException In case of I/O error
     * @throws InterruptedException If the collection is interrupted
     */
    public Map<String, ?> getRunInfo() throws IOException, InterruptedException {
        Map<String, Object> runInfo = OntrackPluginSupport.getRunInfo(build, listener);
        runInfoAdapter.accept(runInfo);
        return runInfo;
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
    public Run getBuild() {
        return build;
    }

    /**
     * Gets access to the listener
     *
     * @return Build listener
     */
    public TaskListener getListener() {
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
