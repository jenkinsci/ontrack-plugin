package net.nemerosa.ontrack.jenkins;

import com.fasterxml.jackson.databind.JsonNode;
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
import static net.nemerosa.ontrack.jenkins.support.client.OntrackClient.forBuild;
import static net.nemerosa.ontrack.jenkins.support.client.OntrackClient.forValidationStamp;
import static net.nemerosa.ontrack.jenkins.support.json.JsonUtils.array;
import static net.nemerosa.ontrack.jenkins.support.json.JsonUtils.object;

/**
 * Allows to create a run for a validation stamp on a build.
 * <p/>
 * The plug-in must get both the build and the validation stamp.
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
        String projectName = expand(project, theBuild, listener);
        String branchName = expand(branch, theBuild, listener);
        String buildName = expand(build, theBuild, listener);
        String validationStampName = expand(validationStamp, theBuild, listener);
        // Run status
        String runStatus = getRunStatus(theBuild);
        // Run description
        String runDescription = String.format("Build %s", theBuild);
        // General configuration
        OntrackConfiguration configuration = OntrackConfiguration.getOntrackConfiguration();
        listener.getLogger().format("[ontrack] Running %s with status %s for build %s of branch %s of project %s%n",
                validationStampName,
                runStatus,
                buildName,
                branchName,
                projectName
        );
        // Gets the validation stamp id
        int validationStampId = forValidationStamp(listener.getLogger(), projectName, branchName, validationStampName).getId();
        // Validation run request
        JsonNode validationRunRequest = object()
                .with("validationStamp", validationStampId)
                .with("validationRunStatusId", runStatus)
                .with("description", runDescription)
                .with("properties", array()
                        .with(getBuildPropertyData(theBuild, configuration))
                        .end())
                .end();
        // OK
        forBuild(listener.getLogger(), projectName, branchName, buildName).on("_validate").post(
                validationRunRequest
        );

        return true;
    }

    private String getRunStatus(AbstractBuild<?, ?> theBuild) {
        Result result = theBuild.getResult();
        if (result.isBetterOrEqualTo(Result.SUCCESS)) {
            return "PASSED";
        } else if (result.isBetterOrEqualTo(Result.UNSTABLE)) {
            return "WARNING";
        } else if (result.equals(Result.ABORTED)) {
            return "INTERRUPTED";
        } else {
            return "FAILED";
        }
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
            return "Ontrack2: Validation run creation";
        }
    }
}
