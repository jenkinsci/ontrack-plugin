package net.nemerosa.ontrack.jenkins.extension;

import hudson.Extension;
import javaposse.jobdsl.dsl.helpers.BuildParametersContext;
import javaposse.jobdsl.plugin.ContextExtensionPoint;
import javaposse.jobdsl.plugin.DslExtensionMethod;
import net.nemerosa.ontrack.jenkins.OntrackChoiceParameterDefinition;
import net.nemerosa.ontrack.jenkins.OntrackMultiChoiceParameterDefinition;
import net.nemerosa.ontrack.jenkins.OntrackSingleParameterDefinition;

@Extension(optional = true)
public class OntrackBuildParametersContextExtensionPoint extends ContextExtensionPoint {

    /**
     * Choice parameter
     */
    @DslExtensionMethod(context = BuildParametersContext.class)
    public OntrackChoiceParameterDefinition ontrackChoiceParameter(Runnable closure) {
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

    /**
     * Multiple choice parameter
     */
    @DslExtensionMethod(context = BuildParametersContext.class)
    public OntrackMultiChoiceParameterDefinition ontrackMultipleChoiceParameter(Runnable closure) {
        OntrackChoiceParameterContext context = new OntrackChoiceParameterContext();
        executeInContext(closure, context);
        context.validate();
        return new OntrackMultiChoiceParameterDefinition(
                context.getName(),
                context.getDescription(),
                context.getDsl(),
                context.isSandbox(),
                context.getValueProperty()
        );
    }

    /**
     * Single parameter
     */
    @DslExtensionMethod(context = BuildParametersContext.class)
    public OntrackSingleParameterDefinition ontrackSingleParameter(Runnable closure) {
        OntrackChoiceParameterContext context = new OntrackChoiceParameterContext();
        executeInContext(closure, context);
        context.validate();
        return new OntrackSingleParameterDefinition(
                context.getName(),
                context.getDescription(),
                context.getDsl(),
                context.isSandbox(),
                context.getValueProperty()
        );
    }

}