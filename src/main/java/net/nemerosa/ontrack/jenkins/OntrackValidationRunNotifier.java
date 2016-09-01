package net.nemerosa.ontrack.jenkins;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import net.nemerosa.ontrack.dsl.Build;
import net.nemerosa.ontrack.dsl.Ontrack;
import net.nemerosa.ontrack.dsl.http.OTMessageClientException;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLConnector;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

import static net.nemerosa.ontrack.jenkins.OntrackPluginSupport.expand;

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
        // Gets the Ontrack connector
        Ontrack ontrack = OntrackDSLConnector.createOntrackConnector(listener);
        try {
            // Gets the build
            Build build = ontrack.build(projectName, branchName, buildName);
            // Validation
            listener.getLogger().format("[ontrack] Running %s with status %s for build %s of branch %s of project %s%n",
                    validationStampName,
                    runStatus,
                    buildName,
                    branchName,
                    projectName
            );
            build.validate(validationStampName, runStatus);
        } catch (OTMessageClientException ex) {
            listener.getLogger().format("[ontrack] ERROR %s%n", ex.getMessage());
            theBuild.setResult(Result.FAILURE);
        }
        // OK
        return true;
    }

    private String getRunStatus(AbstractBuild<?, ?> theBuild) {
        Result result = theBuild.getResult();
        if (result != null) {
            if (result.isBetterOrEqualTo(Result.SUCCESS)) {
                return "PASSED";
            } else if (result.isBetterOrEqualTo(Result.UNSTABLE)) {
                return "WARNING";
            } else if (result.equals(Result.ABORTED)) {
                return "INTERRUPTED";
            } else {
                return "FAILED";
            }
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
            return "Ontrack: Validation run creation";
        }
    }
}
