package net.nemerosa.ontrack.jenkins;

import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;

public abstract class AbstractOntrackNotifier extends Notifier {

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    // TODO protected <T> T call(ControlClientCall<T> controlClientCall) {
//        return OntrackClient.control(controlClientCall);
//    }

}
