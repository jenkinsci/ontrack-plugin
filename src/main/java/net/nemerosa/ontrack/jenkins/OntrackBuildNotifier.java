package net.nemerosa.ontrack.jenkins;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import jenkins.model.Jenkins;
import net.nemerosa.ontrack.dsl.Branch;
import net.nemerosa.ontrack.dsl.Build;
import net.nemerosa.ontrack.dsl.Ontrack;
import net.nemerosa.ontrack.dsl.http.OTMessageClientException;
import net.nemerosa.ontrack.dsl.properties.BuildProperties;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLConnector;
import org.apache.commons.lang.StringUtils;
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
        Result result = theBuild.getResult();
        if (result != null && result.isBetterOrEqualTo(Result.SUCCESS)) {
            // Expands the expressions into actual values
            String projectName = expand(project, theBuild, listener);
            String branchName = expand(branch, theBuild, listener);
            String buildName = expand(build, theBuild, listener);
            // Build description
            String buildDescription = String.format("Build %s", theBuild);
            // Gets the Ontrack connector
            Ontrack ontrack = OntrackDSLConnector.createOntrackConnector(listener);
            try {
                // Gets the branch...
                Branch branch = ontrack.branch(projectName, branchName);
                // ... and creates a build
                Build build = branch.build(buildName, buildDescription, true);
                // Sets the Jenkins build property
                // Note: cannot use the Groovy DSL here, using internal classes
                new BuildProperties(ontrack, build).jenkinsBuild(
                        OntrackConfiguration.getOntrackConfiguration().getOntrackConfigurationName(),
                        getProjectPath(theBuild),
                        theBuild.getNumber()
                );
            } catch (OTMessageClientException ex) {
                listener.getLogger().format("[ontrack] ERROR %s%n", ex.getMessage());
                theBuild.setResult(Result.FAILURE);
            }
        } else {
            listener.getLogger().format("[ontrack] No creation of build since it is broken");
        }
        // OK
        return true;
    }

    /**
     * Gets the slash (/) separated path to the build.
     */
    protected String getProjectPath(AbstractBuild<?, ?> theBuild) {
        return StringUtils.replace(
                theBuild.getProject().getRelativeNameFrom(Jenkins.getInstance()),
                "/",
                "/job/"
        );
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
