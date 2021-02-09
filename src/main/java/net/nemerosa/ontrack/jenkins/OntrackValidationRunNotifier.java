package net.nemerosa.ontrack.jenkins;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLConnector;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLFacade;
import net.nemerosa.ontrack.jenkins.dsl.facade.BuildFacade;
import net.nemerosa.ontrack.jenkins.dsl.facade.ValidationRunFacade;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.Map;

import static net.nemerosa.ontrack.jenkins.OntrackPluginSupport.expand;

/**
 * Allows to create a run for a validation stamp on a build.
 * <p>
 * The plug-in must get both the build and the validation stamp.
 */
public class OntrackValidationRunNotifier extends AbstractOntrackNotifier {

    private final String project;
    private final String branch;
    private final String build;
    private final String validationStamp;
    private final boolean ignoreFailure;
    /**
     * Option to send the run info for this build.
     */
    private final boolean runInfo;

    @DataBoundConstructor
    public OntrackValidationRunNotifier(String project, String branch, String build, String validationStamp, boolean ignoreFailure, boolean runInfo) {
        this.project = project;
        this.branch = branch;
        this.build = build;
        this.validationStamp = validationStamp;
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

    public String getValidationStamp() {
        return validationStamp;
    }

    public boolean isIgnoreFailure() {
        return ignoreFailure;
    }

    public boolean isRunInfo() {
        return runInfo;
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
        OntrackDSLFacade ontrack = OntrackDSLConnector.createOntrackConnector(listener);
        try {
            // Gets the build
            BuildFacade build = ontrack.build(projectName, branchName, buildName);
            // Validation
            listener.getLogger().format("[ontrack] Running %s with status %s for build %s of branch %s of project %s%n",
                    validationStampName,
                    runStatus,
                    buildName,
                    branchName,
                    projectName
            );
            ValidationRunFacade validationRun = build.validate(validationStampName, runStatus);
            // Run info
            if (runInfo) {
                Map<String, Object> runInfo = getRunInfo(theBuild, listener);
                if (runInfo != null) {
                    validationRun.setRunInfo(runInfo);
                }
            }
        } catch (Exception ex) {
            ontrack.onClientException(ex, (message) -> {
                listener.getLogger().format("[ontrack] ERROR %s%n", message);
                if (!ignoreFailure) {
                    theBuild.setResult(Result.FAILURE);
                }
            });
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
