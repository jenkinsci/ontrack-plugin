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
     *
     * @param project Name of the project
     * @param branch  Name of the branch
     * @param build   Name of the build
     * @return Step
     */
    @DslExtensionMethod(context = PublisherContext.class)
    public OntrackBuildNotifier ontrackBuild(String project, String branch, String build) {
        return new OntrackBuildNotifier(project, branch, build, false, false);
    }

    /**
     * Creation of a build with ignore failure option
     *
     * @param project       Name of the project
     * @param branch        Name of the branch
     * @param build         Name of the build
     * @param ignoreFailure If failures must be ignored
     * @return Step
     */
    @DslExtensionMethod(context = PublisherContext.class)
    public OntrackBuildNotifier ontrackBuild(String project, String branch, String build, boolean ignoreFailure) {
        return new OntrackBuildNotifier(project, branch, build, ignoreFailure, false);
    }

    /**
     * Creation of a build with ignore failure and run info option
     *
     * @param project       Name of the project
     * @param branch        Name of the branch
     * @param build         Name of the build
     * @param ignoreFailure If failures must be ignored
     * @param runInfo       If run info must be collected
     * @return Step
     */
    @DslExtensionMethod(context = PublisherContext.class)
    public OntrackBuildNotifier ontrackBuild(String project, String branch, String build, boolean ignoreFailure, boolean runInfo) {
        return new OntrackBuildNotifier(project, branch, build, ignoreFailure, runInfo);
    }

    /**
     * Promotion
     *
     * @param project        Name of the project
     * @param branch         Name of the branch
     * @param build          Name of the build
     * @param promotionLevel Name of the promotion level
     * @return Step
     */
    @DslExtensionMethod(context = PublisherContext.class)
    public OntrackPromotedRunNotifier ontrackPromotion(String project, String branch, String build, String promotionLevel) {
        return new OntrackPromotedRunNotifier(project, branch, build, promotionLevel, false);
    }

    /**
     * Promotion with ignoring of the failure option
     *
     * @param project        Name of the project
     * @param branch         Name of the branch
     * @param build          Name of the build
     * @param promotionLevel Name of the promotion level
     * @param ignoreFailure  If failures must be ignored
     * @return Step
     */
    @DslExtensionMethod(context = PublisherContext.class)
    public OntrackPromotedRunNotifier ontrackPromotion(String project, String branch, String build, String promotionLevel, boolean ignoreFailure) {
        return new OntrackPromotedRunNotifier(project, branch, build, promotionLevel, ignoreFailure);
    }

    /**
     * Validation
     *
     * @param project         Name of the project
     * @param branch          Name of the branch
     * @param build           Name of the build
     * @param validationStamp Name of the validation stamp
     * @return Step
     */
    @DslExtensionMethod(context = PublisherContext.class)
    public OntrackValidationRunNotifier ontrackValidation(String project, String branch, String build, String validationStamp) {
        return new OntrackValidationRunNotifier(project, branch, build, validationStamp, false, false);
    }

    /**
     * Validation with ignoring of the failure option
     *
     * @param project         Name of the project
     * @param branch          Name of the branch
     * @param build           Name of the build
     * @param validationStamp Name of the validation stamp
     * @param ignoreFailure   If failures must be ignored
     * @return Step
     */
    @DslExtensionMethod(context = PublisherContext.class)
    public OntrackValidationRunNotifier ontrackValidation(String project, String branch, String build, String validationStamp, boolean ignoreFailure) {
        return new OntrackValidationRunNotifier(project, branch, build, validationStamp, ignoreFailure, false);
    }

    /**
     * Validation with ignoring of the failure and run info options
     *
     * @param project         Name of the project
     * @param branch          Name of the branch
     * @param build           Name of the build
     * @param validationStamp Name of the validation stamp
     * @param ignoreFailure   If failures must be ignored
     * @param runInfo       If run info must be collected
     * @return Step
     */
    @DslExtensionMethod(context = PublisherContext.class)
    public OntrackValidationRunNotifier ontrackValidation(String project, String branch, String build, String validationStamp, boolean ignoreFailure, boolean runInfo) {
        return new OntrackValidationRunNotifier(project, branch, build, validationStamp, ignoreFailure, runInfo);
    }

    /**
     * DSL
     *
     * @param closure Closure running in the context of {@link OntrackDslContext}
     * @return Step
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
