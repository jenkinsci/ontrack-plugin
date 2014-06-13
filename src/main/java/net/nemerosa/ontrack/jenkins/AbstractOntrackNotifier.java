package net.nemerosa.ontrack.jenkins;

import hudson.model.AbstractBuild;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import jenkins.model.Jenkins;

public abstract class AbstractOntrackNotifier extends Notifier {

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    protected String getBuildUrl(AbstractBuild<?, ?> theBuild) {
        return Jenkins.getInstance().getRootUrl() + theBuild.getUrl();
    }

    // TODO protected <T> T call(ControlClientCall<T> controlClientCall) {
//        return OntrackClient.control(controlClientCall);
//    }

}
