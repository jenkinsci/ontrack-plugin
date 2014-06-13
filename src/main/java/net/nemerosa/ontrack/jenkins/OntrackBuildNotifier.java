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

/**
 * Creation of a build for a branch. The created build will be associated with the Jenkins Build as a property.
 */
public class OntrackBuildNotifier extends AbstractOntrackNotifier {

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
    public OntrackBuildNotifier(String project, String branch, String build) {
        this.project = project;
        this.branch = branch;
        this.build = build;
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
            String projectName = expand(project, theBuild, listener);
            String branchName = expand(branch, theBuild, listener);
            String buildName = expand(build, theBuild, listener);
            // Build description
            String buildDescription = String.format("Build %s", theBuild);
            // Logging of parameters
            listener.getLogger().format("[ontrack] Creating build %s on project %s for branch %s%n", buildName, projectName, branchName);
            // FIXME Calling ontrack UI
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
