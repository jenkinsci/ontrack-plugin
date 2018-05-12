package net.nemerosa.ontrack.jenkins;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import hudson.triggers.SCMTrigger;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    /**
     * Option to ignore failures
     */
    private final boolean ignoreFailure;

    /**
     * Option to send the run info for this build.
     */
    private final boolean runInfo;

    @DataBoundConstructor
    public OntrackBuildNotifier(String project, String branch, String build, boolean ignoreFailure, boolean runInfo) {
        this.project = project;
        this.branch = branch;
        this.build = build;
        this.ignoreFailure = ignoreFailure;
        this.runInfo = runInfo;
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

    public boolean isIgnoreFailure() {
        return ignoreFailure;
    }

    public boolean isRunInfo() {
        return runInfo;
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
                // Run info
                if (runInfo) {
                    // TODO Checks the version of Ontrack
                    // Gets the URL of this build
                    String url = Jenkins.getInstance().getRootUrl() + theBuild.getUrl();
                    // Gets the cause of this build
                    String triggerType = null;
                    String triggerData = null;
                    List<Cause> causes = theBuild.getCauses();
                    if (!causes.isEmpty()) {
                        Cause cause = causes.get(0);
                        if (cause instanceof SCMTrigger.SCMTriggerCause) {
                            triggerType = "scm";
                            // TODO Finds the associated commit
                            triggerData = cause.getShortDescription();
                        } else if (cause instanceof Cause.UserIdCause) {
                            triggerType = "user";
                            triggerData = ((Cause.UserIdCause) cause).getUserId();
                        }
                    }
                    // Gets the duration of this build
                    long durationMs = theBuild.getDuration();
                    long durationSeconds;
                    if (durationMs > 0) {
                        durationSeconds = durationMs / 1000;
                    } else {
                        durationSeconds = (System.currentTimeMillis() - theBuild.getStartTimeInMillis()) / 1000;
                    }
                    // Creates the run info
                    Map<String, Object> runInfo = new HashMap<>();
                    runInfo.put("sourceType", "jenkins");
                    runInfo.put("sourceUri", url);
                    if (triggerType != null && triggerData != null) {
                        runInfo.put("triggerType", triggerType);
                        runInfo.put("triggerData", triggerData);
                    }
                    if (durationSeconds > 0) {
                        runInfo.put("runTime", durationSeconds);
                    }
                    build.setRunInfo(runInfo);
                }
            } catch (OTMessageClientException ex) {
                listener.getLogger().format("[ontrack] ERROR %s%n", ex.getMessage());
                if (!ignoreFailure) {
                    theBuild.setResult(Result.FAILURE);
                }
            }
        } else {
            listener.getLogger().format("[ontrack] No creation of build since it is broken");
        }
        // OK
        return true;
    }

    /**
     * Gets the slash (/) separated path to the build.
     *
     * @param theBuild Build to get the path for
     * @return Relative URL path to the build
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
