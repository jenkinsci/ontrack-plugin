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
     *
     * @param spec          Trigger spec
     * @param project       Name of the project
     * @param branch        Name of the branch
     * @param promotion     Name of the promotion
     * @param parameterName Name of the parameter that will contain the build name
     * @param minimumResult Jenkins result to check in the previous build
     * @return Trigger
     * @throws ANTLRException If the cron expression cannot be parsed
     */
    @DslExtensionMethod(context = TriggerContext.class)
    public OntrackTrigger ontrackTrigger(String spec, String project, String branch, String promotion, String parameterName, String minimumResult) throws ANTLRException {
        return new OntrackTrigger(spec, project, branch, promotion, parameterName, minimumResult);
    }

    /**
     * Trigger
     *
     * @param spec          Trigger spec
     * @param project       Name of the project
     * @param branch        Name of the branch
     * @param promotion     Name of the promotion
     * @param parameterName Name of the parameter that will contain the build name
     * @return Trigger
     * @throws ANTLRException If the cron expression cannot be parsed
     */
    @DslExtensionMethod(context = TriggerContext.class)
    public OntrackTrigger ontrackTrigger(String spec, String project, String branch, String promotion, String parameterName) throws ANTLRException {
        return ontrackTrigger(spec, project, branch, promotion, parameterName, null);
    }

    /**
     * Trigger with default parameter name
     *
     * @param spec      Trigger spec
     * @param project   Name of the project
     * @param branch    Name of the branch
     * @param promotion Name of the promotion
     * @return Trigger
     * @throws ANTLRException If the cron expression cannot be parsed
     */
    @DslExtensionMethod(context = TriggerContext.class)
    public OntrackTrigger ontrackTrigger(String spec, String project, String branch, String promotion) throws ANTLRException {
        return ontrackTrigger(spec, project, branch, promotion, "VERSION");
    }

}
