package net.nemerosa.ontrack.jenkins.extension;

import antlr.ANTLRException;
import hudson.model.Result;
import net.nemerosa.ontrack.jenkins.OntrackTrigger;
import org.junit.Test;

public class OntrackTriggerContextExtensionPointTest {

    @Test
    public void test(){
        OntrackTriggerContextExtensionPoint extensionPoint = new OntrackTriggerContextExtensionPoint();
        OntrackTrigger trigger = null;
        try {
            trigger = extensionPoint.ontrackTrigger(
                    "H H(2-5) * * *",
                    "project",
                    "name",
                    "promotion",
                    "parameterName",
                    "UNSTABLE"
            );
        } catch (ANTLRException e) {
            e.printStackTrace();
        }
        assert trigger.getMinimumResult() == Result.UNSTABLE;
    }
}
