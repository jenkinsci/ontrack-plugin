package net.nemerosa.ontrack.jenkins.extension;

import hudson.Extension;
import javaposse.jobdsl.dsl.helpers.step.StepContext;
import javaposse.jobdsl.plugin.ContextExtensionPoint;
import javaposse.jobdsl.plugin.DslExtensionMethod;
import net.nemerosa.ontrack.jenkins.OntrackDSLStep;

@Extension(optional = true)
public class OntrackStepContextExtensionPoint extends ContextExtensionPoint {

    /**
     * DSL
     *
     * @param closure Closure running in the context of {@link OntrackDslContext}
     * @return Step
     */
    @DslExtensionMethod(context = StepContext.class)
    public OntrackDSLStep ontrackDsl(Runnable closure) {
        OntrackDslContext context = new OntrackDslContext();
        executeInContext(closure, context);
        return new OntrackDSLStep(
                context.getScriptLocation(),
                context.isSandbox(),
                context.getInjectEnvironment(),
                context.getInjectProperties(),
                context.isLog(),
                context.isIgnoreFailure());
    }

}
