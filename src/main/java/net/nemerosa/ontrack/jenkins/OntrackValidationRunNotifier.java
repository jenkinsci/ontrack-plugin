package net.nemerosa.ontrack.jenkins;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

import static net.nemerosa.ontrack.jenkins.OntrackPluginSupport.expand;
import static net.nemerosa.ontrack.jenkins.support.client.OntrackClient.forBranch;
import static net.nemerosa.ontrack.jenkins.support.json.JsonUtils.array;
import static net.nemerosa.ontrack.jenkins.support.json.JsonUtils.object;

/**
 * Allows to notify for a build.
 */
public class OntrackValidationRunNotifier extends AbstractOntrackNotifier {

    private final String project;
    private final String branch;
    private final String build;
    private final String validationStamp;

    @DataBoundConstructor
    public OntrackValidationRunNotifier(String project, String branch, String build, String validationStamp) {
        this.project = project;
        this.branch = branch;
        this.build = build;
        this.validationStamp = validationStamp;
    }

    public String getProject() {
        return project;
    }

    public String getBranch() {
        return branch;
    }

    public String getBuild() {
        return build;
    }

    public String getValidationStamp() {
        return validationStamp;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> theBuild, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        // Expands the expressions into actual values
        final String projectName = expand(project, theBuild, listener);
        final String branchName = expand(branch, theBuild, listener);
        final String buildName = expand(build, theBuild, listener);
        final String validationStampName = expand(validationStamp, theBuild, listener);
        // General configuration
        OntrackConfiguration configuration = OntrackConfiguration.getOntrackConfiguration();
        Result runStatus = theBuild.getResult();
        // TODO Run description
        String runDescription = String.format("Run %s", theBuild);
        listener.getLogger().format("[ontrack] Running %s with status %s for build %s of branch %s of project %s%n", validationStampName, runStatus, buildName, branchName, projectName);
        // OK
        forBranch(listener.getLogger(), projectName, branchName).on("_createValidationStamp").post(
                object()
                        .with("name", validationStampName)
                        .with("description", runDescription)
                        .with("properties", array()
                                .with(object()
                                        .with("propertyTypeName", "net.nemerosa.ontrack.extension.jenkins.JenkinsBuildPropertyType")
                                        .with("propertyData", object()
                                                .with("configuration", configuration.getOntrackConfigurationName())
                                                .with("job", theBuild.getProject().getName())
                                                .with("build", theBuild.getNumber())
                                                .end())
                                        .end())
                                .end())
                        .end()
        );

        return true;
    }

    @Extension
    public static final class OntrackValidationRunDescriptorImpl extends BuildStepDescriptor<Publisher> {

        public OntrackValidationRunDescriptorImpl() {
            super(OntrackValidationRunNotifier.class);
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Ontrack: Validation run creation";
        }
    }
}
