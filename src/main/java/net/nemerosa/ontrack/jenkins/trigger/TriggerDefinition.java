package net.nemerosa.ontrack.jenkins.trigger;

import hudson.model.Result;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

public class TriggerDefinition {

    /**
     * Success condition for previous result.
     */
    public static final String SUCCESS = "SUCCESS";

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

    @DataBoundConstructor
    public TriggerDefinition(String project, String branch, String promotion, String parameterName, String minimumResult) {
        this.project = project;
        this.branch = branch;
        this.promotion = promotion;
        this.parameterName = StringUtils.isBlank(parameterName) ? "VERSION" : parameterName;
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

    public String getParameterName() {
        return parameterName;
    }

    public String getMinimumResult() {
        return minimumResult;
    }

    @Override
    public String toString() {
        return "TriggerDefinition{" +
                "project='" + project + '\'' +
                ", branch='" + branch + '\'' +
                ", promotion='" + promotion + '\'' +
                ", parameterName='" + parameterName + '\'' +
                ", minimumResult='" + minimumResult + '\'' +
                '}';
    }

    TriggerResult noResult() {
        return new TriggerResult(parameterName, null, null);
    }

    TriggerResult noPrevious(String newValue) {
        return new TriggerResult(parameterName, null, newValue);
    }

    TriggerResult withPrevious(String previousValue, String lastVersion) {
        return new TriggerResult(parameterName, previousValue, lastVersion);
    }
}
