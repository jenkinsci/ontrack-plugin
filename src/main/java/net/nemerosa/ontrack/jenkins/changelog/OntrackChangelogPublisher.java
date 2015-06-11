package net.nemerosa.ontrack.jenkins.changelog;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import net.nemerosa.ontrack.dsl.Ontrack;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLConnector;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static net.nemerosa.ontrack.jenkins.OntrackPluginSupport.expand;

public class OntrackChangelogPublisher extends Notifier {

    /**
     * Name of the project to get the change log for
     */
    private final String project;
    /**
     * Name of the branch to get the change log for
     */
    private final String branch;
    /**
     * Name of the parameter which contains the Ontrack build name on a give Jenkins build
     */
    private final String buildNameParameter;

    @DataBoundConstructor
    public OntrackChangelogPublisher(String project, String branch, String buildNameParameter) {
        this.project = project;
        this.branch = branch;
        this.buildNameParameter = buildNameParameter;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        // Gets the project and branch name
        String projectName = expand(project, build, listener);
        String branchName = expand(branch, build, listener);

        // Gets the current build name
        String lastBuildName = getBuildName(build);

        // Gets the previous build name
        String previousBuildName = null;
        AbstractBuild<?, ?> previousBuild = build.getPreviousBuild();
        if (previousBuild != null) {
            previousBuildName = getBuildName(previousBuild);
        }

        // Checks the build boundaries
        if (StringUtils.isBlank(lastBuildName)) {
            return noChangeLog(listener, "No build name can be retrieved from the current build");
        } else if (previousBuild == null) {
            return noChangeLog(listener, "There is no previous build");
        } else if (StringUtils.isBlank(previousBuildName)) {
            return noChangeLog(listener, "No build name can be retrieved from the previous build");
        }

        // TODO Gets the build intervals

        // Gets the Ontrack connector
        Ontrack ontrack = OntrackDSLConnector.createOntrackConnector(listener);
        // TODO Collects the change logs
        // TODO Adds a change log action to register the change log
        // OK
        return true;
    }

    protected boolean noChangeLog(BuildListener listener, String reason) {
        listener.getLogger().format("No change log can be computed. %s%n", reason);
        return true;
    }

    protected String getBuildName(AbstractBuild<?, ?> build) {
        List<ParametersAction> parametersActions = build.getActions(ParametersAction.class);
        for (ParametersAction parametersAction : parametersActions) {
            ParameterValue parameterValue = parametersAction.getParameter(buildNameParameter);
            if (parameterValue != null) {
                return Objects.toString(parameterValue.getValue(), null);
            }
        }
        // Not found
        return null;
    }

    public String getProject() {
        return project;
    }

    public String getBranch() {
        return branch;
    }

    public String getBuildNameParameter() {
        return buildNameParameter;
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    @Extension
    public static final class OntrackChangelogPublisherDescriptor extends BuildStepDescriptor<Publisher> {

        public OntrackChangelogPublisherDescriptor() {
            super(OntrackChangelogPublisher.class);
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Ontrack: Change log publication";
        }
    }
}
