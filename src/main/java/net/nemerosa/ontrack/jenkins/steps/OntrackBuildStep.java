package net.nemerosa.ontrack.jenkins.steps;

import hudson.AbortException;
import hudson.Extension;
import hudson.model.TaskListener;
import net.nemerosa.ontrack.dsl.Branch;
import net.nemerosa.ontrack.dsl.Ontrack;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLConnector;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepExecutionImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;

/**
 * Steps to create a build
 */
@SuppressWarnings("unused")
public class OntrackBuildStep extends AbstractStepImpl {

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
    public OntrackBuildStep(@Nonnull String project, @Nonnull String branch, @Nonnull String build) {
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

    @Extension
    public static class DescriptorImpl extends AbstractStepDescriptorImpl {

        public DescriptorImpl() {
            super(Execution.class);
        }

        @Override
        public String getFunctionName() {
            return "ontrackBuild";
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return "Creates an Ontrack build";
        }
    }

    private static class Execution extends AbstractStepExecutionImpl {

        private final OntrackBuildStep step;

        public Execution(OntrackBuildStep step) {
            this.step = step;
        }

        @Override
        public boolean start() throws Exception {
            // Checks
            if (StringUtils.isBlank(step.project) || StringUtils.isBlank(step.branch) || StringUtils.isBlank(step.build)) {
                throw new AbortException("Ontrack build not created. All mandatory properties must be supplied ('project', 'branch', 'build').");
            }
            // Expansion
            String projectName = step.getProject();
            @SuppressWarnings("ConstantConditions")
            String branchName = step.getBranch();
            @SuppressWarnings("ConstantConditions")
            String buildName = step.getBuild();
            // Build description
            String buildDescription = String.format("Build %s", buildName);
            // Gets the Ontrack connector
            Ontrack ontrack = OntrackDSLConnector.createOntrackConnector(getContext().get(TaskListener.class));
            // Gets the branch...
            Branch branch = ontrack.branch(projectName, branchName);
            // ... and creates a build
            branch.build(buildName, buildDescription, true);
            // OK
            return true;
        }

        @Override
        public void stop(@Nonnull Throwable cause) throws Exception {
        }
    }
}
