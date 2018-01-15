package net.nemerosa.ontrack.jenkins.extension;

import net.nemerosa.ontrack.jenkins.OntrackTrigger;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OntrackTriggerContextExtensionPointTest {

    public static final String VALID_SPEC = "H H(2-5) * * *";
    public static final String INVALID_SPEC = "H H(2-5)";
    public static final String PROJECT = "project";
    public static final String BRANCH = "branch";
    public static final String PROMOTION = "promotion";
    public static final String PARAMETER_NAME = "parameterName";

    @Test
    public void minimumResultIsFailureForInvalidInput() throws Exception {
        testMinimumResult("BROL", OntrackTrigger.FAILURE);
    }

    @Test
    public void minimumResultIsSuccessForEmptyInput() throws Exception {
        testMinimumResult("", OntrackTrigger.SUCCESS);
    }

    @Test
    public void minimumResultIsSuccessForNullInput() throws Exception {
        testMinimumResult(null, OntrackTrigger.SUCCESS);
    }

    @Test
    public void useValidValuesForMinimumResult() throws Exception {
        testMinimumResult(OntrackTrigger.SUCCESS, OntrackTrigger.SUCCESS);
        testMinimumResult(OntrackTrigger.UNSTABLE, OntrackTrigger.UNSTABLE);
        testMinimumResult(OntrackTrigger.FAILURE, OntrackTrigger.FAILURE);
    }

    public void testMinimumResult(String givenResult, String expectedResult) throws Exception {
        OntrackTriggerContextExtensionPoint extensionPoint = new OntrackTriggerContextExtensionPoint();
        OntrackTrigger trigger = extensionPoint.ontrackTrigger(
                VALID_SPEC,
                PROJECT,
                BRANCH,
                PROMOTION,
                PARAMETER_NAME,
                givenResult
        );
        assertEquals(expectedResult, trigger.getMinimumResult());
    }

}
