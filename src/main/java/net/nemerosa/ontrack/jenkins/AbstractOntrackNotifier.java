package net.nemerosa.ontrack.jenkins;

import hudson.model.AbstractBuild;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;

import java.util.Map;

public abstract class AbstractOntrackNotifier extends Notifier {

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    protected Map<String, Object> getRunInfo(AbstractBuild theBuild) {
        return OntrackPluginSupport.getRunInfo(theBuild);
    }

}
