package net.nemerosa.ontrack.jenkins.steps;

import com.google.common.collect.ImmutableSet;
import hudson.AbortException;
import hudson.Extension;
import hudson.model.TaskListener;
import net.nemerosa.ontrack.dsl.Branch;
import net.nemerosa.ontrack.dsl.Ontrack;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLConnector;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
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
                // Gets the Ontrack connector
                Ontrack ontrack = OntrackDSLConnector.createOntrackConnector(context.get(TaskListener.class));
                // Gets the branch...
                Branch ontrackBranch = ontrack.branch(project, branch);
                // ... and creates a build
                ontrackBranch.build(build, buildDescription, true);
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
