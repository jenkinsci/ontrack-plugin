package net.nemerosa.ontrack.jenkins.steps;

import com.google.common.collect.ImmutableSet;
import hudson.AbortException;
import hudson.Extension;
import hudson.model.Result;
import hudson.model.TaskListener;
import net.nemerosa.ontrack.dsl.Build;
import net.nemerosa.ontrack.dsl.Ontrack;
import net.nemerosa.ontrack.dsl.ValidationRun;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLConnector;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import java.util.Map;
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
     * Validation status, defaults to null, for making it computed
     */
    private String validationStatus = null;

    /**
     * Build result to translate into a validation status if defined
     */
    private Result buildResult = null;

    /**
     * Fraction data
     */
    private Map<String, Integer> fraction = null;

    /**
     * Data validation
     */
    private boolean dataValidation = true;

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

    public Map<String, Integer> getFraction() {
        return fraction;
    }

    @DataBoundSetter
    public void setFraction(Map<String, Integer> fraction) {
        this.fraction = fraction;
    }

    public boolean isDataValidation() {
        return dataValidation;
    }

    @DataBoundSetter
    public void setDataValidation(boolean dataValidation) {
        this.dataValidation = dataValidation;
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
                // Gets the current listener
                TaskListener taskListener = context.get(TaskListener.class);
                if (taskListener == null) {
                    throw new IllegalStateException("Cannot get any task listener.");
                }
                // Gets the Ontrack connector
                Ontrack ontrack = OntrackDSLConnector.createOntrackConnector(taskListener);
                // Gets the build...
                Build ontrackBuild = ontrack.build(project, branch, build);
                // Validation status from the build result if defined
                String actualStatus = validationStatus;
                if (actualStatus == null && buildResult != null) {
                    actualStatus = OntrackStepHelper.toValidationRunStatus(buildResult);
                }
                // Computation from current stage if needed
                if (actualStatus == null) {
                    actualStatus = OntrackStepHelper.getValidationRunStatusFromStage(context);
                }
                // ... and creates a validation run
                ValidationRun validationRun;
                if (fraction != null) {
                    validationRun = ontrackBuild.validateWithFraction(
                        validationStamp,
                        get(fraction, "numerator"),
                        get(fraction, "denominator"),
                        dataValidation ? validationStatus : actualStatus
                    );
                } else {
                    validationRun = ontrackBuild.validate(validationStamp, actualStatus);
                }
                // Collecting run info
                Map<String, ?> runInfo = OntrackStepHelper.getRunInfo(context, taskListener);
                // If not empty, send the runtime
                if (runInfo != null && !runInfo.isEmpty()) {
                    validationRun.setRunInfo(runInfo);
                }
                // Done
                return null;
            }
        };
    }

    private <T> T get(Map<String, T> map, String field) {
        T value = map.get(field);
        if (value == null) {
            throw new IllegalArgumentException("Missing field " + field);
        } else {
            return value;
        }
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
