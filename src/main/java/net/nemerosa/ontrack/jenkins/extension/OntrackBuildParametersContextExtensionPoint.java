package net.nemerosa.ontrack.jenkins.extension;

import antlr.ANTLRException;
import hudson.Extension;
import javaposse.jobdsl.dsl.helpers.BuildParametersContext;
import javaposse.jobdsl.plugin.ContextExtensionPoint;
import javaposse.jobdsl.plugin.DslExtensionMethod;
import net.nemerosa.ontrack.jenkins.OntrackChoiceParameterDefinition;

@Extension(optional = true)
public class OntrackBuildParametersContextExtensionPoint extends ContextExtensionPoint {

    /**
     * Choice parameter
     */
    @DslExtensionMethod(context = BuildParametersContext.class)
    public OntrackChoiceParameterDefinition ontrackChoiceParameter(Runnable closure) throws ANTLRException {
        OntrackChoiceParameterContext context = new OntrackChoiceParameterContext();
        executeInContext(closure, context);
        context.validate();
        return new OntrackChoiceParameterDefinition(
                context.getName(),
                context.getDescription(),
                context.getDsl(),
                context.isSandbox(),
                context.getValueProperty()
        );
    }

}
