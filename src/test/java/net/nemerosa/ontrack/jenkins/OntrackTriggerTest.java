package net.nemerosa.ontrack.jenkins;

import antlr.ANTLRException;
import hudson.model.Result;
import org.junit.Test;

public class OntrackTriggerTest {

    public static final String VALID_SPEC = "H H(2-5) * * *";
    public static final String INVALID_SPEC = "H H(2-5)";
    public static final String PROJECT = "project";
    public static final String BRANCH = "branch";
    public static final String PROMOTION = "promotion";
    public static final String PARAMETER_NAME = "parameterName";

    @Test(expected = ANTLRException.class)
    public void invalid_cron_expression_results_in_exception() throws ANTLRException{
        OntrackTrigger trigger = new OntrackTrigger(
                INVALID_SPEC,
                PROJECT,
                BRANCH,
                PROMOTION,
                PARAMETER_NAME,
                null
        );
    }

    @Test
    public void default_result_is_success_for_null() {
        OntrackTrigger trigger = null;
        try {
            trigger = new OntrackTrigger(
                    VALID_SPEC,
                    PROJECT,
                    BRANCH,
                    PROMOTION,
                    PARAMETER_NAME,
                    null
            );
        } catch (ANTLRException e) {

            e.printStackTrace();
        }
        assert trigger.getMinimumResult() == Result.SUCCESS;
    }

    @Test
    public void default_result_is_success_for_empty() {
        OntrackTrigger trigger = null;
        try {
            trigger = new OntrackTrigger(
                    VALID_SPEC,
                    PROJECT,
                    BRANCH,
                    PROMOTION,
                    PARAMETER_NAME,
                    ""
            );
        } catch (ANTLRException e) {

            e.printStackTrace();
        }
        assert trigger.getMinimumResult() == Result.SUCCESS;
    }

    @Test
    public void invalid_result_becomes_failure() {
        OntrackTrigger trigger = null;
        try {
            trigger = new OntrackTrigger(
                    VALID_SPEC,
                    PROJECT,
                    BRANCH,
                    PROMOTION,
                    PARAMETER_NAME,
                    "BROL"
            );
        } catch (ANTLRException e) {

            e.printStackTrace();
        }
        assert trigger.getMinimumResult() == Result.FAILURE;
    }

    @Test
    public void valid_result_is_used() {
        OntrackTrigger trigger = null;
        try {
            trigger = new OntrackTrigger(
                    VALID_SPEC,
                    PROJECT,
                    BRANCH,
                    PROMOTION,
                    PARAMETER_NAME,
                    "UNSTABLE"
            );
        } catch (ANTLRException e) {

            e.printStackTrace();
        }
        assert trigger.getMinimumResult() == Result.UNSTABLE;
    }
}
