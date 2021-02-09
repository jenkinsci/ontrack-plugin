package net.nemerosa.ontrack.jenkins.extension;

import hudson.Extension;
import javaposse.jobdsl.dsl.helpers.BuildParametersContext;
import javaposse.jobdsl.plugin.ContextExtensionPoint;
import javaposse.jobdsl.plugin.DslExtensionMethod;
import net.nemerosa.ontrack.jenkins.OntrackChoiceParameterDefinition;
import net.nemerosa.ontrack.jenkins.OntrackMultiChoiceParameterDefinition;
import net.nemerosa.ontrack.jenkins.OntrackSingleParameterDefinition;
import org.apache.commons.lang.StringUtils;

import java.util.Collections;

import static java.lang.String.format;

@Extension(optional = true)
public class OntrackBuildParametersContextExtensionPoint extends ContextExtensionPoint {

    /**
     * Choice parameter
     *
     * @param closure Code to run. It runs in the context of {@link OntrackChoiceParameterContext}.
     * @return The definition
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
                context.getValueProperty(),
                context.getBindings()
        );
    }

    /**
     * Multiple choice parameter
     *
     * @param closure Code to run. It runs in the context of {@link OntrackChoiceParameterContext}.
     * @return The definition
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
                context.getValueProperty(),
                context.getBindings()
        );
    }

    /**
     * Single parameter
     *
     * @param closure Code to run. It runs in the context of {@link OntrackChoiceParameterContext}.
     * @return The definition
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
                context.getValueProperty(),
                context.getBindings()
        );
    }

    /**
     * Last parameter
     *
     * @param closure Code to run. It runs in the context of {@link OntrackChoiceParameterContext}.
     * @return The definition
     */
    @DslExtensionMethod(context = BuildParametersContext.class)
    public OntrackChoiceParameterDefinition ontrackBuildParameter(Runnable closure) {
        OntrackBuildParameterContext context = new OntrackBuildParameterContext();
        executeInContext(closure, context);
        context.validate();
        // Computes the DSL script
        // TODO #52 Use binding variables
        String dsl;
        if (StringUtils.isBlank(context.getPromotion())) {
            if (context.isUseLabel()) {
                dsl = format(
                        "ontrack.branch('%s', '%s').standardFilter(count: %d, withProperty: 'net.nemerosa.ontrack.extension.general.ReleasePropertyType').collect { build -> [value: build.config.label] }",
                        context.getProject(),
                        context.getBranch(),
                        context.getCount()
                );
            } else {
                dsl = format(
                        "ontrack.branch('%s', '%s').standardFilter(count: %d).collect { build -> [value: build.name] }",
                        context.getProject(),
                        context.getBranch(),
                        context.getCount()
                );
            }
        } else {
            if (context.isUseLabel()) {
                dsl = format(
                        "ontrack.branch('%s', '%s').standardFilter(count: %d, withProperty: 'net.nemerosa.ontrack.extension.general.ReleasePropertyType', withPromotionLevel: '%s').collect { build -> [value: build.config.label] }",
                        context.getProject(),
                        context.getBranch(),
                        context.getCount(),
                        context.getPromotion()
                );
            } else {
                dsl = format(
                        "ontrack.branch('%s', '%s').standardFilter(count: %d, withPromotionLevel: '%s').collect { build -> [value: build.name] }",
                        context.getProject(),
                        context.getBranch(),
                        context.getCount(),
                        context.getPromotion()
                );
            }
        }
        // Creates the component
        return new OntrackChoiceParameterDefinition(
                context.getName(),
                context.getDescription(),
                dsl,
                true,
                "value",
                Collections.emptyMap()
        );
    }

}
