package net.nemerosa.ontrack.jenkins.steps;

import com.google.common.collect.ImmutableSet;
import hudson.AbortException;
import hudson.Extension;
import hudson.model.Result;
import hudson.model.TaskListener;
import net.nemerosa.ontrack.dsl.Build;
import net.nemerosa.ontrack.dsl.Ontrack;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLConnector;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import java.util.Set;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * Steps to validate a build
 */
@SuppressWarnings("unused")
public class OntrackValidateStep extends Step {

    /**
     * Name of the project
     */
    private final String project;
    /**
     * Name of the branch
     */
    private final String branch;
    /**
     * Name of the build to validate
     */
    private final String build;

    /**
     * Name of the validation stamp to apply
     */
    private final String validationStamp;

    /**
     * Validation status, defaults to PASSED
     */
    private String validationStatus = "PASSED";

    /**
     * Build result to translate into a validation status if defined
     */
    private Result buildResult;

    @DataBoundConstructor
    public OntrackValidateStep(@Nonnull String project, @Nonnull String branch, @Nonnull String build, @Nonnull String validationStamp) {
        this.project = project;
        this.branch = branch;
        this.build = build;
        this.validationStamp = validationStamp;
    }

    @DataBoundSetter
    public void setValidationStatus(String validationStatus) {
        this.validationStatus = validationStatus;
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

    public String getValidationStatus() {
        return validationStatus;
    }

    public Result getBuildResult() {
        return buildResult;
    }

    @DataBoundSetter
    public void setBuildResult(Result buildResult) {
        this.buildResult = buildResult;
    }

    @Override
    public StepExecution start(final StepContext context) throws Exception {
        // Checks
        if (isBlank(project) || isBlank(branch) || isBlank(build) || isBlank(validationStamp)) {
            throw new AbortException("Ontrack validation run not created. All mandatory properties must be supplied ('project', 'branch', 'build', 'validationStamp').");
        }
        // OK
        return new SynchronousStepExecution<Void>(context) {
            @Override
            protected Void run() throws Exception {
                // Gets the Ontrack connector
                Ontrack ontrack = OntrackDSLConnector.createOntrackConnector(context.get(TaskListener.class));
                // Gets the build...
                Build ontrackBuild = ontrack.build(project, branch, build);
                // Validation status from the build result if defined
                String actualStatus;
                if (buildResult != null) {
                    if (buildResult.equals(Result.SUCCESS)) {
                        actualStatus = "PASSED";
                    } else if (buildResult.equals(Result.UNSTABLE)) {
                        actualStatus = "UNSTABLE";
                    } else if (buildResult.equals(Result.FAILURE)) {
                        actualStatus = "FAILED";
                    } else if (buildResult.equals(Result.ABORTED)) {
                        actualStatus = "INTERRUPTED";
                    } else {
                        actualStatus = validationStatus;
                    }
                } else {
                    actualStatus = validationStatus;
                }
                // ... and creates a validation run
                ontrackBuild.validate(validationStamp, actualStatus);
                // Done
                return null;
            }
        };
    }

    @Extension
    public static class DescriptorImpl extends StepDescriptor {

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return ImmutableSet.of(TaskListener.class);
        }

        @Override
        public String getFunctionName() {
            return "ontrackValidate";
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return "Validates an Ontrack build";
        }
    }

}
