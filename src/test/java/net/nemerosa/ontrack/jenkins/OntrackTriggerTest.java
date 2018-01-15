package net.nemerosa.ontrack.jenkins;

import antlr.ANTLRException;
import org.junit.Test;

public class OntrackTriggerTest {


    @Test(expected = ANTLRException.class)
    public void invalid_cron_expression_results_in_exception() throws ANTLRException{
        OntrackTrigger trigger = new OntrackTrigger(
                "H H(2-5)",
                "project",
                "branch",
                "promotion",
                "parameterName",
                null
        );
    }
      @Test
    public void default_result_is_success() {
        OntrackTrigger trigger = null;
        try {
            trigger = new OntrackTrigger(
                    "H H(2-5) * * *",
                    "project",
                    "branch",
                    "promotion",
                    "parameterName",
                    null
            );
        } catch (ANTLRException e) {

            e.printStackTrace();
        }
        assert trigger.getMinimumResult() == OntrackTrigger.SUCCESS;
    }


    @Test
    public void invalid_result_becomes_failure() {
        OntrackTrigger trigger = null;
        try {
            trigger = new OntrackTrigger(
                    "H H(2-5) * * *",
                    "project",
                    "branch",
                    "promotion",
                    "parameterName",
                    "BROL"
            );
        } catch (ANTLRException e) {

            e.printStackTrace();
        }
        assert trigger.getMinimumResult() == OntrackTrigger.FAILURE;
    }

    @Test
    public void valid_result_is_used() {
        OntrackTrigger trigger = null;
        try {
            trigger = new OntrackTrigger(
                    "H H(2-5) * * *",
                    "project",
                    "branch",
                    "promotion",
                    "parameterName",
                    "UNSTABLE"
            );
        } catch (ANTLRException e) {

            e.printStackTrace();
        }
        assert trigger.getMinimumResult() == OntrackTrigger.UNSTABLE;
    }
}
