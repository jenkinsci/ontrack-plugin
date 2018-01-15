package net.nemerosa.ontrack.jenkins.extension;

import antlr.ANTLRException;
import net.nemerosa.ontrack.jenkins.OntrackTrigger;
import org.junit.Test;

public class OntrackTriggerContextExtensionPointTest {

    @Test
    public void minimumResultIsFailureForInvalidInput(){
        testMinimumResult("BROL", OntrackTrigger.FAILURE);
    }

    @Test
    public void minimumResultIsSuccessForEmptyInput(){
        testMinimumResult("", OntrackTrigger.SUCCESS);
    }

    @Test
    public void minimumResultIsSuccessForNullInput(){
        testMinimumResult(null, OntrackTrigger.SUCCESS);
    }

    @Test
    public void useValidValuesForMinimumResult(){
        testMinimumResult(OntrackTrigger.SUCCESS, OntrackTrigger.SUCCESS);
        testMinimumResult(OntrackTrigger.UNSTABLE, OntrackTrigger.UNSTABLE);
        testMinimumResult(OntrackTrigger.FAILURE, OntrackTrigger.FAILURE);
    }

    public void testMinimumResult(String givenResult, String expectedResult){
        OntrackTriggerContextExtensionPoint extensionPoint = new OntrackTriggerContextExtensionPoint();
        OntrackTrigger trigger = null;
        try {
            trigger = extensionPoint.ontrackTrigger(
                    "H H(2-5) * * *",
                    "project",
                    "name",
                    "promotion",
                    "parameterName",
                    givenResult
            );
        } catch (ANTLRException e) {
            e.printStackTrace();
        }
        assert trigger.getMinimumResult() == expectedResult;
    }
}
