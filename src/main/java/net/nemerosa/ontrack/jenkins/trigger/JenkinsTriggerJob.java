package net.nemerosa.ontrack.jenkins.trigger;

import hudson.model.*;
import jenkins.model.ParameterizedJobMixIn;

import javax.annotation.CheckForNull;
import java.util.List;

/**
 * Implementation for Jenkins.
 */
public class JenkinsTriggerJob implements TriggerJob {

    private final Job job;

    public JenkinsTriggerJob(Job job) {
        this.job = job;
    }

    @Override
    public String getFullName() {
        return job.getFullName();
    }

    @CheckForNull
    @Override
    public TriggerRun getLastBuild() {
        Run lastBuild = job.getLastBuild();
        if (lastBuild != null) {
            return new JenkinsTriggerRun(lastBuild);
        } else {
            return null;
        }
    }

    @Override
    public void trigger(OntrackTriggerCause cause, List<ParameterValue> parameters) {
        ParameterizedJobMixIn.scheduleBuild2(
                job,
                0,
                new CauseAction(cause),
                new ParametersAction(parameters)
        );
    }
}
