package net.nemerosa.ontrack.jenkins;

import hudson.model.AbstractBuild;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;

import java.io.IOException;
import java.util.Map;

public abstract class AbstractOntrackNotifier extends Notifier {

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    protected Map<String, Object> getRunInfo(AbstractBuild theBuild, TaskListener taskListener) throws IOException, InterruptedException {
        return OntrackPluginSupport.getRunInfo(theBuild, taskListener);
    }

}
