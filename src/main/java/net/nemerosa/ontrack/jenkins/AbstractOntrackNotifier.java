package net.nemerosa.ontrack.jenkins;

import com.fasterxml.jackson.databind.node.ObjectNode;
import hudson.model.AbstractBuild;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import net.nemerosa.ontrack.jenkins.support.json.ObjectBuilder;

import static net.nemerosa.ontrack.jenkins.support.json.JsonUtils.object;

public abstract class AbstractOntrackNotifier extends Notifier {

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    public ObjectNode getBuildPropertyData(AbstractBuild<?, ?> theBuild, OntrackConfiguration configuration) {
        return object()
                .with("propertyTypeName", "net.nemerosa.ontrack.extension.jenkins.JenkinsBuildPropertyType")
                .with("propertyData", object()
                        .with("configuration", configuration.getOntrackConfigurationName())
                        .with("job", theBuild.getProject().getName())
                        .with("build", theBuild.getNumber())
                        .end())
                .end();
    }

}
