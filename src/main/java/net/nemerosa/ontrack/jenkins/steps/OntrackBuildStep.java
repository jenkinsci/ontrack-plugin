package net.nemerosa.ontrack.jenkins.steps;

import com.google.common.collect.ImmutableSet;
import hudson.AbortException;
import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import net.nemerosa.ontrack.dsl.Branch;
import net.nemerosa.ontrack.dsl.Build;
import net.nemerosa.ontrack.jenkins.OntrackPluginSupport;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLConnector;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLFacade;
import net.nemerosa.ontrack.jenkins.dsl.facade.BranchFacade;
import net.nemerosa.ontrack.jenkins.dsl.facade.BuildFacade;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;

/**
 * Steps to create a build
 */
@SuppressWarnings("unused")
public class OntrackBuildStep extends Step {

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
     * Optional Git commit property to associate with the build
     */
    private String gitCommit;

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

    public String getGitCommit() {
        return gitCommit;
    }

    @DataBoundSetter
    public void setGitCommit(String gitCommit) {
        this.gitCommit = gitCommit;
    }

    @Override
    public StepExecution start(final StepContext context) throws Exception {
        // Checks
        if (StringUtils.isBlank(this.project) || StringUtils.isBlank(this.branch) || StringUtils.isBlank(this.build)) {
            throw new AbortException("Ontrack build not created. All mandatory properties must be supplied ('project', 'branch', 'build').");
        }
        // Build description
        final String buildDescription = String.format("Build %s", this.build);
        // OK
        return new SynchronousStepExecution<Void>(context) {
            @Override
            protected Void run() throws Exception {
                // Gets the current listener
                TaskListener taskListener = context.get(TaskListener.class);
                if (taskListener == null) {
                    throw new IllegalStateException("Cannot get any task listener.");
                }
                // Gets the current Jenkins build
                Run run = context.get(Run.class);
                if (run == null) {
                    throw new IllegalStateException("Cannot get any run.");
                }
                // Gets the Ontrack connector
                OntrackDSLFacade ontrack = OntrackDSLConnector.createOntrackConnector(taskListener);
                // Gets the branch...
                BranchFacade ontrackBranch = ontrack.branch(project, branch);
                // ... and creates a build
                BuildFacade ontrackBuild = ontrackBranch.build(OntrackBuildStep.this.build, buildDescription, true);
                // Ontrack build link
                OntrackPluginSupport.createOntrackLinks(ontrack, run, ontrackBuild);
                // ... and associates a Git commit
                if (StringUtils.isNotBlank(gitCommit)) {
                    ontrackBuild.setGitCommit(gitCommit);
                }
                // Collecting run info
                Map<String, ?> runInfo = OntrackStepHelper.getRunInfo(context, taskListener);
                // If not empty, send the runtime
                if (runInfo != null && !runInfo.isEmpty()) {
                    ontrackBuild.setRunInfo(runInfo);
                }
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
            return "ontrackBuild";
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return "Creates an Ontrack build";
        }
    }

}
