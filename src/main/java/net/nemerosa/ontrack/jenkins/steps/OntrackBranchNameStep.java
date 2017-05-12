package net.nemerosa.ontrack.jenkins.steps;

import hudson.AbortException;
import hudson.Extension;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Set;

/**
 * Step to transform a branch name, as provided by the pipeline for example, into a name
 * suitable for a branch in Ontrack.
 */
@SuppressWarnings("unused")
public class OntrackBranchNameStep extends Step {

    /**
     * Default replacement sequence
     */
    private static final String BRANCH_CHARACTERS = "[^A-Za-z0-9._\\-]";

    /**
     * Name of the branch to create the build for
     */
    private final String branch;

    /**
     * Replacement expression
     */
    private String branchReplacement = "-";

    @DataBoundConstructor
    public OntrackBranchNameStep(@Nonnull String branch) {
        this.branch = branch;
    }

    public String getBranch() {
        return branch;
    }

    public String getBranchReplacement() {
        return branchReplacement;
    }

    @DataBoundSetter
    public void setBranchReplacement(String branchReplacement) {
        this.branchReplacement = branchReplacement;
    }

    @Override
    public StepExecution start(final StepContext context) throws Exception {
        // Checks
        if (StringUtils.isBlank(this.branch)) {
            throw new AbortException("Ontrack branch name not available. All mandatory properties must be supplied ('branch').");
        }
        // OK
        return new SynchronousStepExecution<String>(context) {
            @Override
            protected String run() throws Exception {
                return branch.replaceAll(BRANCH_CHARACTERS, branchReplacement);
            }
        };
    }

    @Extension
    public static class DescriptorImpl extends StepDescriptor {

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return Collections.emptySet();
        }

        @Override
        public String getFunctionName() {
            return "ontrackBranchName";
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return "Transforms a branch name, as provided by the pipeline for example, into a name " +
                    "suitable for a branch in Ontrack.";
        }
    }

}
