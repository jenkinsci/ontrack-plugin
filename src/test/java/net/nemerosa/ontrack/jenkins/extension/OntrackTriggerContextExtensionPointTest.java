package net.nemerosa.ontrack.jenkins.extension;

import antlr.ANTLRException;
import hudson.model.Result;
import net.nemerosa.ontrack.jenkins.OntrackTrigger;
import org.junit.Test;

public class OntrackTriggerContextExtensionPointTest {

    public static final String VALID_SPEC = "H H(2-5) * * *";
    public static final String INVALID_SPEC = "H H(2-5)";
    public static final String PROJECT = "project";
    public static final String BRANCH = "branch";
    public static final String PROMOTION = "promotion";
    public static final String PARAMETER_NAME = "parameterName";

    @Test
    public void set_custom_minimum_result(){
        OntrackTriggerContextExtensionPoint extensionPoint = new OntrackTriggerContextExtensionPoint();
        OntrackTrigger trigger = null;
        try {
            trigger = extensionPoint.ontrackTrigger(
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


    @Test
    public void invalid_value_results_in_failure(){
        OntrackTriggerContextExtensionPoint extensionPoint = new OntrackTriggerContextExtensionPoint();
        OntrackTrigger trigger = null;
        try {
            trigger = extensionPoint.ontrackTrigger(
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
    public void default_value_is_success_for_null(){
        OntrackTriggerContextExtensionPoint extensionPoint = new OntrackTriggerContextExtensionPoint();
        OntrackTrigger trigger = null;
        try {
            trigger = extensionPoint.ontrackTrigger(
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
    public void default_value_is_success_for_empty(){
        OntrackTriggerContextExtensionPoint extensionPoint = new OntrackTriggerContextExtensionPoint();
        OntrackTrigger trigger = null;
        try {
            trigger = extensionPoint.ontrackTrigger(
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
    public void default_value_is_success_if_missing_parameter(){
        OntrackTriggerContextExtensionPoint extensionPoint = new OntrackTriggerContextExtensionPoint();
        OntrackTrigger trigger = null;
        try {
            trigger = extensionPoint.ontrackTrigger(
                    VALID_SPEC,
                    PROJECT,
                    BRANCH,
                    PROMOTION,
                    PARAMETER_NAME
            );
        } catch (ANTLRException e) {
            e.printStackTrace();
        }
        assert trigger.getMinimumResult() == Result.SUCCESS;
    }
}
