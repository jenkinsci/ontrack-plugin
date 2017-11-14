package net.nemerosa.ontrack.jenkins.extension;

import hudson.Extension;
import javaposse.jobdsl.dsl.helpers.publisher.PublisherContext;
import javaposse.jobdsl.plugin.ContextExtensionPoint;
import javaposse.jobdsl.plugin.DslExtensionMethod;
import net.nemerosa.ontrack.jenkins.OntrackBuildNotifier;
import net.nemerosa.ontrack.jenkins.OntrackDSLNotifier;
import net.nemerosa.ontrack.jenkins.OntrackPromotedRunNotifier;
import net.nemerosa.ontrack.jenkins.OntrackValidationRunNotifier;

@Extension(optional = true)
public class OntrackPublisherContextExtensionPoint extends ContextExtensionPoint {

    /**
     * Creation of a build
     */
    @DslExtensionMethod(context = PublisherContext.class)
    public OntrackBuildNotifier ontrackBuild(String project, String branch, String build) {
        return new OntrackBuildNotifier(project, branch, build, false);
    }

    /**
     * Creation of a build with ignore failure option
     */
    @DslExtensionMethod(context = PublisherContext.class)
    public OntrackBuildNotifier ontrackBuild(String project, String branch, String build, boolean ignoreFailure) {
        return new OntrackBuildNotifier(project, branch, build, ignoreFailure);
    }

    /**
     * Promotion
     */
    @DslExtensionMethod(context = PublisherContext.class)
    public OntrackPromotedRunNotifier ontrackPromotion(String project, String branch, String build, String promotionLevel) {
        return new OntrackPromotedRunNotifier(project, branch, build, promotionLevel, false);
    }

    /**
     * Promotion with ignoring of the failure option
     */
    @DslExtensionMethod(context = PublisherContext.class)
    public OntrackPromotedRunNotifier ontrackPromotion(String project, String branch, String build, String promotionLevel, boolean ignoreFailure) {
        return new OntrackPromotedRunNotifier(project, branch, build, promotionLevel, ignoreFailure);
    }

    /**
     * Validation
     */
    @DslExtensionMethod(context = PublisherContext.class)
    public OntrackValidationRunNotifier ontrackValidation(String project, String branch, String build, String validationStamp) {
        return new OntrackValidationRunNotifier(project, branch, build, validationStamp, false);
    }

    /**
     * Validation with ignoring of the failure option
     */
    @DslExtensionMethod(context = PublisherContext.class)
    public OntrackValidationRunNotifier ontrackValidation(String project, String branch, String build, String validationStamp, boolean ignoreFailure) {
        return new OntrackValidationRunNotifier(project, branch, build, validationStamp, ignoreFailure);
    }

    /**
     * DSL
     */
    @DslExtensionMethod(context = PublisherContext.class)
    public OntrackDSLNotifier ontrackDsl(Runnable closure) {
        OntrackDslContext context = new OntrackDslContext();
        executeInContext(closure, context);
        return new OntrackDSLNotifier(
                context.getScriptLocation(),
                context.isSandbox(),
                context.getInjectEnvironment(),
                context.getInjectProperties(),
                context.isLog(),
                context.isIgnoreFailure());
    }

}
