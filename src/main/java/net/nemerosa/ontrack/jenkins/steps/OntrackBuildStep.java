package net.nemerosa.ontrack.jenkins.steps;

import com.google.common.collect.ImmutableSet;
import hudson.AbortException;
import hudson.Extension;
import hudson.model.TaskListener;
import net.nemerosa.ontrack.dsl.Branch;
import net.nemerosa.ontrack.dsl.Build;
import net.nemerosa.ontrack.dsl.Ontrack;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLConnector;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.workflow.actions.TimingAction;
import org.jenkinsci.plugins.workflow.cps.nodes.StepStartNode;
import org.jenkinsci.plugins.workflow.graph.FlowNode;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
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
                // Gets the Ontrack connector
                Ontrack ontrack = OntrackDSLConnector.createOntrackConnector(context.get(TaskListener.class));
                // Gets the branch...
                Branch ontrackBranch = ontrack.branch(project, branch);
                // ... and creates a build
                Build ontrackBuild = ontrackBranch.build(OntrackBuildStep.this.build, buildDescription, true);
                // ... and associates a Git commit
                if (StringUtils.isNotBlank(gitCommit)) {
                    ontrackBuild.getConfig().gitCommit(gitCommit);
                }
                // Collecting run info
                Map<String, Object> runInfo = new HashMap<>();
                // Gets the (current) duration of the stage
                FlowNode flowNode = context.get(FlowNode.class);
                if (flowNode != null) {
                    Long durationSeconds = getTiming(flowNode);
                    // TODO Cause
                    // TODO URL
                    if (durationSeconds != null) {
                        runInfo.put("runTime", durationSeconds);
                    }
                }
                // If not empty, send the runtime
                if (!runInfo.isEmpty()) {
                    ontrackBuild.setRunInfo(runInfo);
                }
                // Done
                return null;
            }
        };
    }

    // org.jenkinsci.plugins.workflow.support.steps.StageStep
    protected Long getTiming(FlowNode node) {
        if (node instanceof StepStartNode) {
            StepStartNode stepNode = (StepStartNode) node;
            StepDescriptor stepDescriptor = stepNode.getDescriptor();
            if (stepDescriptor != null) {
                String stepDescriptorId = stepDescriptor.getId();
                if ("org.jenkinsci.plugins.workflow.support.steps.StageStep".equals(stepDescriptorId)) {
                    TimingAction timingAction = node.getAction(TimingAction.class);
                    if (timingAction != null) {
                        long startTime = timingAction.getStartTime();
                        return (System.currentTimeMillis() - startTime) / 1000;
                    }
                }
            }
        }
        return getTiming(node.getParents());
    }

    private Long getTiming(List<FlowNode> nodes) {
        for (FlowNode node : nodes) {
            Long durationSeconds = getTiming(node);
            if (durationSeconds != null) {
                return durationSeconds;
            }
        }
        return null;
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
