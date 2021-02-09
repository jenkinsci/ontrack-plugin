package net.nemerosa.ontrack.jenkins.trigger;

import hudson.model.ParameterValue;

import javax.annotation.CheckForNull;
import java.util.List;

/**
 * Defines the interaction of the {@link TriggerHelper} with a job, allows for mocking.
 */
public interface TriggerJob {

    /**
     * Full name of the job
     *
     * @return Full name of the job
     */
    String getFullName();

    /**
     * Gets the last build for this job
     *
     * @return Last build for this job, null if no run.
     */
    @CheckForNull
    TriggerRun getLastBuild();

    /**
     * Triggers the job with a cause and a list of parameters.
     *
     * @param cause      Cause to use
     * @param parameters Parameters for the job
     */
    void trigger(OntrackTriggerCause cause, List<ParameterValue> parameters);

}
