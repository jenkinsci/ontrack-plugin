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
 * Creation of a build for a branch. The created build will be associated with the Jenkins Build as a property.
 */
public class OntrackBuildNotifier extends AbstractOntrackNotifier {

    /**
     * Name of the Jenkins configuration in ontrack
     */
    private final String configurationName;
    /**
     * Name of the project to create the build for
     */
    private final String project;
    /**
     * Name of the branch to create the build for
     */
    private final String branch;
    /**
     * Name of the build to create
     */
    private final String build;

    @DataBoundConstructor
    public OntrackBuildNotifier(String configurationName, String project, String branch, String build) {
        this.configurationName = configurationName;
        this.project = project;
        this.branch = branch;
        this.build = build;
    }

    public String getConfigurationName() {
        return configurationName;
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

    @Override
    public boolean perform(AbstractBuild<?, ?> theBuild, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        // Only triggers in case of success
        if (theBuild.getResult().isBetterOrEqualTo(Result.SUCCESS)) {
            // Expands the expressions into actual values
            String configuration = expand(configurationName, theBuild, listener);
            String projectName = expand(project, theBuild, listener);
            String branchName = expand(branch, theBuild, listener);
            String buildName = expand(build, theBuild, listener);
            // Build description
            String buildDescription = String.format("Build %s", theBuild);
            // Logging of parameters
            listener.getLogger().format("[ontrack] Creating build %s on project %s for branch %s in configuration %s%n",
                    buildName,
                    projectName,
                    branchName,
                    configuration);

            // Calling ontrack UI
            forBranch(listener.getLogger(), projectName, branchName).on("_createBuild").post(
                    object()
                            .with("name", buildName)
                            .with("description", buildDescription)
                            .with("properties", array()
                                    .with(object()
                                            .with("propertyTypeName", "net.nemerosa.ontrack.extension.jenkins.JenkinsBuildProperty")
                                            .with("propertyData", object()
                                                    .with("configuration", configuration)
                                                    .with("job", theBuild.getProject().getName())
                                                    .with("build", theBuild.getNumber())
                                                    .end())
                                            .end())
                                    .end())
                            .end()
            );

        } else {
            listener.getLogger().format("[ontrack] No creation of build since it is broken");
        }
        // OK
        return true;
    }

    @Extension
    public static final class OntrackBuildDescriptorImpl extends BuildStepDescriptor<Publisher> {

        public OntrackBuildDescriptorImpl() {
            super(OntrackBuildNotifier.class);
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Ontrack: Build creation";
        }
    }
}
