package net.nemerosa.ontrack.jenkins;

import hudson.model.AbstractBuild;
import hudson.model.Cause;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.triggers.SCMTrigger;
import jenkins.model.Jenkins;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractOntrackNotifier extends Notifier {

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    protected Map<String, Object> getRunInfo(AbstractBuild theBuild) {
        // TODO Checks the version of Ontrack
        // Gets the URL of this build
        String url = Jenkins.getInstance().getRootUrl() + theBuild.getUrl();
        // Gets the cause of this build
        String triggerType = null;
        String triggerData = null;
        List<Cause> causes = theBuild.getCauses();
        if (!causes.isEmpty()) {
            Cause cause = causes.get(0);
            if (cause instanceof SCMTrigger.SCMTriggerCause) {
                triggerType = "scm";
                // TODO Finds the associated commit
                triggerData = cause.getShortDescription();
            } else if (cause instanceof Cause.UserIdCause) {
                triggerType = "user";
                triggerData = ((Cause.UserIdCause) cause).getUserId();
            }
        }
        // Gets the duration of this build
        long durationMs = theBuild.getDuration();
        long durationSeconds;
        if (durationMs > 0) {
            durationSeconds = durationMs / 1000;
        } else {
            durationSeconds = (System.currentTimeMillis() - theBuild.getStartTimeInMillis()) / 1000;
        }
        // Creates the run info
        Map<String, Object> runInfo = new HashMap<>();
        runInfo.put("sourceType", "jenkins");
        runInfo.put("sourceUri", url);
        if (triggerType != null && triggerData != null) {
            runInfo.put("triggerType", triggerType);
            runInfo.put("triggerData", triggerData);
        }
        if (durationSeconds > 0) {
            runInfo.put("runTime", durationSeconds);
        }
        // OK
        return runInfo;
    }

}
