package net.nemerosa.ontrack.jenkins;

import antlr.ANTLRException;
import hudson.Extension;
import hudson.model.Cause;
import hudson.model.Item;
import hudson.model.Job;
import hudson.model.Result;
import hudson.triggers.Trigger;
import hudson.triggers.TriggerDescriptor;
import net.nemerosa.ontrack.dsl.Ontrack;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLConnector;
import net.nemerosa.ontrack.jenkins.trigger.JenkinsTriggerJob;
import net.nemerosa.ontrack.jenkins.trigger.TriggerDefinition;
import net.nemerosa.ontrack.jenkins.trigger.TriggerHelper;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Trigger based on builds and promotions.
 */
public class OntrackTrigger extends Trigger<Job> {

    private static final Logger LOGGER = Logger.getLogger(OntrackTrigger.class.getName());

    private static final Level LOG_LEVEL = Level.FINE;
    public static final String SUCCESS = TriggerDefinition.SUCCESS;
    public static final String FAILURE = "FAILURE";
    public static final String UNSTABLE = "UNSTABLE";

    /**
     * Ontrack project name
     */
    private final String project;

    /**
     * Ontrack branch name
     */
    private final String branch;

    /**
     * The Ontrack promotion level to take into account.
     */
    private final String promotion;

    /**
     * Name of the parameter which contains the name of the build
     */
    private final String parameterName;

    /**
     * Minimum result of previous run
     */
    private final String minimumResult;

    /**
     * Constructor.
     *
     * @param spec          CRON specification
     * @param project       Ontrack project
     * @param branch        Ontrack branch
     * @param promotion     Ontrack promotion
     * @param parameterName Name of the parameter which contains the name of the build
     * @param minimumResult Minimum Result of the previous build
     * @throws ANTLRException If CRON expression is not correct
     */
    @DataBoundConstructor
    public OntrackTrigger(String spec, String project, String branch, String promotion, String parameterName, String minimumResult) throws ANTLRException {
        super(spec);
        this.project = project;
        this.branch = branch;
        this.promotion = promotion;
        this.parameterName = parameterName;
        // First we parse the given String 'minimumResult'
        // Hence 'minimumResult' will be
        // - 'SUCCESS' if the input is null or an empty String
        // - 'FAILURE' if the input contains an invalid value
        this.minimumResult = (minimumResult != null && !minimumResult.isEmpty()) ? Result.fromString(minimumResult).toString() : SUCCESS;
    }

    public String getProject() {
        return project;
    }

    public String getBranch() {
        return branch;
    }

    public String getPromotion() {
        return promotion;
    }

    @SuppressWarnings("unused")
    public String getParameterName() {
        return parameterName;
    }

    public String getMinimumResult() {
        return minimumResult;
    }

    @SuppressWarnings("unused")
    public List<String> getChoices() {
        List<String> list = new ArrayList<>();
        list.add(SUCCESS);
        list.add(UNSTABLE);
        list.add(FAILURE);
        return list;
    }

    @Override
    public void run() {
        // Checks for null
        if (job == null) {
            LOGGER.log(Level.WARNING, "[ontrack][trigger] Cannot run because of job being null.");
            return;
        }
        // Logging
        LOGGER.log(LOG_LEVEL, String.format("[ontrack][trigger][%s] Check %s promotion trigger", job.getFullName(), promotion));

        // Ontrack accessor
        Ontrack ontrack = OntrackDSLConnector.createOntrackConnector(System.out);

        // Helper
        TriggerHelper.evaluate(ontrack, new JenkinsTriggerJob(job), Collections.singletonList(
                new TriggerDefinition(
                        project,
                        branch,
                        promotion,
                        parameterName,
                        minimumResult
                )
        ));

    }

    @Extension
    @Symbol("ontrackTrigger")
    public static class DescriptorImpl extends TriggerDescriptor {

        @Override
        public boolean isApplicable(Item item) {
            return item instanceof Job;
        }

        @Override
        @Nonnull
        public String getDisplayName() {
            return "Ontrack: trigger";
        }
    }

    /**
     * Kept for backward compatibility
     */
    @Deprecated
    public static class OntrackTriggerCause extends Cause {
        @Override
        public String getShortDescription() {
            return "Triggered by Ontrack.";
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof OntrackTriggerCause;
        }

    }
}
