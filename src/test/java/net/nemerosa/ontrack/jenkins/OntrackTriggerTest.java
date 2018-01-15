package net.nemerosa.ontrack.jenkins;

import antlr.ANTLRException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OntrackTriggerTest {

    private static final String VALID_SPEC = "H H(2-5) * * *";
    private static final String INVALID_SPEC = "H H(2-5)";
    private static final String PROJECT = "project";
    private static final String BRANCH = "branch";
    private static final String PROMOTION = "promotion";
    private static final String PARAMETER_NAME = "parameterName";

    @Test(expected = ANTLRException.class)
    public void invalid_cron_expression_results_in_exception() throws ANTLRException {
        new OntrackTrigger(
                INVALID_SPEC,
                PROJECT,
                BRANCH,
                PROMOTION,
                PARAMETER_NAME,
                null
        );
    }

    @Test
    public void default_result_is_success_for_null() throws ANTLRException {
        OntrackTrigger trigger = new OntrackTrigger(
                VALID_SPEC,
                PROJECT,
                BRANCH,
                PROMOTION,
                PARAMETER_NAME,
                null
        );
        assertEquals("SUCCESS", trigger.getMinimumResult());
    }

    @Test
    public void default_result_is_success_for_empty() throws ANTLRException {
        OntrackTrigger trigger = new OntrackTrigger(
                VALID_SPEC,
                PROJECT,
                BRANCH,
                PROMOTION,
                PARAMETER_NAME,
                ""
        );
        assertEquals("SUCCESS", trigger.getMinimumResult());
    }

    @Test
    public void invalid_result_becomes_failure() throws ANTLRException {
        OntrackTrigger trigger = new OntrackTrigger(
                VALID_SPEC,
                PROJECT,
                BRANCH,
                PROMOTION,
                PARAMETER_NAME,
                "BROL"
        );
        assertEquals("FAILURE", trigger.getMinimumResult());
    }

    @Test
    public void valid_result_is_used() throws ANTLRException {
        OntrackTrigger trigger = new OntrackTrigger(
                VALID_SPEC,
                PROJECT,
                BRANCH,
                PROMOTION,
                PARAMETER_NAME,
                "UNSTABLE"
        );
        assertEquals("UNSTABLE", trigger.getMinimumResult());
    }
}
