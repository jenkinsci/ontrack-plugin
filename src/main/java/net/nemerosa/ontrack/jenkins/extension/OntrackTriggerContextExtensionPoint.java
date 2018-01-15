package net.nemerosa.ontrack.jenkins.extension;

import antlr.ANTLRException;
import hudson.Extension;
import javaposse.jobdsl.dsl.helpers.triggers.TriggerContext;
import javaposse.jobdsl.plugin.ContextExtensionPoint;
import javaposse.jobdsl.plugin.DslExtensionMethod;
import net.nemerosa.ontrack.jenkins.OntrackTrigger;

@Extension(optional = true)
public class OntrackTriggerContextExtensionPoint extends ContextExtensionPoint {

    /**
     * Trigger
     */
    @DslExtensionMethod(context = TriggerContext.class)
    public OntrackTrigger ontrackTrigger(String spec, String project, String branch, String promotion, String parameterName, String minimumResult) throws ANTLRException {
        return new OntrackTrigger(spec, project, branch, promotion, parameterName, minimumResult);
    }

    /**
     * Trigger
     */
    @DslExtensionMethod(context = TriggerContext.class)
    public OntrackTrigger ontrackTrigger(String spec, String project, String branch, String promotion, String parameterName) throws ANTLRException {
        return ontrackTrigger(spec, project, branch, promotion, parameterName, null);
    }

    /**
     * Trigger with default parameter name
     */
    @DslExtensionMethod(context = TriggerContext.class)
    public OntrackTrigger ontrackTrigger(String spec, String project, String branch, String promotion) throws ANTLRException {
        return ontrackTrigger(spec, project, branch, promotion, "VERSION");
    }

}
