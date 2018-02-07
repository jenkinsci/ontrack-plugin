package net.nemerosa.ontrack.jenkins;

import antlr.ANTLRException;
import com.google.common.collect.ImmutableMap;
import hudson.Extension;
import hudson.model.*;
import hudson.triggers.Trigger;
import hudson.triggers.TriggerDescriptor;
import hudson.util.LogTaskListener;
import net.nemerosa.ontrack.dsl.Branch;
import net.nemerosa.ontrack.dsl.Build;
import net.nemerosa.ontrack.dsl.Ontrack;
import net.nemerosa.ontrack.jenkins.dsl.OntrackDSLConnector;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Trigger based on builds and promotions.
 */
public class OntrackTrigger extends Trigger<AbstractProject> {

    private static final Logger LOGGER = Logger.getLogger(OntrackTrigger.class.getName());

    private static final Level LOG_LEVEL = Level.FINE;
    public static final String SUCCESS = "SUCCESS";
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
        this.minimumResult = (minimumResult!=null&&!minimumResult.isEmpty())?Result.fromString(minimumResult).toString():SUCCESS;
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

    public String getParameterName() {
        return parameterName;
    }

    public String getMinimumResult() {
        return minimumResult;
    }

    public List<String> getChoices(){
        List<String> list = new ArrayList<>();
        list.add(SUCCESS);
        list.add(UNSTABLE);
        list.add(FAILURE);
        return list;
    }

    @Override
    public void run() {
        // Logging
        LOGGER.log(LOG_LEVEL, String.format("[ontrack][trigger][%s] Check %s promotion trigger", job.getFullName(), promotion));

        // Ontrack accessor
        Ontrack ontrack = OntrackDSLConnector.createOntrackConnector(System.out);

        // Gets the Ontrack branch
        Branch ontrackBranch = ontrack.branch(project, this.branch);

        // Gets the last builds
        List<Build> ontrackBuilds;
        if (StringUtils.isBlank(promotion)) {
            ontrackBuilds = ontrackBranch.standardFilter(ImmutableMap.of(
                    "count", 1
            ));
        }
        // Gets the last promoted build
        else if ("*".equals(promotion)) {
            ontrackBuilds = ontrackBranch.getLastPromotedBuilds();
        }
        // Gets the last build with promotion
        else {
            ontrackBuilds = ontrackBranch.standardFilter(ImmutableMap.of(
                    "count", 1,
                    "withPromotionLevel", promotion
            ));
        }

        // Nothing eligible
        if (ontrackBuilds.isEmpty()) {
            LOGGER.log(LOG_LEVEL, String.format("[ontrack][trigger][%s] No build eligible", job.getFullName()));
            return;
        }

        // Gets the last version
        String lastVersion = ontrackBuilds.get(0).getName();
        LOGGER.log(LOG_LEVEL, String.format("[ontrack][trigger][%s] Last available build: %s", job.getFullName(), lastVersion));

        // Version parameter name
        String parameterNameValue = this.parameterName;
        if (StringUtils.isBlank(parameterNameValue)) {
            parameterNameValue = "VERSION";
        }

        // Firing the job?
        boolean firing;
        // Gets any previous build
        Run lastBuild = job.getLastBuild();
        if (lastBuild != null) {
            Result result = lastBuild.getResult();
            Result minimum = Result.fromString(minimumResult);
            if (result == null || (result.isWorseThan(minimum) && result.isCompleteBuild())) {
                LOGGER.log(LOG_LEVEL, String.format("[ontrack][trigger][%s] Last build was failed or unsuccessful", job.getFullName()));
                firing = true;
            } else {
                // Log listener
                TaskListener taskListener = new LogTaskListener(
                        LOGGER,
                        Level.FINER
                );
                // Gets the last build name
                try {
                    String lastBuildName = lastBuild.getEnvironment(taskListener).get(parameterNameValue, null);
                    LOGGER.log(LOG_LEVEL, String.format("[ontrack][trigger][%s] Version for last build: %s", job.getFullName(), lastBuildName));
                    // Identical to last version
                    if (StringUtils.equals(lastBuildName, lastVersion)) {
                        LOGGER.log(LOG_LEVEL, String.format("[ontrack][trigger][%s] No new version available", job.getFullName()));
                        firing = false;
                    }
                    // Not equal
                    else {
                        LOGGER.log(LOG_LEVEL, String.format("[ontrack][trigger][%s] New version available", job.getFullName()));
                        firing = true;
                    }
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(
                            String.format("[ontrack][trigger][%s] Could not compute the trigger condition because %s environment variable could not be accessed", job.getFullName(), parameterNameValue),
                            e
                    );
                }
            }
        } else {
            LOGGER.log(LOG_LEVEL, String.format("[ontrack][trigger][%s] No previous build, firing", job.getFullName()));
            firing = true;
        }

        // Summary
        if (!firing) {
            LOGGER.log(LOG_LEVEL, String.format("[ontrack][trigger][%s] For one of the reasons mentioned above, not firing", job.getFullName()));
        } else {
            LOGGER.log(LOG_LEVEL, String.format("[ontrack][trigger][%s] Firing with %s = %s", job.getFullName(), parameterNameValue, lastVersion));
            // Scheduling
            job.scheduleBuild2(
                    0,
                    new OntrackTriggerCause(),
                    new ParametersAction(
                            new StringParameterValue(parameterNameValue, lastVersion)
                    )
            );
        }

    }

    @Extension
    public static class DescriptorImpl extends TriggerDescriptor {

        @Override
        public boolean isApplicable(Item item) {
            return item instanceof AbstractProject;
        }

        @Override
        public String getDisplayName() {
            return "Ontrack: trigger";
        }
    }

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
