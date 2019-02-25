package net.nemerosa.ontrack.jenkins.trigger;

import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.LogTaskListener;

import javax.annotation.CheckForNull;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of {@link TriggerRun} for Jenkins.
 */
public class JenkinsTriggerRun implements TriggerRun {

    private static final Logger LOGGER = Logger.getLogger(TriggerRun.class.getName());

    private final Run run;

    private final TaskListener taskListener = new LogTaskListener(
            LOGGER,
            Level.FINER
    );

    public JenkinsTriggerRun(Run run) {
        this.run = run;
    }

    @CheckForNull
    @Override
    public Result getResult() {
        return run.getResult();
    }

    @CheckForNull
    @Override
    public String getEnvironment(String name) throws IOException, InterruptedException {
        return run.getEnvironment(taskListener).get(name, null);
    }
}
